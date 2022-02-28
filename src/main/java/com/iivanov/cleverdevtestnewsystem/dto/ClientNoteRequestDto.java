package com.iivanov.cleverdevtestnewsystem.dto;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Value
@Builder
public class ClientNoteRequestDto {

    @NotBlank String agency;

    @NotNull LocalDate dateFrom;

    @NotNull LocalDate dateTo;

    @NotBlank String clientGuid;
}
