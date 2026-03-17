package com.sila.modules.image.repository;

import com.sila.modules.image.model.Image;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ImageRepository
    extends JpaRepository<Image, Long>, JpaSpecificationExecutor<Image> {
  Optional<Image> findOneByPublicId(String publicId);
}
