package com.sila.modules.image.service;

import com.sila.config.context.UserContext;
import com.sila.config.exception.BadRequestException;
import com.sila.modules.image.dto.ImageListResponse;
import com.sila.modules.image.model.Image;
import com.sila.modules.image.repository.ImageRepository;
import com.sila.modules.image.spec.ImageSpec;
import com.sila.share.core.crud.AbstractCrudCommon;
import com.sila.share.core.pagination.EntityResponseHandler;
import com.sila.share.core.pagination.PaginationRequest;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageService extends AbstractCrudCommon<Image, Long, ImageRepository> {

  private final ImageCloudinaryService imageCloudinaryService;

  protected ImageService(
      ImageRepository baseRepository,
      ModelMapper mapper,
      ImageCloudinaryService imageCloudinaryService) {
    super(baseRepository, mapper);
    this.imageCloudinaryService = imageCloudinaryService;
  }

  /** Upload single image */
  @Transactional
  public String uploadImage(MultipartFile file) {
    return imageCloudinaryService.uploadImage(file);
  }

  /** Upload multiple images */
  @Transactional
  public List<String> uploadMultipleImages(List<MultipartFile> files) {
    return files.stream().map(imageCloudinaryService::uploadImage).toList();
  }

  /** Delete single image */
  @Transactional
  public void deleteImage(String publicId) {
    imageCloudinaryService.deleteImage(publicId);
  }

  /** Delete multiple images */
  @Transactional
  public void deleteMultipleImages(List<String> publicIds) {
    imageCloudinaryService.deleteImages(publicIds);
  }

  /** Update image */
  @Transactional
  public String updateImage(String oldPublicId, MultipartFile file) {

    return imageCloudinaryService.updateImage(oldPublicId, file);
  }

  @Transactional
  public Image updateImageEntity(Image image) {
    return super.update(image);
  }

  /** Get URL of image */
  @Transactional
  public String getUrlImage(String publicId) {

    return imageCloudinaryService.getImageUrl(publicId);
  }

  /** Get URL of image */
  @Transactional(readOnly = true)
  public String getPublicProfileImage() {

    return baseRepository
        .findOneByUserId(UserContext.getUserId())
        .map(img -> imageCloudinaryService.getImageUrl(img.getPublicId()))
        .orElse(null);
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

  //  @Transactional
  //  public void uploadImageUser(String title, MultipartFile file) {
  //
  //    String publicId = this.imageCloudinaryService.uploadImage(file);
  //
  //    Image image = new Image();
  //    image.setTitle(title);
  //    image.setPublicId(publicId);
  //    image.setUser(UserContext.getUser());
  //
  //    super.save(image);
  //  }

  @Transactional
  public Image saveImage(Image image) {
    return super.save(image);
  }

  @Transactional
  public Image findyByPublicId(String publicId) {
    return this.baseRepository
        .findOneByPublicId(publicId)
        .orElseThrow(() -> new BadRequestException("Not found image with this publicId"));
  }
}
