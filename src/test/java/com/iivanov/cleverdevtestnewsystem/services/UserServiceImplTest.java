package com.iivanov.cleverdevtestnewsystem.services;

import com.iivanov.cleverdevtestnewsystem.dao.UserRepository;
import com.iivanov.cleverdevtestnewsystem.entities.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    public static final String USER_LOGIN = "userLogin";

    @Captor
    ArgumentCaptor<User> userCaptor;

    @Mock
    private UserRepository repoMock;

    @InjectMocks
    private UserServiceImpl userService;

    @Nested
    @DisplayName("test 'findByLoginAndCreateIfMissing' method")
    class FindByLoginAndCreateIfMissingTest {

        @Test
        @DisplayName("when Repo return Optional with user then shouldn't call " +
            "Repo.save() method and return this user")
        void whenRepoReturnUser_NotCallRepoSaveMethod() {
            User user = new User(1L, USER_LOGIN);
            Optional<User> optionalFromRepo = Optional.of(user);

            when(repoMock.findByLogin(anyString())).thenReturn(optionalFromRepo);

            User actualUser = userService.findByLoginAndCreateIfMissing(anyString());

            assertThat(actualUser).isEqualTo(user);
            verify(repoMock, never()).save(any());
        }

        @Test
        @DisplayName("when Repo return empty Optional then should call Repo.save()")
        void whenRepoReturnEmptyOptional_CallRepoSave() {
            Optional<User> emptyOptional = Optional.empty();

            when(repoMock.findByLogin(USER_LOGIN)).thenReturn(emptyOptional);

            userService.findByLoginAndCreateIfMissing(USER_LOGIN);

            verify(repoMock).save(userCaptor.capture());
            User capturedUser = userCaptor.getValue();
            assertThat(capturedUser.getLogin()).isEqualTo(USER_LOGIN);
        }
    }
}