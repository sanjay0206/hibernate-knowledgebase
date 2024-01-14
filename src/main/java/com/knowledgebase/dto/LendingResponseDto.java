package com.knowledgebase.dto;


import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class LendingResponseDto {
    private long lendingId;
    private LocalDate dateOut;
    private LocalDate dueDate;
    private LocalDate dateReturned;
    private String username;
    private String bookTitle;
}

