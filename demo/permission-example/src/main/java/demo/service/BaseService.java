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

package demo.service;

import java.io.Serializable;
import java.util.Optional;

/**
 * 基本服务
 * @author YongWu zheng
 * @version V1.0
 * Created by 2020/9/26 17:03
 */
public interface BaseService<T, ID extends Serializable> {
    /**
     * 保存
     * @param entity    entity
     * @return  entity
     */
    <S extends T> S save(S entity);

    /**
     * 保存
     * @param entities  entities
     * @return  Iterable
     */
    <S extends T> Iterable<S> saveAll(Iterable<S> entities);

    /**
     * 根据 id 查询
     * @param id    id
     * @return  SysResources
     */
    Optional<T> findById(ID id);

    /**
     * id 实体是否存在
     * @param id    id
     * @return  boolean
     */
    boolean existsById(ID id);

    /**
     * 查找所有
     * @return Iterable
     */
    Iterable<T> findAll();

    /**
     * 根据 id 查询实体列表
     * @param ids   ids
     * @return  Iterable
     */
    Iterable<T> findAllById(Iterable<ID> ids);

    /**
     * 统计总记录
     * @return long
     */
    long count();

    /**
     * 根据 id 删除
     * @param id    id
     */
    void deleteById(ID id);

    /**
     * 根据实体删除
     * @param entity    entity
     */
    void delete(T entity);

    /**
     * 根据 entities 删除所有
     * @param entities  entities
     */
    void deleteAll(Iterable<? extends T> entities);

    /**
     * 删除所有
     */
    void deleteAll();

}