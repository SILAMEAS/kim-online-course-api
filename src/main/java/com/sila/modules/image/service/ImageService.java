package com.sila.modules.image.service;

import com.sila.config.exception.BadRequestException;
import com.sila.modules.image.Enum.CloudinaryFolder;
import com.sila.modules.image.dto.ImageListResponse;
import com.sila.modules.image.model.Image;
import com.sila.modules.image.repository.ImageRepository;
import com.sila.modules.image.spec.ImageSpec;
import com.sila.share.core.crud.AbstractCrudCommon;
import com.sila.share.core.pagination.ResponsePaginationHandler;
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
  /** Get URL of image */
  @Transactional
  public ResponsePaginationHandler<ImageListResponse> listAllImage( PaginationRequest paginationRequest) {
    var pageable =
            super.toPageable(
                    paginationRequest.getPage(),
                    paginationRequest.getLimit(),
                    paginationRequest.getSortBy(),
                    String.valueOf(paginationRequest.getSortOrder()));
    var spec = ImageSpec.search(paginationRequest.getSearch());
    final var imagePage = super.findAll(spec, pageable);
    final var images = imagePage.map(im -> mapper.map(im, ImageListResponse.class));
    return new ResponsePaginationHandler<>(images);
  }



  /** Get URL of image */
  @Transactional
  public String getUrlImage(String publicId) {

    return imageServiceCloudinary.getImageUrl(publicId);
  }

  /**
   * Retrieves all videos globally with pagination and search filters.
   *
   * @param paginationRequest Pagination parameters and search term
   * @return Paginated list of VideoListResponse
   */
  @Transactional(readOnly = true)
  public ResponsePaginationHandler<ImageListResponse> getAllImages(
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
    return new ResponsePaginationHandler<>(images);
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

  @Transactional
  public Image createImage(MultipartFile file, CloudinaryFolder folder) {
    String publicId = this.uploadImageFolderProfile(file, folder);
    return super.save(Image.builder().title(publicId).publicId(publicId).build());
  }

  /** Update image */
  @Transactional
  public Image updateImage(Image image, MultipartFile file, CloudinaryFolder folder) {
    var newPublicId = imageServiceCloudinary.updateImage(image.getPublicId(), file, folder);
    image.setPublicId(newPublicId);
    return super.update(image);
  }

  /** Upload single image */
  public String uploadImageFolderProfile(MultipartFile file, CloudinaryFolder folder) {
    return imageServiceCloudinary.uploadImage(file, folder);
  }
}
