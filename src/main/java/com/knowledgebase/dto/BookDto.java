package com.knowledgebase.dto;

import lombok.*;

@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
@ToString
public class BookDto {
    private Long bookId;
    private String title;
    private String author;
    private String isbn;
    private Integer availableStock;
}
