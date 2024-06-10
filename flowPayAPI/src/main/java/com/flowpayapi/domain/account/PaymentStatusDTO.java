package com.flowpayapi.domain.account;

import jakarta.validation.constraints.NotBlank;

public record PaymentStatusDTO(@NotBlank(message = "A situação deve ser informada") PaymentStatus paymentStatus) {
}