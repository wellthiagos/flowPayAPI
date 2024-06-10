package com.flowpayapi.domain.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterUserDTO(@NotBlank(message = "Informe o login.") String login,
                          @NotBlank(message = "Informe a senha.") String password,
                          @NotNull(message = "Informe a role.") UserRole role) {

}