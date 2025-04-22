package com.repsy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.repsy.domain.Package;
import com.repsy.dto.MetadataDTO;
import com.repsy.repository.PackageRepository;
import com.repsy.storage.StorageLibrary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;


@Service
public class PackageServiceImpl implements PackageService {
    private final PackageRepository packageRepository;
    private final Validator validator;
    private final ObjectMapper objectMapper;
    StorageLibrary storageLibrary;
    private final String storageStrategy;

    public PackageServiceImpl(PackageRepository packageRepository, Validator validator,
                             ObjectMapper objectMapper, StorageLibrary storageLibrary,
                             @Value("${storage.strategy}") String storageStrategy) {

        if(!storageStrategy.equals("object-storage") && !storageStrategy.equals("file-system")){
            throw new IllegalArgumentException("Invalid storage strategy: " + storageStrategy);
        }

        this.packageRepository = packageRepository;
        this.validator = validator;
        this.objectMapper = objectMapper;
        this.storageLibrary=storageLibrary;
        this.storageStrategy= storageStrategy;
    }

    @Override
    public String uploadPackage(String packageName, String version, MultipartFile file, String metadataJson) throws JsonProcessingException {
        MetadataDTO metadata = objectMapper.readValue(metadataJson, MetadataDTO.class);
        BindingResult bindingResult = new BeanPropertyBindingResult(metadata,"metadata");
        validator.validate(metadata,bindingResult);

        if(bindingResult.hasErrors()){
            StringBuilder errors = new StringBuilder("Validation failed: ");
            bindingResult.getAllErrors().forEach(error ->
                    errors.append(error.getDefaultMessage()).append(", "));
            throw new IllegalArgumentException(errors.toString());
        }
        if(packageRepository.existsPackageByPackageNameAndVersion(packageName,version)){
            throw new IllegalArgumentException("Package already exists.");
        }

        com.repsy.domain.Package newPackage = new Package();
        newPackage.setPackageName(packageName);
        newPackage.setVersion(version);
        newPackage.setAuthor(metadata.getAuthor());
        newPackage.setStorageStrategy(storageStrategy);

        String storageLibraryResponse = storageLibrary.write(file,metadataJson);
        packageRepository.save(newPackage);


        return (storageLibraryResponse + "\nPackage uploaded successfully");
    }

    @Override
    public String downloadPackage(String packageName, String version) throws FileNotFoundException{
        String storageLibraryResponse = storageLibrary.read(packageName,version);
        Package foundPackage = packageRepository.findPackageByPackageNameAndVersion(packageName,version);
        if (foundPackage==null){
            throw new FileNotFoundException("Requested package is not found");
        }
        return (storageLibraryResponse + "\n" + foundPackage);
    }
}
