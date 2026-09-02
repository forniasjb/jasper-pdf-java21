package com.sandbox.jasperpdfjava21.controller;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.primefaces.PrimeFaces;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.sandbox.jasperpdfjava21.daoImpl.TicketReportDaoImpl;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

@Named("ticketReportBean")
@ViewScoped
public class TicketReportBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private TicketReportDaoImpl ticketReportDao = new TicketReportDaoImpl();

    private String reportType;

    // DDSOA Filters (Ticket Number Range)
    private String ticketNoFrom;
    private String ticketNoTo;

    // DTDTS Filters (Account & Analysis Codes)
    private String glAccountCode;
    private String analysisCode;
    private String accountNoFrom;
    private String accountNoTo;

    // Flags to toggle visibility of filter sections on the main page
    private boolean showDtdtsFilters = false;
    private boolean showDdsOaFilters = false;

    // StreamedContent object used for PrimeFaces file download component
    private StreamedContent file;

    /**
     * Handles the initial selection of the report type and determines
     * which modal dialog should be displayed via PrimeFaces callback parameters.
     */
    public void proceed() {
        showDtdtsFilters = false;
        showDdsOaFilters = false;

        boolean showDdsOaDialog = false;
        boolean showDtdtsDialog = false;

        if (reportType == null || reportType.isBlank()) {
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Report Type Required",
                            "Please select a report type."));
        } else if ("DDSOA".equals(reportType)) {
            showDdsOaDialog = true;
        } else if ("DTDTS".equals(reportType)) {
            showDtdtsDialog = true;
        }

        PrimeFaces.current().ajax().addCallbackParam("showDdsOaDialog", showDdsOaDialog);
        PrimeFaces.current().ajax().addCallbackParam("showDtdtsDialog", showDtdtsDialog);
    }

    /**
     * Validates DTDTS modal inputs, compiles/generates the report PDF,
     * and displays the main DTDTS filter section if successful.
     */
    public void continueDtdts() {
        FacesContext context = FacesContext.getCurrentInstance();

        if (accountNoFrom == null || accountNoFrom.isBlank()) {
            context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Required", "Account No. From is required."));
            PrimeFaces.current().ajax().addCallbackParam("validationFailed", true);
            return;
        }

        if (accountNoTo == null || accountNoTo.isBlank()) {
            context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Required", "Account No. To is required."));
            PrimeFaces.current().ajax().addCallbackParam("validationFailed", true);
            return;
        }

        generateReportInternal();

        showDtdtsFilters = true;
        PrimeFaces.current().ajax().addCallbackParam("validationFailed", false);
    }

    /**
     * Validates DDSOA modal inputs, compiles/generates the report PDF,
     * and displays the main DDSOA filter section if successful.
     */
    public void continueDdsOa() {
        FacesContext context = FacesContext.getCurrentInstance();

        if (ticketNoFrom == null || ticketNoFrom.isBlank()) {
            context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Required", "Ticket No. From is required."));
            PrimeFaces.current().ajax().addCallbackParam("validationFailed", true);
            return;
        }

        if (ticketNoTo == null || ticketNoTo.isBlank()) {
            context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Required", "Ticket No. To is required."));
            PrimeFaces.current().ajax().addCallbackParam("validationFailed", true);
            return;
        }

        generateDdsOaReportInternal();

        showDdsOaFilters = true;
        PrimeFaces.current().ajax().addCallbackParam("validationFailed", false);
    }

    /**
     * Internal helper method to build parameters, fetch template,
     * and generate the DDSOA PDF report stream.
     */
    private void generateDdsOaReportInternal() {
        try {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("TICKET_FROM", ticketNoFrom);
            parameters.put("TICKET_TO", ticketNoTo);

            InputStream reportStream = getClass().getResourceAsStream("/reports/Ticket.jasper");

            if (reportStream == null) {
                reportStream = getClass().getResourceAsStream("/reports/Ticket.jrxml");
            }

            if (reportStream == null) {
                throw new RuntimeException(
                        "Report file 'Ticket.jasper' or 'Ticket.jrxml' not found in src/main/resources/reports/");
            }

            JasperPrint jasperPrint = ticketReportDao.generateBankSummaryReport(reportStream, parameters);
            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

            file = DefaultStreamedContent.builder()
                    .name("DDSOA_Report.pdf")
                    .contentType("application/pdf")
                    .stream(() -> new ByteArrayInputStream(pdfBytes))
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                            "Failed to generate DDSOA report: " + e.getMessage()));
        }
    }

    /**
     * Internal helper method to build parameters, fetch template,
     * and generate the DTDTS PDF report stream.
     */
    private void generateReportInternal() {
        try {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("GL_ACCOUNT", glAccountCode);
            parameters.put("ANALYSIS_CODE", analysisCode);
            parameters.put("ACC_FROM", accountNoFrom);
            parameters.put("ACC_TO", accountNoTo);

            InputStream reportStream = getClass().getResourceAsStream("/reports/Ticket.jasper");

            if (reportStream == null) {
                reportStream = getClass().getResourceAsStream("/reports/Ticket.jrxml");
            }

            if (reportStream == null) {
                throw new RuntimeException(
                        "Report file 'Ticket.jasper' or 'Ticket.jrxml' not found in src/main/resources/reports/");
            }

            JasperPrint jasperPrint = ticketReportDao.generateBankSummaryReport(reportStream, parameters);
            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

            file = DefaultStreamedContent.builder()
                    .name("ticket.pdf")
                    .contentType("application/pdf")
                    .stream(() -> new ByteArrayInputStream(pdfBytes))
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                            "Failed to generate DTDTS report: " + e.getMessage()));
        }
    }

    /**
     * Legacy action method for direct execution of DDSOA generation from XHTML.
     */
    public void generateDdsOaReport() {
        generateDdsOaReportInternal();
    }

    /**
     * Legacy action method for direct execution of DTDTS generation from XHTML.
     */
    public void generateReport() {
        generateReportInternal();
    }

    /**
     * Resets all DTDTS input fields.
     */
    public void clearDtdtsFilters() {
        glAccountCode = null;
        analysisCode = null;
        accountNoFrom = null;
        accountNoTo = null;
    }

    /**
     * Resets all DDSOA input fields.
     */
    public void clearDdsOaFilters() {
        ticketNoFrom = null;
        ticketNoTo = null;
    }

    // Getters & Setters
    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getTicketNoFrom() {
        return ticketNoFrom;
    }

    public void setTicketNoFrom(String ticketNoFrom) {
        this.ticketNoFrom = ticketNoFrom;
    }

    public String getTicketNoTo() {
        return ticketNoTo;
    }

    public void setTicketNoTo(String ticketNoTo) {
        this.ticketNoTo = ticketNoTo;
    }

    public String getGlAccountCode() {
        return glAccountCode;
    }

    public void setGlAccountCode(String glAccountCode) {
        this.glAccountCode = glAccountCode;
    }

    public String getAnalysisCode() {
        return analysisCode;
    }

    public void setAnalysisCode(String analysisCode) {
        this.analysisCode = analysisCode;
    }

    public String getAccountNoFrom() {
        return accountNoFrom;
    }

    public void setAccountNoFrom(String accountNoFrom) {
        this.accountNoFrom = accountNoFrom;
    }

    public String getAccountNoTo() {
        return accountNoTo;
    }

    public void setAccountNoTo(String accountNoTo) {
        this.accountNoTo = accountNoTo;
    }

    public boolean isShowDtdtsFilters() {
        return showDtdtsFilters;
    }

    public void setShowDtdtsFilters(boolean showDtdtsFilters) {
        this.showDtdtsFilters = showDtdtsFilters;
    }

    public boolean isShowDdsOaFilters() {
        return showDdsOaFilters;
    }

    public void setShowDdsOaFilters(boolean showDdsOaFilters) {
        this.showDdsOaFilters = showDdsOaFilters;
    }

    public StreamedContent getFile() {
        return file;
    }

    public void setFile(StreamedContent file) {
        this.file = file;
    }
}
