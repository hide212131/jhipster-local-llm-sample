package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.UploadedFile;
import com.mycompany.myapp.repository.rowmapper.UploadedFileRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoin;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the UploadedFile entity.
 */
@SuppressWarnings("unused")
class UploadedFileRepositoryInternalImpl extends SimpleR2dbcRepository<UploadedFile, Long> implements UploadedFileRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final UploadedFileRowMapper uploadedfileMapper;

    private static final Table entityTable = Table.aliased("uploaded_file", EntityManager.ENTITY_ALIAS);

    public UploadedFileRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        UploadedFileRowMapper uploadedfileMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(UploadedFile.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.uploadedfileMapper = uploadedfileMapper;
    }

    @Override
    public Flux<UploadedFile> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<UploadedFile> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = UploadedFileSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, UploadedFile.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<UploadedFile> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<UploadedFile> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private UploadedFile process(Row row, RowMetadata metadata) {
        UploadedFile entity = uploadedfileMapper.apply(row, "e");
        return entity;
    }

    @Override
    public <S extends UploadedFile> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
