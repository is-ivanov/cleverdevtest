package com.iivanov.cleverdevtestnewsystem.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.iivanov.cleverdevtestnewsystem.util.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientResponseDto {
    private String agency;
    private String guid;
    private String firstName;
    private String lastName;
    private String status;
    @JsonProperty("dob")
    private LocalDate dateOfBirthday;
    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime createdDateTime;
}
