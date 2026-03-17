package com.sila.modules.image.service;

import com.sila.config.context.UserContext;
import com.sila.config.exception.BadRequestException;
import com.sila.modules.image.Enum.CloudinaryFolder;
import com.sila.modules.image.dto.ImageListResponse;
import com.sila.modules.image.model.Image;
import com.sila.modules.image.repository.ImageRepository;
import com.sila.modules.image.spec.ImageSpec;
import com.sila.share.core.crud.AbstractCrudCommon;
import com.sila.share.core.pagination.EntityResponseHandler;
import com.sila.share.core.pagination.PaginationRequest;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class ImageService extends AbstractCrudCommon<Image, Long, ImageRepository> {

  private final ImageServiceCloudinary imageServiceCloudinary;

  protected ImageService(
      ImageRepository baseRepository,
      ModelMapper mapper,
      ImageServiceCloudinary imageServiceCloudinary) {
    super(baseRepository, mapper);
    this.imageServiceCloudinary = imageServiceCloudinary;
  }

  /** Upload single image */
  @Transactional
  public String uploadImageFolderProfile(MultipartFile file) {
    return imageServiceCloudinary.uploadImage(file, CloudinaryFolder.PROFILE);
  }

  /** Update image */
  @Transactional
  public String updateImage(String oldPublicId, MultipartFile file) {

    return imageServiceCloudinary.updateImage(oldPublicId, file, CloudinaryFolder.PROFILE);
  }

  @Transactional
  public void updateImageEntity(Image image) {
    super.update(image);
  }

  /** Get URL of image */
  @Transactional
  public String getUrlImage(String publicId) {

    return imageServiceCloudinary.getImageUrl(publicId);
  }

  /** Get URL of image */
  @Transactional(readOnly = true)
  public Image getImageByUserLogin() {

    return baseRepository.findOneByUserId(UserContext.getUserId()).orElse(null);
  }

  /**
   * Retrieves all videos globally with pagination and search filters.
   *
   * @param paginationRequest Pagination parameters and search term
   * @return Paginated list of VideoListResponse
   */
  @Transactional(readOnly = true)
  public EntityResponseHandler<ImageListResponse> getAllImages(
      PaginationRequest paginationRequest) {
    var pageable =
        super.toPageable(
            paginationRequest.getPage(),
            paginationRequest.getLimit(),
            paginationRequest.getSortBy(),
            String.valueOf(paginationRequest.getSortOrder()));
    var spec = ImageSpec.search(paginationRequest.getSearch());
    final var imagePage = super.findAll(spec, pageable);
    final var images = imagePage.map(vd -> mapper.map(vd, ImageListResponse.class));
    return new EntityResponseHandler<>(images);
  }

  @Transactional
  public Image findByPublicId(String publicId) {
    return this.baseRepository
        .findOneByPublicId(publicId)
        .orElseThrow(() -> new BadRequestException("Not found image with this publicId"));
  }

  /** Delete image by publicId */
  @Transactional
  public void deleteImageByPublicId(String publicId) {
    imageServiceCloudinary.deleteImage(publicId);
  }
}
