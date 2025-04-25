package com.repsy.service;

import com.repsy.storage.MetadataDTO;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;

public interface PackageService {
    String uploadPackage(MultipartFile file, MetadataDTO metadata) throws Exception;
    InputStream downloadPackage(String packageName, String version, String fileName) throws Exception;
}
