package com.repsy.storage;


import com.repsy.dto.MetadataDTO;
import io.minio.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
@ConditionalOnProperty(name="storage.strategy", havingValue = "object-storage")
public class ObjectStorageLibrary implements StorageLibrary {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    public ObjectStorageLibrary(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @Override
    public String write(MultipartFile file, MetadataDTO metaData) throws Exception{
        String fileName = file.getOriginalFilename();
        InputStream filestream = file.getInputStream();
        //TODO: NEED TO ADD METADATA AND FILE INTO ONE FOLDER AND ADD IT TO BUCKET
        PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                .bucket(bucketName)
                .object(fileName+"-"+metaData.version)
                .stream(filestream,file.getSize(),-1)
                .contentType(file.getContentType())
                .build();

        ObjectWriteResponse response = minioClient.putObject(putObjectArgs);

        return(response.toString());
    }
    //TODO: This
    @Override
    public InputStream read(String packageName, String version, String fileName) throws Exception {
        GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                .bucket(bucketName)
                .object(packageName)
                .build();

        GetObjectResponse response = minioClient.getObject(getObjectArgs);

        return new InputStream() {
            @Override
            public int read() throws IOException {
                return 0;
            }
        };
    }
}
