package com.flowpayapi.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import com.flowpayapi.domain.account.Account;
import com.flowpayapi.domain.account.PaymentStatus;
import com.flowpayapi.repositories.AccountRepository;

@DataJpaTest
@ActiveProfiles("test")
public class AccountRepositoryTest {

	@Autowired
	TestEntityManager entityManager;

	@Autowired
	private AccountRepository accountRepository;

	private Account account1 = null;
	private Account account2 = null;
	private Account account3 = null;
	private Account account4 = null;
	private Account account5 = null;
	private Account account6 = null;

	@BeforeEach
	void setUp() {
		LocalDate dataVencimento = LocalDate.of(2023, 10, 26);
		LocalDate dataVencimento2 = LocalDate.of(2023, 10, 21);
		LocalDate dataPagamento = LocalDate.of(2023, 10, 27);

		account1 = new Account(dataVencimento, dataPagamento, new BigDecimal(150), "Agua", PaymentStatus.RECEBIDO);
		account2 = new Account(dataVencimento, null, new BigDecimal(80), "Luz", PaymentStatus.PENDENTE);
		account3 = new Account(dataVencimento, null, new BigDecimal(100), "Telefone", PaymentStatus.PENDENTE);
		account4 = new Account(dataVencimento, dataPagamento, new BigDecimal(800), "Aluguel", PaymentStatus.RECEBIDO);
		account5 = new Account(dataVencimento2, null, new BigDecimal(500), "Financiamento", PaymentStatus.PENDENTE);
		account6 = new Account(dataVencimento2, dataPagamento, new BigDecimal(300), "Financiamento",
				PaymentStatus.RECEBIDO);

		entityManager.persist(account1);
		entityManager.persist(account2);
		entityManager.persist(account3);
		entityManager.persist(account4);
		entityManager.persist(account5);
		entityManager.persist(account6);
		entityManager.flush();
	}

	@Test
	void findAllByDataVencimentoAndDescricaoSuccess() {

		LocalDate dataVencimento = LocalDate.of(2023, 10, 26);
		String descricao = "Agua";

		Pageable pageable = PageRequest.of(0, 10, Sort.by("dataVencimento"));
		Page<Account> accounts = accountRepository.findAllByDataVencimentoAndDescricao(dataVencimento, descricao,
				pageable);

		assertThat(accounts).hasSize(1);
		assertThat(accounts.getContent()).containsExactlyInAnyOrder(account1);
	}
	
	@Test
	void findAllByDataVencimentoAndDescricaoFail() {

		LocalDate dataVencimento = LocalDate.of(2023, 10, 26);
		String descricao = "Agua2";

		Pageable pageable = PageRequest.of(0, 10, Sort.by("dataVencimento"));
		Page<Account> accounts = accountRepository.findAllByDataVencimentoAndDescricao(dataVencimento, descricao,
				pageable);

		assertThat(accounts).hasSize(0);
	}

	@Test
	void findAllByDataVencimento() {
		LocalDate dataVencimento = LocalDate.of(2023, 10, 21);

		Pageable pageable = PageRequest.of(0, 10, Sort.by("dataVencimento"));
		Page<Account> accounts = accountRepository.findAllByDataVencimento(dataVencimento, pageable);

		assertThat(accounts).hasSize(2);
		assertThat(accounts.getContent()).containsExactlyInAnyOrder(account5, account6);
	}
	
	@Test
	void findAllByDataVencimentoFail() {
		LocalDate dataVencimento = LocalDate.of(2023, 10, 29);

		Pageable pageable = PageRequest.of(0, 10, Sort.by("dataVencimento"));
		Page<Account> accounts = accountRepository.findAllByDataVencimento(dataVencimento, pageable);

		assertThat(accounts).hasSize(0);
	}

	@Test
	void findAllByDescricao() {
		String descricao = "Financiamento";
		Pageable pageable = PageRequest.of(0, 10, Sort.by("dataVencimento"));
		Page<Account> accounts = accountRepository.findAllByDescricao(descricao, pageable);

		assertThat(accounts).hasSize(2);
		assertThat(accounts.getContent()).containsExactlyInAnyOrder(account5, account6);
	}
	
	@Test
	void findAllByDescricaoFail() {
		String descricao = "Financiamento5";
		Pageable pageable = PageRequest.of(0, 10, Sort.by("dataVencimento"));
		Page<Account> accounts = accountRepository.findAllByDescricao(descricao, pageable);

		assertThat(accounts).hasSize(0);
	}

	@Test
	void findAllByDataPagamentoBetween() {
		LocalDate dataInicial = LocalDate.of(2023, 10, 26);
		LocalDate dataFinal = LocalDate.of(2023, 10, 27);

		List<Account> accounts = accountRepository.findAllByDataPagamentoBetween(dataInicial, dataFinal);

		assertThat(accounts).hasSize(3);
		assertThat(accounts).containsExactlyInAnyOrder(account1, account4, account6);
	}
	
	@Test
	void findAllByDataPagamentoBetweenFail() {
		LocalDate dataInicial = LocalDate.of(2023, 10, 30);
		LocalDate dataFinal = LocalDate.of(2023, 10, 30);

		List<Account> accounts = accountRepository.findAllByDataPagamentoBetween(dataInicial, dataFinal);

		assertThat(accounts).hasSize(0);
	}
}
