package com.flowpayapi.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import com.flowpayapi.domain.account.Account;
import com.flowpayapi.domain.account.AccountDTO;
import com.flowpayapi.domain.account.PaymentStatus;
import com.flowpayapi.exceptions.AccountException;
import com.flowpayapi.repositories.AccountRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class AccountServiceTest {

	@Mock
	private AccountRepository accountRepository;
	
	@Autowired
	@InjectMocks
	private AccountService accountService;
	
	private Account account1;
    private Account account2;
    private Account account3;
    private UUID id1;
    private UUID id2;
    private UUID id3;
    private LocalDate dataVencimento1;
    private LocalDate dataVencimento2;
    private LocalDate dataVencimento3;
    private LocalDate dataPagamento1;
    private LocalDate dataPagamento2;
    private LocalDate dataPagamento3;
    private BigDecimal valor1;
    private BigDecimal valor2;
    private BigDecimal valor3;
    private String descricao1;
    private String descricao2;
    private String descricao3;
    private PaymentStatus paymentStatus1;
    private PaymentStatus paymentStatus2;
    private PaymentStatus paymentStatus3;
    private Pageable pageable;
	
	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		id1 = UUID.randomUUID();
        id2 = UUID.randomUUID();
        id3 = UUID.randomUUID();
        dataVencimento1 = LocalDate.of(2023, 10, 20);
        dataVencimento2 = LocalDate.of(2023, 11, 15);
        dataVencimento3 = LocalDate.of(2023, 12, 10);
        dataPagamento1 = LocalDate.of(2023, 10, 25);
        dataPagamento2 = LocalDate.of(2023, 11, 20);
        dataPagamento3 = LocalDate.of(2023, 12, 15);
        valor1 = new BigDecimal("100.00");
        valor2 = new BigDecimal("200.00");
        valor3 = new BigDecimal("300.00");
        descricao1 = "Conta de Luz";
        descricao2 = "Conta de Água";
        descricao3 = "Conta de Telefone";
        paymentStatus1 = PaymentStatus.RECEBIDO;
        paymentStatus2 = PaymentStatus.PENDENTE;
        paymentStatus3 = PaymentStatus.RECEBIDO;

        account1 = new Account(id1, dataVencimento1, dataPagamento1, valor1, descricao1, paymentStatus1);
        account2 = new Account(id2, dataVencimento2, dataPagamento2, valor2, descricao2, paymentStatus2);
        account3 = new Account(id3, dataVencimento3, dataPagamento3, valor3, descricao3, paymentStatus3);
        pageable = PageRequest.of(0, 10);
	}
	
	@Test
    public void testCadastrarConta() throws AccountException {
		LocalDate dataVencimento = LocalDate.of(2023, 10, 26);
		LocalDate dataPagamento = LocalDate.of(2023, 10, 27);
		String descricao = "Agua";
		BigDecimal valor = new BigDecimal(150);
		PaymentStatus paymentStatus = PaymentStatus.RECEBIDO;
		
		Account account = new Account(dataVencimento, dataPagamento, new BigDecimal(150), "Agua", PaymentStatus.RECEBIDO);
		AccountDTO accountDTO = new AccountDTO(dataVencimento, dataPagamento, valor, descricao, paymentStatus);

        when(accountRepository.save(account)).thenReturn(account);

        Account contaCadastrada = accountService.saveAccount(accountDTO);

        assertEquals(account, contaCadastrada);
        verify(accountRepository, times(1)).save(account);
    }
	
	@Test
	public void testUpdateAccount() throws AccountException {
		
		UUID id = UUID.randomUUID();
		LocalDate dataVencimento = LocalDate.of(2023, 10, 26);
		LocalDate dataPagamento = LocalDate.of(2023, 10, 27);
		String descricao = "Agua";
		BigDecimal valor = new BigDecimal(150);
		PaymentStatus paymentStatus = PaymentStatus.RECEBIDO;
		
		Account account = new Account(id, dataVencimento, dataPagamento, valor, descricao, paymentStatus);
		AccountDTO accountDTO = new AccountDTO(dataVencimento, dataPagamento, valor, descricao, paymentStatus);
		
		when(accountRepository.save(account)).thenReturn(account);

		Account updatedAccount = accountService.updateAccount(id, accountDTO);

		assertEquals(account, updatedAccount);
		verify(accountRepository, times(1)).save(account);
	}
	
	@Test
	public void testUpdatePaymentStatus() throws AccountException {
		 UUID id = UUID.randomUUID();
		    PaymentStatus paymentStatuUpdate = PaymentStatus.PENDENTE;
		    when(accountRepository.findById(id)).thenReturn(Optional.of(new Account(id, LocalDate.of(2023, 10, 26),
		            LocalDate.of(2023, 10, 27), new BigDecimal(150), "Agua", PaymentStatus.RECEBIDO)));
		    Account accountUpdate = accountService.updatePaymentStatus(id, paymentStatuUpdate);
		    assertEquals(paymentStatuUpdate, accountUpdate.getPaymentStatus());
		    verify(accountRepository, times(1)).save(accountUpdate);
	}
	
	@Test
    void testFindAllByDataVencimentoAndDescricao() {
        when(accountRepository.findAllByDataVencimentoAndDescricao(dataVencimento1, descricao1, pageable))
                .thenReturn(new PageImpl<>(Arrays.asList(account1), pageable, 1));

        Page<Account> foundAccounts = accountRepository.findAllByDataVencimentoAndDescricao(dataVencimento1, descricao1,
                pageable);
        
        assertThat(foundAccounts).hasSize(1);
		assertThat(foundAccounts.getContent()).containsExactlyInAnyOrder(account1);
    }

    @Test
    void testFindAllByDataVencimento() {
        when(accountRepository.findAllByDataVencimento(dataVencimento1, pageable))
                .thenReturn(new PageImpl<>(Arrays.asList(account1), pageable, 1));

        Page<Account> foundAccounts = accountRepository.findAllByDataVencimento(dataVencimento1, pageable);

        assertThat(foundAccounts).hasSize(1);
		assertThat(foundAccounts.getContent()).containsExactlyInAnyOrder(account1);
    }

    @Test
    void testFindAllByDescricao() {
        when(accountRepository.findAllByDescricao(descricao1, pageable))
                .thenReturn(new PageImpl<>(Arrays.asList(account1), pageable, 1));

        Page<Account> foundAccounts = accountRepository.findAllByDescricao(descricao1, pageable);

        assertThat(foundAccounts).hasSize(1);
		assertThat(foundAccounts.getContent()).containsExactlyInAnyOrder(account1);
    }

    @Test
    void testFindAll() {
        when(accountRepository.findAll(pageable)).thenReturn(new PageImpl<>(Arrays.asList(account1, account2, account3),
                pageable, 3));

        Page<Account> foundAccounts = accountRepository.findAll(pageable);

        assertEquals(3, foundAccounts.getTotalElements());
        assertThat(foundAccounts.getContent()).containsExactlyInAnyOrder(account1, account2, account3);
    }

    @Test
    void testFindById() {
        when(accountRepository.findById(id1)).thenReturn(Optional.of(account1));

        Optional<Account> foundAccount = accountRepository.findById(id1);
        assertThat(foundAccount).isPresent();
    }
}