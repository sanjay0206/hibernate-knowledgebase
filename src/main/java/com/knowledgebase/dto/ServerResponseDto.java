package com.knowledgebase.dto;

import com.knowledgebase.entities.Status;
import lombok.*;

@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
@ToString
public class ServerResponseDto {
    private Status status;
    private String message;
}
