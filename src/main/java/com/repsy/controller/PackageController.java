package com.repsy.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.HttpHeaders;

import com.repsy.exceptions.FileValidationException;
import com.repsy.exceptions.InvalidMetadataException;
import com.repsy.service.PackageService;
import com.repsy.storage.MetadataDTO;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


@RestController
@Validated
@RequestMapping("/packages")
public class PackageController {

    private final ObjectMapper objectMapper;
    private final PackageService packageService;
    private final Validator validator;

    public PackageController(ObjectMapper objectMapper, PackageService packageService, Validator validator) {
        this.objectMapper = objectMapper;
        this.packageService = packageService;
        this.validator = validator;
    }

    @PostMapping("/{packageName}/{version}")
    public ResponseEntity<String> uploadPackage(@PathVariable String packageName,
                                                @PathVariable String version,
                                                @RequestPart("file") MultipartFile file,
                                                @RequestPart("meta") MultipartFile metadataJson) {

        //TODO: Add validation between URL variables and metadata
        if (file.isEmpty() || metadataJson.isEmpty()) {
            throw new FileValidationException("File(s) must not be empty.");
        }
        if(file.getOriginalFilename()==null){
            throw new FileValidationException("Invalid filename.");
        }
        if (!file.getOriginalFilename().endsWith(".rep")) {
            throw new FileValidationException("File must be a .rep type.");
        }
        if(!file.getOriginalFilename().equals(packageName+".rep")){
            throw new FileValidationException("File name must be the same as path variable.");
        }


        MetadataDTO metadata;
        try {
            metadata = objectMapper.readValue(metadataJson.getBytes(), MetadataDTO.class);
        } catch (IOException e) {
            throw new InvalidMetadataException("Invalid JSON format.");
        }

        BindingResult bindingResult = new BeanPropertyBindingResult(metadata, "metadata");
        validator.validate(metadata, bindingResult);
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder("Metafile validation failed: ");
            bindingResult.getAllErrors().forEach(error ->
                    errorMessage.append(error.getDefaultMessage()).append(", ")
            );
            throw new InvalidMetadataException(errorMessage.toString());
        }

        if(!metadata.name.equals(packageName) || !metadata.version.equals(version)){
            throw new InvalidMetadataException("Parameters don't match metadata.");
        }

        try {
            String response = packageService.uploadPackage(file, metadata);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{packageName}/{version}/{fileName}")
    public ResponseEntity<Object> downloadPackage(
            @PathVariable String packageName,
            @PathVariable String version,
            @PathVariable String fileName) {

        MediaType contentType;
        if (fileName.endsWith(".json")) {
            contentType = MediaType.APPLICATION_JSON;
        } else if (fileName.endsWith(".rep")) {
            contentType = MediaType.APPLICATION_OCTET_STREAM;
        } else {
            return ResponseEntity.badRequest().body(null);
        }

        try {
            InputStream response = packageService.downloadPackage(packageName, version, fileName);
            return ResponseEntity.ok()
                    .contentType(contentType)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(new InputStreamResource(response));
        } catch (FileNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error occurred during download: " + e.getMessage());
        }
    }
}
