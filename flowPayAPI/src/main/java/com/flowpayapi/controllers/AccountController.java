package com.flowpayapi.controllers;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.flowpayapi.domain.account.Account;
import com.flowpayapi.domain.account.AccountDTO;
import com.flowpayapi.domain.account.PaymentStatusDTO;
import com.flowpayapi.exceptions.AccountException;
import com.flowpayapi.services.AccountService;
import com.flowpayapi.utils.CsvUtils;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/account")
public class AccountController {

	@Autowired
	private AccountService accountService;

	@PostMapping
	public ResponseEntity<Account> saveAccount(@RequestBody @Valid AccountDTO dto) throws AccountException {
		Account account = accountService.saveAccount(dto);
		return ResponseEntity.status(HttpStatus.CREATED).body(account);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Account> updateAccount(@PathVariable UUID id, @Valid @RequestBody AccountDTO dto)
			throws AccountException {
		Account account = accountService.updateAccount(id, dto);
		return ResponseEntity.ok(account);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Account> obterContaPorId(@PathVariable UUID id) throws AccountException {
		Account account = accountService.findById(id);
		return ResponseEntity.ok(account);
	}

	@GetMapping("/all")
	public ResponseEntity<Page<Account>> findAll(
			@PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable)
			throws AccountException {

		Page<Account> accounts = accountService.findAll(pageable);

		return ResponseEntity.ok(accounts);
	}

	@GetMapping
	public ResponseEntity<Page<Account>> findByDataVencimentoOrDescricao(
			@PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataVencimento,
			@RequestParam(required = false) String descricao) throws AccountException {

		Page<Account> accounts = accountService.findByDataVencimentoOrDescricao(pageable, dataVencimento, descricao);

		return ResponseEntity.ok(accounts);
	}

	@PutMapping("/{id}/situacao")
	public ResponseEntity<Account> alterarSituacao(@PathVariable UUID id,
			@RequestBody PaymentStatusDTO paymentStatusDTO) throws AccountException {
		Account account = accountService.updatePaymentStatus(id, paymentStatusDTO.paymentStatus());
		return ResponseEntity.ok(account);
	}

	@GetMapping("/total")
	public ResponseEntity<BigDecimal> sumByDateIniAndDateFim(
			@RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
			@RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal) {

		BigDecimal sum = accountService.sumByDateIniAndDateFim(dataInicial, dataFinal);

		return ResponseEntity.ok(sum);
	}

	@PostMapping("/importar")
	public ResponseEntity<?> importAccounts(@RequestBody MultipartFile file)
			throws IOException, AccountException {
		if(file == null)
			return ResponseEntity.badRequest().body("O Arquivo está vazio");
		
		if (!file.getContentType().equals("text/csv")) {
			return ResponseEntity.badRequest().body("O arquivo deve ser do tipo CSV (text/csv).");
		}

		List<AccountDTO> dtos = CsvUtils.parseCsv(new InputStreamReader(file.getInputStream()));
		accountService.importAccounts(dtos);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
}