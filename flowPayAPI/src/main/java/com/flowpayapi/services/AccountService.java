package com.flowpayapi.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flowpayapi.domain.account.Account;
import com.flowpayapi.domain.account.AccountDTO;
import com.flowpayapi.domain.account.PaymentStatus;
import com.flowpayapi.exceptions.AccountException;
import com.flowpayapi.repositories.AccountRepository;

@Service
public class AccountService {

	@Autowired
	private AccountRepository accountRepository;

	@Transactional
	public Account saveAccount(AccountDTO accountDTO) throws AccountException {
		Account account = accountDtoToObject(accountDTO);
		try {
			validatingPayment(account);
			account = accountRepository.save(account);
		} catch (Exception e) {
			throw new AccountException("Erro ao inserir: " + e);
		}
		return account;
	}

	@Transactional
	public Account updateAccount(UUID id, AccountDTO accountDTO) throws AccountException {
		Account account = accountDtoToObject(accountDTO);
		try {
			account.setId(id);
			validatingPayment(account);
			account = accountRepository.save(account);
		} catch (Exception e) {
			throw new AccountException("Erro ao atualizar: " + e);
		}
		return account;
	}

	@Transactional
	public Account updatePaymentStatus(UUID id, PaymentStatus paymentStatus) throws AccountException {
		Account account = null;
		try {
			Optional<Account> accountSave = accountRepository.findById(id);

			if (!accountSave.isPresent())
				throw new AccountException("Conta não encontrada: ");
			
			accountSave.get().setPaymentStatus(paymentStatus);
			validatingPayment(accountSave.get());
			
			accountRepository.save(accountSave.get());
			
			account = accountSave.get();
		} catch (Exception e) {
			throw new AccountException("Erro ao inserir: " + e);
		}
		return account;
	}

	public Account findById(UUID id) throws AccountException {
		Optional<Account> account = accountRepository.findById(id);

		if (!account.isPresent())
			throw new AccountException("Conta não encontrada: ");

		return account.get();
	}

	public Page<Account> findByDataVencimentoOrDescricao(Pageable pageable, LocalDate dataVencimento,
			String descricao) throws AccountException {

		Page<Account> accounts = null;
		
		if (dataVencimento == null && descricao == null) {
			throw new AccountException("Informe um dos parametros: Data de vencimento ou descricao ");
		}

		if (dataVencimento != null && descricao != null) {
			accounts = accountRepository.findAllByDataVencimentoAndDescricao(dataVencimento, descricao, pageable);
		} else if (dataVencimento != null) {
			accounts = accountRepository.findAllByDataVencimento(dataVencimento, pageable);
		} else if (descricao != null) {
			accounts = accountRepository.findAllByDescricao(descricao, pageable);
		}

		return accounts;

	}
	
	public Page<Account> findAll(Pageable pageable) throws AccountException {
		Page<Account> accounts = accountRepository.findAll(pageable);
		return accounts;

	}

	public BigDecimal sumByDateIniAndDateFim(LocalDate initialDate, LocalDate finalDate) {
		 List<Account> accounts = accountRepository.findAllByDataPagamentoBetween(initialDate, finalDate);
	     BigDecimal sum = accounts.stream()
	                .filter(account -> account.getPaymentStatus() == PaymentStatus.RECEBIDO)
	                .map(Account::getValor)
	                .reduce(BigDecimal.ZERO, BigDecimal::add);
	     
	     return sum;
	}
	
	@Transactional
	public void importAccounts(List<AccountDTO> AccountDTO) {
		for (AccountDTO dto : AccountDTO) {
			Account account = accountDtoToObject(dto);
			accountRepository.save(account);
		}
	}

	private Account accountDtoToObject(AccountDTO dto) {
		Account account = new Account();
		account.setDataVencimento(dto.dataVencimento());
		account.setDataPagamento(dto.dataPagamento());
		account.setDescricao(dto.descricao());
		account.setValor(dto.valor());
		account.setPaymentStatus(dto.paymentStatus());

		return account;
	}
	
	public void validatingPayment(Account account) throws AccountException {
		
		if(account.getPaymentStatus() == PaymentStatus.PENDENTE)
			account.setDataPagamento(null);
		
		if(account.getPaymentStatus() == PaymentStatus.RECEBIDO 
				&& account.getDataPagamento() == null)
			account.setDataPagamento(LocalDate.now());
		
		if(account.getDataPagamento() != null && account.getPaymentStatus() == PaymentStatus.PENDENTE )
			account.setPaymentStatus(PaymentStatus.RECEBIDO);
	}

}