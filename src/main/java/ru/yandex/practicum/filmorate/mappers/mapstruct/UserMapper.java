package ru.yandex.practicum.filmorate.mappers.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.filmorate.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(User user);

    @Mapping(source = "login", target = "name")
    User toUserIfNoName(User user);
}
