package com.iivanov.cleverdevtestnewsystem.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientNoteRequestDto {
    private String agency;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private String clientGuid;
}
