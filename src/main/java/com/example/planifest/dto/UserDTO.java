package com.example.planifest.dto;
import java.util.List;
import com.example.planifest.enums.Role;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private Role role;
    private Long positionId;
    List<Long> taskid;
    

}
