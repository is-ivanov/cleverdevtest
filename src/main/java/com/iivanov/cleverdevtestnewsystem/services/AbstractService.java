package com.iivanov.cleverdevtestnewsystem.services;

import com.iivanov.cleverdevtestnewsystem.entities.AbstractEntity;
import com.iivanov.cleverdevtestnewsystem.exceptions.MyEntityNotFoundException;
import com.iivanov.cleverdevtestnewsystem.services.interfaces.Service;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public abstract class AbstractService<T extends AbstractEntity> implements Service<T> {

    @Override
    @Transactional(readOnly = true)
    public T findById(Long id) {
        return getRepo().findById(id)
            .orElseThrow(() ->
                new MyEntityNotFoundException(getEntityName(), "id", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> findAll() {
        return getRepo().findAll();
    }

    @Override
    public T create(T entity) {
        return getRepo().save(entity);
    }

    @Override
    public void delete(Long id) {
        try {
            getRepo().deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new MyEntityNotFoundException(getEntityName(), "id", id, e);
        }
    }

    protected abstract JpaRepository<T, Long> getRepo();

    protected abstract String getEntityName();
}
