package com.iivanov.cleverdevtestnewsystem.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iivanov.cleverdevtestnewsystem.util.Constants;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class NoteResponseDto {

    String comments;
    String guid;

    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    LocalDateTime modifiedDateTime;

    String clientGuid;

    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    LocalDateTime createdDateTime;

    String loggedUser;
}
