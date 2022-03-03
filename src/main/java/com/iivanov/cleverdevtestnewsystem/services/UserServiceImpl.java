package com.iivanov.cleverdevtestnewsystem.services;

import com.iivanov.cleverdevtestnewsystem.dao.CompanyUserRepository;
import com.iivanov.cleverdevtestnewsystem.entities.CompanyUser;
import com.iivanov.cleverdevtestnewsystem.exceptions.MyEntityNotFoundException;
import com.iivanov.cleverdevtestnewsystem.services.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl extends AbstractService<CompanyUser> implements UserService {

    private final CompanyUserRepository userRepo;

    @Override
    public CompanyUser findByLoginAndCreateIfMissing(String login) {
        try {
            return findByLogin(login);
        } catch (MyEntityNotFoundException e) {
            return create(login);
        }
    }

    private CompanyUser findByLogin(String login) {
        return userRepo.findByLogin(login)
            .orElseThrow(() ->
                new MyEntityNotFoundException(getEntityName(), "login", login));
    }

    private CompanyUser create(String login) {
        return userRepo.save(new CompanyUser(login));
    }

    @Override
    protected JpaRepository<CompanyUser, Long> getRepo() {
        return userRepo;
    }

    @Override
    protected String getEntityName() {
        return CompanyUser.class.getSimpleName();
    }
}
