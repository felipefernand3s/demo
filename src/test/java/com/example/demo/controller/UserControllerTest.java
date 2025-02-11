package com.example.demo.controller;

import com.example.demo.dto.UserDTO;
import com.example.demo.exceptions.EntityNotFoundException;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    public static final String ID = "123";
    public static final String USER_NAME = "John";
    public static final String LAST_NAME = "Doe";
    public static final String ADDRESS = "123 Main St";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("Should be able to execute a successful PUT method in User")
    void testUpdateUserSuccess() throws Exception {
        String userId = ID;
        UserDTO userDTO = createUserDTO();

        when(userService.updateUser(eq(userId), any(UserDTO.class)))
                .thenReturn(Optional.of(userDTO));

        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "firstName": "John",
                                    "lastName": "Doe",
                                    "address": "123 Main St",
                                    "birthDate": "1990-06-10"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(USER_NAME))
                .andExpect(jsonPath("$.birthDate").value("1990-06-10"));
    }

    @Test
    @DisplayName("Should be able to POST successfully an new User")
    void testCreateUserSuccess() throws Exception {
        UserDTO user = createUserDTO();

        when(userService.saveUser(any(UserDTO.class)))
                .thenAnswer(savedUser -> {
                    user.setId("333");
                    return user;
                });

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "firstName": "John",
                                    "lastName": "Doe",
                                    "address": "123 Main St",
                                    "birthDate": "1990-06-10"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value(USER_NAME))
                .andExpect(jsonPath("$.lastName").value(LAST_NAME))
                .andExpect(jsonPath("$.address").value(ADDRESS))
                .andExpect(jsonPath("$.birthDate").value("1990-06-10"));
    }

    @Test
    @DisplayName("Should be able to GET successfully all Users")
    void testGetUsers() throws Exception {
        UserDTO user1 = createUserDTO();
        UserDTO user2 = createUserDTO();
        user2.setFirstName("John Jr");
        user2.setLastName("Doe the Second");

        List<UserDTO> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value(USER_NAME))
                .andExpect(jsonPath("$[0].lastName").value(LAST_NAME))
                .andExpect(jsonPath("$[0].address").value(ADDRESS))
                .andExpect(jsonPath("$[0].birthDate").value("1990-06-10"))
                .andExpect(jsonPath("$[1].firstName").value("John Jr"))
                .andExpect(jsonPath("$[1].lastName").value("Doe the Second"));
    }

    @Test
    @DisplayName("Should be able to GET successfully an user by ID")
    void testGetUserByIdFound() throws Exception {
        UserDTO user = createUserDTOWithId();
        when(userService.getUserById(ID)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/{id}", ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(USER_NAME))
                .andExpect(jsonPath("$.lastName").value(LAST_NAME))
                .andExpect(jsonPath("$.address").value(ADDRESS))
                .andExpect(jsonPath("$.birthDate").value("1990-06-10"));
    }

    @Test
    @DisplayName("Should return HTTP Status Not found - when user doesn't exist")
    void testGetUserByIdNotFound() throws Exception {
        when(userService.getUserById(ID)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/{id}", ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("Should be able to delete an user")
    void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/api/users/{id}", ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

    }

    @Test
    @DisplayName("Should be able to delete an user")
    void testDeleteUserThrowsExceptionWhenUserDoesNotExist() throws Exception {
        doThrow(new EntityNotFoundException("ID not found")).when(userService).deleteUser(ID);

        mockMvc.perform(delete("/api/users/{id}", ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

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
