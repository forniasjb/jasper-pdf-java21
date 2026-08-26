package com.sandbox.jasperpdfjava21.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "transactions")
@NamedQueries({
        @NamedQuery(name = "Transaction.findByTicketNo", query = "SELECT t FROM Transaction t WHERE t.ticketNo = :ticketNo"),
        @NamedQuery(name = "Transaction.findAll", query = "SELECT t FROM Transaction t"),
        @NamedQuery(name = "Transaction.findLatestAmount", query = "SELECT t.amount FROM Transaction t ORDER BY t.adviceDate DESC")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Transaction implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "institution_name", nullable = false, length = 255)
    private String institutionName;

    @Column(name = "analysis_code", nullable = false, length = 50)
    private String analysisCode;

    @Column(name = "ticket_no", nullable = false, unique = true, length = 50)
    private String ticketNo;

    @Column(name = "advice_date", nullable = false)
    private LocalDate adviceDate;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "particulars", nullable = false, columnDefinition = "TEXT")
    private String particulars;

    @Column(name = "signatory_name", nullable = false, length = 100)
    private String signatoryName;

    @Column(name = "signatory_designation", nullable = false, length = 100)
    private String signatoryDesignation;
}
