package com.iivanov.cleverdevtestnewsystem.services.interfaces;

import com.iivanov.cleverdevtestnewsystem.entities.CompanyUser;

public interface UserService extends Service<CompanyUser> {

    CompanyUser findByLoginAndCreateIfMissing(String login);
}
