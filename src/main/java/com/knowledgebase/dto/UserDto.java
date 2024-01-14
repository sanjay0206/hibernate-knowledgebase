package com.knowledgebase.dto;

import com.knowledgebase.entities.UserRole;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UserDto {
    private String username;
    private String password;
    private String email;
    private String phoneNumber;
    private UserRole userRole;
}
