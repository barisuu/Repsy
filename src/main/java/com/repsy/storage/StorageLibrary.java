package com.repsy.storage;

import com.repsy.dto.MetadataDTO;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;

public interface StorageLibrary {

    public InputStream read(String packageName, String version, String fileName) throws Exception;
    public String write(MultipartFile file, MetadataDTO metaData) throws Exception;
}
