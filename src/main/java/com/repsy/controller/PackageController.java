package com.repsy.controller;


import com.repsy.service.PackageService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;


@RestController
@Validated
@RequestMapping("/packages")
public class PackageController {
    private final PackageService packageService;

    public PackageController(PackageService packageService) {
        this.packageService = packageService;
    }

    @PostMapping("/{packageName}/{version}")
    public ResponseEntity<String> uploadPackage(@PathVariable String packageName,
                                                @PathVariable String version,
                                                @RequestPart("file") MultipartFile file,
                                                @RequestPart("meta") String metadataJson)
    {
        try{
            if(file.isEmpty()){
                throw new IOException("Sth");
            }
            String response = packageService.uploadPackage(packageName,version,file,metadataJson);
            return ResponseEntity.ok(response);
        }catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().body("Validation error: " + e.getMessage());
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body("Error occured during upload.");
        }

    }

    @GetMapping("/{packageName}/{version}")
    public ResponseEntity<String> downloadPackage(
            @PathVariable String packageName,
            @PathVariable String version) {
        try{
            String response = packageService.downloadPackage(packageName,version);
            return ResponseEntity.ok(response);
        }catch (FileNotFoundException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }catch (Exception e){
            return ResponseEntity.badRequest().body("Error occured during download: " + e.getMessage());
        }
    }
}
