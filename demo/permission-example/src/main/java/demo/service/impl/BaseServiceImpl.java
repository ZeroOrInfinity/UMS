/*
 * MIT License
 * Copyright (c) 2020-2029 YongWu zheng (dcenter.top and gitee.com/pcore and github.com/ZeroOrInfinity)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package demo.service.impl;

import demo.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Optional;

/**
 * {@link BaseService} 的抽象实现类
 * @author YongWu zheng
 * @version V1.0  Created by 2020/9/26 16:47
 */
@Slf4j
public abstract class BaseServiceImpl<T, ID extends Serializable> implements BaseService<T, ID> {
    
    private final CrudRepository<T, ID> repository;

    protected BaseServiceImpl(CrudRepository<T, ID> repository) {
        this.repository = repository;
    }


    @Transactional(rollbackFor = {Exception.class, Error.class}, propagation = Propagation.REQUIRED)
    @Override
    public <S extends T> S save(S entity) {
        return repository.save(entity);
    }

    @Transactional(rollbackFor = {Exception.class, Error.class}, propagation = Propagation.REQUIRED)
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

    @Transactional(rollbackFor = {Exception.class, Error.class}, propagation = Propagation.REQUIRED)
    @Override
    public void deleteById(ID id) {
        repository.deleteById(id);
    }

    @Transactional(rollbackFor = {Exception.class, Error.class}, propagation = Propagation.REQUIRED)
    @Override
    public void delete(T entity) {
        repository.delete(entity);
    }

    @Transactional(rollbackFor = {Exception.class, Error.class}, propagation = Propagation.REQUIRED)
    @Override
    public void deleteAll(Iterable<? extends T> entities) {
        repository.deleteAll(entities);
    }
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    @Override
    public void deleteAll() {
        repository.deleteAll();
    }

}