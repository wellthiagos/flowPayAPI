package com.flowpayapi.domain.account;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AccountDTO(@NotNull(message = "A data de vencimento deve ser informada.") LocalDate dataVencimento,
		                 LocalDate dataPagamento,
		                 @NotNull(message = "O valor deve ser informado") BigDecimal valor,
					     @NotBlank(message = "A descrição deve ser informada.") String descricao,
					     @NotNull(message = "A situação deve ser informada") PaymentStatus paymentStatus) {
}