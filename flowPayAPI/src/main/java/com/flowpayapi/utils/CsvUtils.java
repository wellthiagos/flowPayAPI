package com.flowpayapi.utils;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.flowpayapi.domain.account.AccountDTO;
import com.flowpayapi.domain.account.PaymentStatus;
import com.flowpayapi.exceptions.AccountException;

public class CsvUtils {
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	public static List<AccountDTO> parseCsv(Reader reader) throws IOException, AccountException {
		int linha = 0;
		List<AccountDTO> dtos = new ArrayList<>();
		@SuppressWarnings("deprecation")
		CSVParser parser = CSVParser.parse(reader, CSVFormat.DEFAULT.withHeader());
		for (CSVRecord record : parser) {
			linha++;
			LocalDate dataVencimento = LocalDate.parse(record.get("data_vencimento"), DATE_FORMATTER);
			LocalDate dataPagamento = record.get("data_pagamento").isEmpty() ? null : LocalDate.parse(record.get("data_pagamento"), DATE_FORMATTER);
			String descricao = record.get("descricao");
			PaymentStatus paymentStatus = PaymentStatus.valueOf(record.get("situacao"));
			BigDecimal valor = record.get("data_pagamento").isEmpty() ? new BigDecimal(0) :new BigDecimal(record.get("valor"));
			
			if(dataVencimento == null) {
				throw new AccountException(String.format("Erro na linha: [%s] - [data_vencimento] nula ou com formato inválido.", linha));
			}
					
			if(descricao.isEmpty()) {
				throw new AccountException(String.format("Erro na linha: [%s] - [descricao] não deve ser nulo", linha));
			}
			
			if(paymentStatus == null) {
				throw new AccountException(String.format("Erro na linha: [%s] - [situacao] nulo ou inválido", linha));
			}

			AccountDTO accountDTO = new AccountDTO(dataVencimento, dataPagamento, valor, descricao, paymentStatus);
			dtos.add(accountDTO);
		}
		
		return dtos;
	}
}
