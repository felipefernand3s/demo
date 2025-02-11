package com.example.demo.service;

import com.example.demo.dto.UserDTO;
import com.example.demo.entity.User;
import com.example.demo.exceptions.EntityNotFoundException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private static String ID = "1";
    private static String USER_NAME = "John";
    private static String LAST_NAME = "Doe";
    private static String ADDRESS = "123 Main St";


    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Should be able to create a new user")
    void testSaveUserSuccess() {
        UserDTO userDTO = createUserDTO();
        when(userRepository.save(any(User.class))).thenAnswer(dto -> {
            userDTO.setId(ID);
            return UserMapper.INSTANCE.toEntity(userDTO);
        });
        assertEquals(userService.saveUser(userDTO), userDTO);
    }

    @Test
    @DisplayName("Should be fail to create a new user because ID is filled")
    void testSaveUserIdFilledError() {
        UserDTO userDTO = createUserDTOWithId();
        assertThrows(IllegalArgumentException.class, () -> userService.saveUser(userDTO));
    }

    @Test
    @DisplayName("Should delete user successfully")
    void testDeleteUserSuccess() {
        when(userRepository.existsById(ID)).thenReturn(true);
        userService.deleteUser(ID);
        verify(userRepository, times(1)).deleteById(ID);
    }

    @Test
    @DisplayName("Should NOT be able to delete user because it doesn't exist")
    void testDeleteUserThrowsExceptionWhenUserDoesNotExist() {
        assertThrows(EntityNotFoundException.class, () -> userService.deleteUser(ID));
    }

    @Test
    @DisplayName("Should return users")
    void testGetUsers() {
        UserDTO userDTO1 = createUserDTO();
        UserDTO userDTO2 = createUserDTO();

        List<UserDTO> users = new ArrayList<>();
        users.add(userDTO1);
        users.add(userDTO2);

        when(userRepository.findAll())
                .thenReturn(users.stream()
                        .map(UserMapper.INSTANCE::toEntity)
                        .collect(Collectors.toUnmodifiableList())
                );
        List<UserDTO> responseUsersDTO = userService.getAllUsers();
        assertEquals(users, responseUsersDTO);
    }

    @Test
    @DisplayName("Should be able to retrieve an user by ID")
    void testUpdateUser() {
        UserDTO userDTO = createUserDTOWithId();
        User userEntity = UserMapper.INSTANCE.toEntity(userDTO);

        when(userRepository.findById(ID)).thenReturn(Optional.of(userEntity));
        when(userRepository.save(userEntity)).thenReturn(userEntity);

        userService.updateUser(ID, userDTO);

        verify(userRepository, times(1)).findById(ID);
        verify(userRepository, times(1)).save(userEntity);
    }

    @Test
    @DisplayName("Should return null it can't find the user")
    void testUpdateUserFail() {
        when(userRepository.findById(ID)).thenReturn(Optional.empty());

        Optional<UserDTO> response = userService.updateUser(ID, createUserDTOWithId());

        verify(userRepository, times(1)).findById(ID);
        verify(userRepository, times(0)).save(any());
        assertEquals(response, Optional.empty());
    }

    @Test
    @DisplayName("Should return user by the id given")
    public void testGetUser() {
        UserDTO userDTO = createUserDTOWithId();
        when(userRepository.findById(ID)).thenReturn(Optional.of(UserMapper.INSTANCE.toEntity(userDTO)));
        assertEquals(Optional.of(userDTO), userService.getUserById(ID));
        verify(userRepository, times(1)).findById(ID);
    }

    @Test
    @DisplayName("Should return empty when it can't find user with given id")
    public void testGetUserNotFound() {
        UserDTO userDTO = createUserDTOWithId();
        when(userRepository.findById(ID)).thenReturn(Optional.empty());
        assertEquals(Optional.empty(), userService.getUserById(ID));
        verify(userRepository, times(1)).findById(ID);
    }

    private static UserDTO createUserDTOWithId() {
        UserDTO userDTO = createUserDTO();
        userDTO.setId(ID);
        return userDTO;
    }

    private static UserDTO createUserDTO() {
        UserDTO userDTO = new UserDTO();

        userDTO.setFirstName(USER_NAME);
        userDTO.setLastName(LAST_NAME);
        userDTO.setAddress(ADDRESS);
        userDTO.setBirthDate(LocalDate.of(1990, 6, 10));

        return userDTO;
    }
}
