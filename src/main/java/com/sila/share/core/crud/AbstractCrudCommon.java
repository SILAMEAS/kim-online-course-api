package com.sila.share.core.crud;

import com.sila.config.exception.NotFoundException;
import com.sila.share.core.pagiation.PaginationConstant;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.FluentQuery;

/**
 * Abstract class for CRUD common service.
 *
 * @param <E> refers to the entity type
 * @param <T> refers to the type of 'primary key'
 */
public abstract class AbstractCrudCommon<
        E, T, R extends JpaRepository<E, T> & JpaSpecificationExecutor<E>>
    implements GetClassType {
  protected final R baseRepository;
  protected final ModelMapper mapper;

  protected AbstractCrudCommon(R baseRepository, ModelMapper mapper) {
    this.baseRepository = baseRepository;
    this.mapper = mapper;
  }

  /**
   * Creates a Pageable object for pagination with the given page number and page size.
   *
   * @param page the number of the page to retrieve (0-indexed)
   * @param limit the maximum number of items to retrieve per page
   * @return a Pageable object representing the pagination settings
   */
  protected Pageable toPageable(int page, int limit) {
    return PageRequest.of(page - 1, limit);
  }

  /**
   * Creates a Pageable object for pagination with the given page number, page size, sort by field,
   * and sort direction.
   *
   * @param page the number of the page to retrieve (0-indexed)
   * @param limit the maximum number of items to retrieve per page
   * @param sortBy the field to sort the results by
   * @param sortOrder the direction to sort the results in (either "ASC" or "DESC")
   * @return a Pageable object representing the pagination settings
   */
  protected Pageable toPageable(
      int page, int limit, @NonNull String sortBy, @NonNull String sortOrder) {
    final var sort = Sort.by(Sort.Direction.valueOf(sortOrder.toUpperCase(Locale.ENGLISH)), sortBy);
    return PageRequest.of(page - 1, limit, sort);
  }

  protected <B extends BaseSortEnum> Pageable toPageable(BaseSearchRequest<B> request) {

    final var sort =
        request.sortField() == null
            ? Sort.by(Sort.Direction.DESC, PaginationConstant.DEFAULT_SORT_BY_FIELD)
            : Sort.by(request.sortDirection(), request.sortField().getProperty());

    return PageRequest.of(request.page() - 1, request.limit(), sort);
  }

  protected E findById(@NonNull T id) {
    return this.baseRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("resource not found"));
  }

  protected E findActiveById(UUID id) {

    return this.baseRepository
        .findOne(
            Specification.<E>where((var root, var query, var cb) -> cb.equal(root.get("id"), id))
                .and((var root, var query, var cb) -> cb.isNull(root.get("deletedAt"))))
        .orElseThrow(() -> new NotFoundException("resource not found"));
  }

  protected E save(@NonNull E entity) {
    return this.baseRepository.save(entity);
  }

  protected E update(@NonNull E entity) {
    return this.baseRepository.save(entity);
  }

  protected void deleteById(@NonNull T id) {
    this.baseRepository.deleteById(id);
  }

  /**
   * Finds a single entity matching the given query.
   *
   * @param query the fluent query to execute
   * @return the matched entity
   * @throws NotFoundException if no entity is found
   */
  protected E findOne(FluentQuery.FetchableFluentQuery<E> query) {
    return query.one().orElseThrow(() -> new NotFoundException("resource not found"));
  }

  /**
   * Finds a single entity matching the given query.
   *
   * @param query the fluent query to execute
   * @return the optional entity
   */
  protected Optional<E> findOneOptional(FluentQuery.FetchableFluentQuery<E> query) {
    return query.one();
  }

  /**
   * Executes the given query and returns at most one result mapped to the specified type.
   *
   * @param responseType target projection/DTO type
   * @param query fluent query to execute
   * @param <A> result type
   * @return an {@link Optional} containing the result if found, otherwise {@link Optional#empty()}
   */
  protected <A> Optional<A> findOneOptional(
      Class<A> responseType, FluentQuery.FetchableFluentQuery<E> query) {
    return query.as(responseType).one();
  }

  /**
   * Retrieves a paginated (and optionally sorted) list of entities of type {@code E}.
   *
   * <p>If a {@link Specification} is provided, it will be applied to filter the results. Otherwise,
   * all entities are returned. Sorting is optional; if {@code sortByField} is null or empty, no
   * sorting is applied.
   *
   * @param pageable the page number to retrieve (1-based) {@code sortByField} is null)
   * @return a {@link Page} containing the entities for the requested page
   * @throws IllegalStateException if a {@link Specification} is provided but the repository does
   *     not support it
   */
  protected Page<E> findAll(@NonNull Pageable pageable) {
    return this.baseRepository.findAll(pageable);
  }

  /**
   * Retrieves a paginated (and optionally sorted) list of entities of type {@code E}.
   *
   * <p>If a {@link Specification} is provided, it will be applied to filter the results. Otherwise,
   * all entities are returned. Sorting is optional; if {@code sortByField} is null or empty, no
   * sorting is applied.
   *
   * @param spec an optional {@link Specification} to filter the results (can be null)
   * @param pageable the page number to retrieve (1-based) {@code sortByField} is null)
   * @return a {@link Page} containing the entities for the requested page
   * @throws IllegalStateException if a {@link Specification} is provided but the repository does
   *     not support it
   */
  protected Page<E> findAll(@NonNull Specification<E> spec, Pageable pageable) {
    return this.baseRepository.findAll(spec, pageable);
  }
}
