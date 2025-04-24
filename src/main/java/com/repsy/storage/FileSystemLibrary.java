package com.repsy.storage;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.repsy.dto.MetadataDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@ConditionalOnProperty(name="storage.strategy", havingValue = "file-system")
public class FileSystemLibrary implements StorageLibrary {
    @Value("${file.storage.location}")
    private String storagePath;
    private final ObjectMapper mapper;

    public FileSystemLibrary(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public String write(MultipartFile file, MetadataDTO metaData) {
        try{
            String fileName = file.getOriginalFilename();
            String dirName = metaData.name+"-"+metaData.version;
            Path fileDir = Paths.get(storagePath,dirName);
            Files.createDirectories(fileDir);
            //Saving .rep file
            Path filePath = fileDir.resolve(fileName);
            file.transferTo(filePath.toFile());
            //Saving metaDataJson as .txt
            Path metaDataPath = fileDir.resolve("metadata.json");
            mapper.writeValue(metaDataPath.toFile(), metaData);
            return "File and metafile uploaded.";
        } catch (IOException e) {
            throw new RuntimeException("Error writing file or metadata. " + e.getMessage());
        }
    }

    @Override
    public InputStream read(String packageName, String version, String fileName) throws FileNotFoundException {
        String dirName = storagePath+"/"+packageName+"-"+version+"/";
        File file = new File(dirName+fileName);
        if(!file.exists()){
            throw new FileNotFoundException("File not found: " + fileName);
        }
        return new FileInputStream(file);
    }
}
