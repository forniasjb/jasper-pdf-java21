package com.sandbox.jasperpdfjava21.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bank_summary")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@NamedQueries({
        @NamedQuery(name = "BankSummary.findAll", query = "SELECT b FROM BankSummary b"),
        @NamedQuery(name = "BankSummary.findByAccountCode", query = "SELECT b FROM BankSummary b WHERE b.accountCode = :accountCode"),
        @NamedQuery(name = "BankSummary.findByBankName", query = "SELECT b FROM BankSummary b WHERE b.bankName = :bankName"),
        @NamedQuery(name = "BankSummary.findByStatus", query = "SELECT b FROM BankSummary b WHERE b.status = :status"),
        @NamedQuery(name = "BankSummary.findByCategory", query = "SELECT b FROM BankSummary b WHERE b.category = :category"),
        @NamedQuery(name = "BankSummary.findByAccountCodeAndBankName", query = "SELECT b FROM BankSummary b WHERE b.accountCode = :accountCode AND b.bankName = :bankName"),
        @NamedQuery(name = "BankSummary.findByStatusAndCategory", query = "SELECT b FROM BankSummary b WHERE b.status = :status AND b.category = :category"),
        @NamedQuery(name = "BankSummary.findByDateRange", query = "SELECT b FROM BankSummary b WHERE b.transactionId BETWEEN :startId AND :endId"),
        @NamedQuery(name = "BankSummary.findByClosingBalanceGreaterThan", query = "SELECT b FROM BankSummary b WHERE b.closingBalance > :balance"),
        @NamedQuery(name = "BankSummary.countByStatus", query = "SELECT COUNT(b) FROM BankSummary b WHERE b.status = :status"),
        @NamedQuery(name = "BankSummary.updateStatusByAccountCode", query = "UPDATE BankSummary b SET b.status = :newStatus WHERE b.accountCode = :accountCode"),
        @NamedQuery(name = "BankSummary.deleteByAccountCode", query = "DELETE FROM BankSummary b WHERE b.accountCode = :accountCode")
})
public class BankSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id", nullable = false)
    private Long transactionId;

    @Column(name = "account_code", nullable = false, length = 20)
    private String accountCode;

    @Column(name = "bank_name", nullable = false, length = 255)
    private String bankName;

    @Column(name = "beginning_balance", precision = 18, scale = 2)
    private BigDecimal beginningBalance = BigDecimal.ZERO;

    @Column(name = "debit_amount", precision = 18, scale = 2)
    private BigDecimal debitAmount = BigDecimal.ZERO;

    @Column(name = "credit_amount", precision = 18, scale = 2)
    private BigDecimal creditAmount = BigDecimal.ZERO;

    @Column(name = "closing_balance", precision = 18, scale = 2)
    private BigDecimal closingBalance = BigDecimal.ZERO;

    @Column(name = "status", length = 10)
    private String status = "CR";

    @Column(name = "category", length = 50)
    private String category;
}
