package com.amam.wizardschool.mapper;

import com.amam.wizardschool.dto.AvatarDto;
import com.amam.wizardschool.model.Avatar;
import org.springframework.stereotype.Component;

@Component
public class AvatarMapper {

    public AvatarDto toDto(Avatar avatar) {
        AvatarDto avatarDto = new AvatarDto();
        avatarDto.setId(avatar.getId());
        avatarDto.setStudentName(avatar.getStudent().getName());
        avatarDto.setURI("/student/%s/avatar".formatted(avatar.getId()));

        return avatarDto;
    }
}
