package com.knowledgebase.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CategoryDto {
    private Long categoryId;
    private String name;
    private String description;
}
