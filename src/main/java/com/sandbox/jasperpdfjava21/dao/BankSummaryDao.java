package com.sandbox.jasperpdfjava21.dao;

import java.math.BigDecimal;
import java.util.List;

import com.sandbox.jasperpdfjava21.entity.BankSummary;

public interface BankSummaryDao {

    List<BankSummary> findAll();

    List<BankSummary> findByAccountCode(String accountCode);

    List<BankSummary> findByBankName(String bankName);

    List<BankSummary> findByStatus(String status);

    List<BankSummary> findByCategory(String category);

    List<BankSummary> findByAccountCodeAndBankName(String accountCode, String bankName);

    List<BankSummary> findByStatusAndCategory(String status, String category);

    List<BankSummary> findByDateRange(Long startId, Long endId);

    List<BankSummary> findByClosingBalanceGreaterThan(BigDecimal balance);

    Long countByStatus(String status);

    int updateStatusByAccountCode(String accountCode, String newStatus);

    int deleteByAccountCode(String accountCode);

    BankSummary findById(Long transactionId);

    BankSummary save(BankSummary bankSummary);

    void delete(BankSummary bankSummary);

    void deleteById(Long transactionId);

    boolean existsById(Long transactionId);
}
