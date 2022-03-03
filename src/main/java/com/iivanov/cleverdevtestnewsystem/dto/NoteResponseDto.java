package com.iivanov.cleverdevtestnewsystem.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iivanov.cleverdevtestnewsystem.util.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NoteResponseDto {
    private String comments;
    private String guid;
    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime modifiedDateTime;
    private String clientGuid;
    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime createdDateTime;
    private String loggedUser;
}
