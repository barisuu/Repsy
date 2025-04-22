package com.repsy.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;

public interface PackageService {
    String uploadPackage(String packageName, String version, MultipartFile file, String metadataJson) throws Exception;
    String downloadPackage(String packageName, String version) throws FileNotFoundException;
}
