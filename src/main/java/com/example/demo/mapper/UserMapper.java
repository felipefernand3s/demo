package com.example.demo.mapper;

import com.example.demo.dto.UserDTO;
import com.example.demo.entity.User;
import org.mapstruct.Mapper;

import java.time.LocalDate;
import java.util.Date;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = org.mapstruct.factory.Mappers.getMapper(UserMapper.class);

    UserDTO toDTO(User user);

    User toEntity(UserDTO userDTO);

    default LocalDate map(Date date) {
        return DateMapper.toLocalDate(date);
    }

    default Date map(LocalDate localDate) {
        return DateMapper.toDate(localDate);
    }

}
