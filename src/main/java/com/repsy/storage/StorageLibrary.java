package com.repsy.storage;

import org.springframework.web.multipart.MultipartFile;

public interface StorageLibrary {

    public String read(String packageName, String version);
    public String write(MultipartFile file, String metaDataJson);
}
