package com.repsy.repository;

import com.repsy.domain.Package;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PackageRepository extends JpaRepository<com.repsy.domain.Package, String> {
    Package findPackageByPackageNameAndVersion(String packageName, String version);

    boolean existsPackageByPackageNameAndVersion(String packageName, String version);
}
