package com.iivanov.cleverdevtestnewsystem.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iivanov.cleverdevtestnewsystem.util.Constants;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Value
@Builder
public class ClientResponseDto {
    String agency;
    String guid;
    String firstName;
    String lastName;
    String status;
    LocalDate dateOfBirthday;
    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    LocalDateTime createdDateTime;
}
