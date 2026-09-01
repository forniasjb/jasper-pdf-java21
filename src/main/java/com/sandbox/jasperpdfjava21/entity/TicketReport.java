package com.sandbox.jasperpdfjava21.entity;

import java.math.BigDecimal;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "TICKET_REPORT", schema = "DDALIB")
@NamedQuery(name = "TicketReport.findAll", query = "SELECT t FROM TicketReport t")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketReport {

    @Id
    @Column(name = "ID")
    private Integer id;

    @Column(name = "TICKET_NO", nullable = false, length = 50)
    private String ticketNo;

    @Column(name = "GL_ACCOUNT_CODE", length = 50)
    private String glAccountCode;

    @Column(name = "GL_ACCOUNT_DESCRIPTION", length = 255)
    private String glAccountDescription;

    @Column(name = "ANALYSIS_CODE", length = 50)
    private String analysisCode;

    @Column(name = "ANALYSIS_DESCRIPTION", length = 255)
    private String analysisDescription;

    @Column(name = "DEBIT", precision = 15, scale = 2)
    private BigDecimal debit;

    @Column(name = "CREDIT", precision = 15, scale = 2)
    private BigDecimal credit;

    @Transient
    private String rcCode = "RC-DEFAULT";

    @Transient
    private String rcDesc = "Responsibility Center";

    @Transient
    private String transType = "DDA";

    @Transient
    private String transTypeDesc = "Demand Deposit Account";

    @Transient
    private String transId = "TRX-001";

    @Transient
    private String transIdDesc = "Standard Transaction";

    @Transient
    private Date transDate = new Date();

    @Transient
    private String preparedBy = "System Admin";

    @Transient
    private String reviewedBy = "Supervisor";

    @Transient
    private String approvedBy = "Manager";

    @Transient
    private String particulars;

    // --- EXPLICIT GETTERS PARA SA LAHAT NG UPPERCASE .JRXML FIELDS ---

    @Transient
    public String getTICKET_NO() {
        return ticketNo != null ? ticketNo : "";
    }

    @Transient
    public String getGL_ACCOUNT_CODE() {
        return glAccountCode != null ? glAccountCode : "";
    }

    @Transient
    public String getGL_ACCOUNT_DESCRIPTION() {
        return glAccountDescription != null ? glAccountDescription : "";
    }

    @Transient
    public String getANALYSIS_CODE() {
        return analysisCode != null ? analysisCode : "";
    }

    @Transient
    public String getANALYSIS_DESCRIPTION() {
        return analysisDescription != null ? analysisDescription : "";
    }

    @Transient
    public BigDecimal getDEBIT() {
        return debit != null ? debit : BigDecimal.ZERO;
    }

    @Transient
    public BigDecimal getCREDIT() {
        return credit != null ? credit : BigDecimal.ZERO;
    }

    @Transient
    public String getRC_CODE() {
        return rcCode != null ? rcCode : "RC-DEFAULT";
    }

    @Transient
    public String getRC_DESC() {
        return rcDesc != null ? rcDesc : "Responsibility Center";
    }

    @Transient
    public String getTRANS_TYPE() {
        return transType != null ? transType : "DDA";
    }

    @Transient
    public String getTRANS_TYPE_DESC() {
        return transTypeDesc != null ? transTypeDesc : "Demand Deposit Account";
    }

    @Transient
    public String getTRANS_ID() {
        return transId != null ? transId : "TRX-001";
    }

    @Transient
    public String getTRANS_ID_DESC() {
        return transIdDesc != null ? transIdDesc : "Standard Transaction";
    }

    @Transient
    public Date getTRANS_DATE() {
        return transDate != null ? transDate : new Date();
    }

    @Transient
    public String getPREPARED_BY() {
        return preparedBy != null ? preparedBy : "System Admin";
    }

    @Transient
    public String getREVIEWED_BY() {
        return reviewedBy != null ? reviewedBy : "Supervisor";
    }

    @Transient
    public String getAPPROVED_BY() {
        return approvedBy != null ? approvedBy : "Manager";
    }

    @Transient
    public String getPARTICULARS() {
        if (particulars != null && !particulars.isEmpty()) {
            return particulars;
        }
        return "Generated transaction report for ticket " + (ticketNo != null ? ticketNo : "");
    }

    // --- MGA UTILITY METHODS ---

    @Transient
    public BigDecimal getNetAmount() {
        BigDecimal debitAmount = debit != null ? debit : BigDecimal.ZERO;
        BigDecimal creditAmount = credit != null ? credit : BigDecimal.ZERO;
        return debitAmount.subtract(creditAmount);
    }

    @Transient
    public String getTransactionType() {
        if (debit != null && debit.signum() > 0) {
            return "DEBIT";
        }
        if (credit != null && credit.signum() > 0) {
            return "CREDIT";
        }
        return "ZERO";
    }

    @Transient
    public boolean isBalanced() {
        if (debit == null && credit == null) {
            return true;
        }
        if (debit == null || credit == null) {
            return false;
        }
        return debit.compareTo(credit) == 0;
    }

    @Transient
    public boolean isZeroAmount() {
        return (debit == null || debit.signum() == 0)
                && (credit == null || credit.signum() == 0);
    }

    @Transient
    public boolean isValid() {
        return ticketNo != null
                && !ticketNo.isBlank()
                && glAccountCode != null
                && !glAccountCode.isBlank()
                && !isZeroAmount();
    }
}
