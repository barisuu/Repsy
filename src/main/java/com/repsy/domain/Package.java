package com.repsy.domain;


import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(indexes = {
        @Index(name="pck_version",columnList = "packageName, version", unique = true)
})
public class Package {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String packageName;
    private String version;
    private String author;
    private String storageStrategy;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getStorageStrategy() {
        return storageStrategy;
    }

    public void setStorageStrategy(String storageStrategy) {
        this.storageStrategy = storageStrategy;
    }

}

