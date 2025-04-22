package com.repsy.storage;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
@ConditionalOnProperty(name="storage.strategy", havingValue = "file-system")
public class FileSystemLibrary implements StorageLibrary {
    @Value("${file.storage.location}")
    private String storagePath;

    @Override
    public String write(MultipartFile file, String metaDataJson) {
        try{
            String fileName = file.getOriginalFilename();
            String filePath = storagePath +"/"+ fileName;
            file.transferTo(new File(filePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return("Writing in File system.");
    }

    @Override
    public String read(String packageName, String version) {
        return("Reading in File system.");
    }
}
