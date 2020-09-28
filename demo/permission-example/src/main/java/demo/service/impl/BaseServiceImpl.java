package demo.service.impl;

import demo.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Optional;

/**
 * @author zyw
 * @version V1.0  Created by 2020/9/26 16:47
 */
@Slf4j
public abstract class BaseServiceImpl<T, ID extends Serializable> implements BaseService<T, ID> {
    
    private final CrudRepository<T, ID> repository;

    protected BaseServiceImpl(CrudRepository<T, ID> repository) {
        this.repository = repository;
    }


    @Transactional(rollbackFor = {Exception.class, Error.class})
    @Override
    public <S extends T> S save(S entity) {
        return repository.save(entity);
    }

    @Transactional(rollbackFor = {Exception.class, Error.class})
    @Override
    public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
        return repository.saveAll(entities);
    }

    @Override
    public Optional<T> findById(ID id) {
        return repository.findById(id);
    }

    @Override
    public boolean existsById(ID id) {
        return repository.existsById(id);
    }

    @Override
    public Iterable<T> findAll() {
        return repository.findAll();
    }

    @Override
    public Iterable<T> findAllById(Iterable<ID> ids) {
        return repository.findAllById(ids);
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Transactional(rollbackFor = {Exception.class, Error.class})
    @Override
    public void deleteById(ID id) {
        repository.deleteById(id);
    }

    @Transactional(rollbackFor = {Exception.class, Error.class})
    @Override
    public void delete(T entity) {
        repository.delete(entity);
    }

    @Transactional(rollbackFor = {Exception.class, Error.class})
    @Override
    public void deleteAll(Iterable<? extends T> entities) {
        repository.deleteAll(entities);
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }

}
