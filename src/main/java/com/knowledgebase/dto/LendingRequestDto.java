package com.knowledgebase.dto;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class LendingRequestDto {
    private LocalDate dateOut;
    private LocalDate dueDate;
    private LocalDate dateReturned;
}
