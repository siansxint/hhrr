package me.siansxint.hhrr.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

    private long id;

    @NotNull
    @Size(min = 11, max = 11)
    private String cardId;
    @Size(max = 255)
    private String name;

    @Size(max = 255)
    private String email;
}