package com.repsy.storage;


import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@ConditionalOnProperty(name="storage.strategy", havingValue = "object-storage")
public class ObjectStorageLibrary implements StorageLibrary {

    @Override
    public String write(MultipartFile file, String metaDataJson) {
        return("Writing in Obj Storage.");
    }

    @Override
    public String read(String packageName, String version) {
        return("Reading in Obj Storage.");
    }
}
