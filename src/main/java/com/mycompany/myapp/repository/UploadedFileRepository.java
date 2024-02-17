package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.UploadedFile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the UploadedFile entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UploadedFileRepository extends ReactiveCrudRepository<UploadedFile, Long>, UploadedFileRepositoryInternal {
    @Override
    <S extends UploadedFile> Mono<S> save(S entity);

    @Override
    Flux<UploadedFile> findAll();

    @Override
    Mono<UploadedFile> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface UploadedFileRepositoryInternal {
    <S extends UploadedFile> Mono<S> save(S entity);

    Flux<UploadedFile> findAllBy(Pageable pageable);

    Flux<UploadedFile> findAll();

    Mono<UploadedFile> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<UploadedFile> findAllBy(Pageable pageable, Criteria criteria);
}
