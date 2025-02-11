package com.example.demo.service;

import com.example.demo.dto.UserDTO;
import com.example.demo.exceptions.EntityNotFoundException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDTO saveUser(UserDTO userDTO) {
        if (userDTO.getId() != null) {
            throw new IllegalArgumentException("ID should not be provided for new users");
        }

        User user = UserMapper.INSTANCE.toEntity(userDTO);
        User savedUser = userRepository.save(user);
        return UserMapper.INSTANCE.toDTO(savedUser);
    }

    public Optional<UserDTO> updateUser(String id, UserDTO userDTO) {
        // Check if the user exists
        return userRepository.findById(id).map(existingUser -> {
            User userEntity = UserMapper.INSTANCE.toEntity(userDTO);

            existingUser.setFirstName(userEntity.getFirstName());
            existingUser.setLastName(userEntity.getLastName());
            existingUser.setAddress(userEntity.getAddress());
            existingUser.setBirthDate(userEntity.getBirthDate());

            User savedUser = userRepository.save(existingUser);

            return UserMapper.INSTANCE.toDTO(savedUser);
        });
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper.INSTANCE::toDTO)
                .collect(Collectors.toUnmodifiableList());
    }

    public Optional<UserDTO> getUserById(String id) {
        return userRepository.findById(id).map(UserMapper.INSTANCE::toDTO);
    }

    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User with ID %s not found".formatted(id));
        }
        userRepository.deleteById(id);
    }

}
