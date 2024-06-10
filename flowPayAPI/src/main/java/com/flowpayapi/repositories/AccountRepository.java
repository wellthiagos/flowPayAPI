package com.flowpayapi.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.flowpayapi.domain.account.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {

	Optional<Account> findById(UUID id);

	@Query("SELECT a FROM account a WHERE a.dataVencimento = :dataVencimento AND LOWER(a.descricao) = LOWER(:descricao) ")
	Page<Account> findAllByDataVencimentoAndDescricao(@Param("dataVencimento") LocalDate dataVencimento,
			@Param("descricao") String descricao, Pageable pageable);

	@Query("SELECT a FROM account a WHERE a.dataVencimento = :dataVencimento ")
	Page<Account> findAllByDataVencimento(@Param("dataVencimento") LocalDate dataVencimento, Pageable pageable);

	@Query("SELECT a FROM account a WHERE LOWER(a.descricao) = LOWER(:descricao) ")
	Page<Account> findAllByDescricao(@Param("descricao") String descricao, Pageable pageable);

	@Query("SELECT a FROM account a WHERE a.dataPagamento BETWEEN :dataInicial AND :dataFinal ")
	List<Account> findAllByDataPagamentoBetween(@Param("dataInicial") LocalDate dataInicial,
			@Param("dataFinal") LocalDate dataFinal);
}