package com.iivanov.cleverdevtestnewsystem.services.interfaces;

import com.iivanov.cleverdevtestnewsystem.entities.AbstractEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface Service<T extends AbstractEntity> {

    @Transactional(readOnly = true)
    T findById(Long id);

    @Transactional(readOnly = true)
    List<T> findAll();

    T create(T entity);

//    T update(int id, T entity);

    void delete(Long id);
}
