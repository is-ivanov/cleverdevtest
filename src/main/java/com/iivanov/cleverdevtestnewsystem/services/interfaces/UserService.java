package com.iivanov.cleverdevtestnewsystem.services.interfaces;

import com.iivanov.cleverdevtestnewsystem.entities.User;

public interface UserService extends Service<User> {

    User findByLoginOrCreateIfMissing(String login);
}
