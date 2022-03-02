package com.iivanov.cleverdevtestnewsystem.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
public class ClientNoteRequestDto {
    String agency;
    LocalDate dateFrom;
    LocalDate dateTo;
    String clientGuid;
}
