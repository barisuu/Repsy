package com.repsy.service;

import com.repsy.domain.Package;
import com.repsy.repository.PackageRepository;
import com.repsy.storage.MetadataDTO;
import com.repsy.storage.StorageLibrary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;


@Service
public class PackageServiceImpl implements PackageService {

    private final PackageRepository packageRepository;
    StorageLibrary storageLibrary;
    private final String storageStrategy;

    public PackageServiceImpl(PackageRepository packageRepository, StorageLibrary storageLibrary,
                             @Value("${storage.strategy}") String storageStrategy) {

        if(!storageStrategy.equals("object-storage") && !storageStrategy.equals("file-system")){
            throw new IllegalArgumentException("Invalid storage strategy: " + storageStrategy);
        }

        this.packageRepository = packageRepository;
        this.storageLibrary=storageLibrary;
        this.storageStrategy= storageStrategy;
    }

    @Override
    public String uploadPackage(MultipartFile file, MetadataDTO metadata) throws FileAlreadyExistsException {

        if(packageRepository.existsPackageByPackageNameAndVersion(metadata.name,metadata.version)){
            throw new FileAlreadyExistsException("Package already exists.");
        }
        try{
            Package newPackage = new Package();
            newPackage.setPackageName(metadata.name);
            newPackage.setVersion(metadata.version);
            newPackage.setAuthor(metadata.author);
            newPackage.setStorageStrategy(storageStrategy);
            String storageLibraryResponse = storageLibrary.write(file,metadata);
            packageRepository.save(newPackage);
            return (storageLibraryResponse + "\nPackage uploaded successfully");
        }catch (Exception e){
            throw new RuntimeException("Error encountered during storage.", e);
        }

    }

    @Override
    public InputStream downloadPackage(String packageName, String version, String fileName) throws Exception{
        Package foundPackage = packageRepository.findPackageByPackageNameAndVersion(packageName,version);
        if (foundPackage==null){
            throw new FileNotFoundException("Requested package is not found");
        }
        return storageLibrary.read(packageName,version, fileName);
    }
}
