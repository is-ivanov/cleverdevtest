package com.iivanov.cleverdevtestnewsystem.services;

import com.iivanov.cleverdevtestnewsystem.dao.UserRepository;
import com.iivanov.cleverdevtestnewsystem.entities.User;
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
public class UserServiceImpl extends AbstractService<User> implements UserService {

    private final UserRepository userRepo;

    @Override
    public User findByLoginOrCreateIfMissing(String login) {
        try {
            return findByLogin(login);
        } catch (MyEntityNotFoundException e) {
            return create(login);
        }
    }

    private User findByLogin(String login) {
        return userRepo.findByLogin(login)
            .orElseThrow(() ->
                new MyEntityNotFoundException(getEntityName(), "login", login));
    }

    private User create(String login) {
        return userRepo.save(new User(login));
    }

    @Override
    protected JpaRepository<User, Long> getRepo() {
        return userRepo;
    }

    @Override
    protected String getEntityName() {
        return User.class.getSimpleName();
    }
}
