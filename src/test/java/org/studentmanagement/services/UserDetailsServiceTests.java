package org.studentmanagement.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.studentmanagement.data.entities.UserEntity;
import org.studentmanagement.data.enums.RoleEnum;
import org.studentmanagement.data.repositories.UserRepository;
import org.studentmanagement.services.implementations.UserDetailsServiceImpl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
class UserDetailsServiceTests {
    @Mock
    private UserRepository userRepository;
    private UserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        userRepository  = Mockito.mock(UserRepository.class);
        userDetailsService = new UserDetailsServiceImpl(userRepository);
    }

    @Test
    void testLoadUserByUsernameValidUsername() {
        String username = "username";
        UserEntity userEntity = Mockito.mock(UserEntity.class);
        RoleEnum role = Mockito.mock(RoleEnum.class);
        String roleString = "role";
        String password = "password";

        Mockito.when(userRepository.findUserEntityByEmail(username)).thenReturn(Optional.of(userEntity));
        Mockito.when(userEntity.getRole()).thenReturn(role);
        Mockito.when(userEntity.getEmail()).thenReturn(username);
        Mockito.when(userEntity.getPassword()).thenReturn(password);
        Mockito.when(role.toString()).thenReturn(roleString);

        UserDetails result = userDetailsService.loadUserByUsername(username);

        Mockito.verify(userRepository, Mockito.times(1)).findUserEntityByEmail(username);
        Mockito.verify(userEntity, Mockito.times(1)).getRole();

        Assertions.assertEquals(username, result.getUsername());
        Assertions.assertEquals(password, result.getPassword());
        Assertions.assertArrayEquals(
                result.getAuthorities().toArray(),
                new LinkedList<GrantedAuthority>(List.of(new SimpleGrantedAuthority(roleString))).toArray()
        );
    }

    @Test
    void testLoadUserByUsernameInvalidUsername() {
        String username = "invalidUsername";

        Mockito.when(userRepository.findUserEntityByEmail(username)).thenReturn(Optional.empty());

        Assertions.assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(username));
    }
}
