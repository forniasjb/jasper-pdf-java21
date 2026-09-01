package com.sandbox.jasperpdfjava21.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sandbox.jasperpdfjava21.daoImpl.TransactionDaoImpl;
import com.sandbox.jasperpdfjava21.utils.NumberToWordsConverter;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

@Named("transactionBean")
@ViewScoped
public class TransactionBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionBean.class);

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("primefacesPU");
    private JasperReport cachedJasperReport;
    private TransactionDaoImpl transactionDao = new TransactionDaoImpl();

    @PostConstruct
    public void init() {
        LOGGER.info("Initializing TransactionBean and pre-compiling Jasper report template...");
        try {
            InputStream reportStream = TransactionBean.class.getClassLoader()
                    .getResourceAsStream("reports/Financial_Accounting_Department_Debit_Advice.jrxml");
            if (reportStream != null) {
                this.cachedJasperReport = JasperCompileManager.compileReport(reportStream);
            } else {
                LOGGER.error(
                        "CRITICAL ERROR: Could not find reports/Financial_Accounting_Department_Debit_Advice.jrxml in the classpath");
            }
        } catch (Exception e) {
            LOGGER.error("FATAL ERROR during report compilation in init: {}", e.getMessage(), e);
        }
    }

    // third method to generate report using JDBC Connection and convert amount to
    // words
    public StreamedContent getFile() {
        LOGGER.info(
                "Starting Financial_Accounting_Department_Debit_Advice report generation process using JDBC Connection...");

        EntityManager em = emf.createEntityManager();
        try {
            if (cachedJasperReport == null) {
                LOGGER.error("CRITICAL ERROR: JasperReport is not compiled.");
                return null;
            }

            java.sql.Connection connection = em.unwrap(org.hibernate.Session.class)
                    .doReturningWork(conn -> conn);

            // Fetch the amount from the database and convert it into words for the report
            // parameter
            java.math.BigDecimal totalAmount = java.math.BigDecimal.ZERO;
            try {
                List<java.math.BigDecimal> amounts = em
                        .createNamedQuery("Transaction.findLatestAmount", java.math.BigDecimal.class)
                        .setMaxResults(1)
                        .getResultList();

                if (!amounts.isEmpty() && amounts.get(0) != null) {
                    totalAmount = amounts.get(0);
                }
            } catch (Exception ex) {
                LOGGER.warn("Could not fetch amount for words conversion, defaulting to ZERO: {}", ex.getMessage());
            }

            // Convert the numeric amount to words using the utility helper
            String amountInWords = NumberToWordsConverter.convertAmountToWords(totalAmount);

            // Pass the converted amount in words into the JasperReports parameter map
            HashMap<String, Object> parameters = new HashMap<>();
            parameters.put("AMOUNT_IN_WORDS", amountInWords);

            // Fill report using the SQL query and parameters defined in your JRXML file
            JasperPrint jasperPrint = JasperFillManager.fillReport(cachedJasperReport, parameters, connection);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, byteArrayOutputStream);
            byte[] pdfBytes = byteArrayOutputStream.toByteArray();

            LOGGER.info("Ticket report generated successfully. Size: {} bytes",
                    pdfBytes.length);

            return DefaultStreamedContent.builder()
                    .name("Ticket.pdf")
                    .contentType("application/pdf")
                    .stream(() -> new ByteArrayInputStream(pdfBytes))
                    .build();

        } catch (Exception e) {
            LOGGER.error("FATAL ERROR during report generation: {}", e.getMessage(), e);
            return null;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    // First Method to generate report using JDBC Connection
    // public StreamedContent getFile() {
    // LOGGER.info(
    // "Starting Financial_Accounting_Department_Debit_Advice report generation
    // process using JDBC Connection...");

    // EntityManager em = emf.createEntityManager();
    // try {
    // if (cachedJasperReport == null) {
    // LOGGER.error("CRITICAL ERROR: JasperReport is not compiled.");
    // return null;
    // }

    // java.sql.Connection connection = em.unwrap(org.hibernate.Session.class)
    // .doReturningWork(conn -> conn);

    // // Fill report using the SQL query defined in your JRXML file
    // JasperPrint jasperPrint = JasperFillManager.fillReport(cachedJasperReport,
    // new HashMap<>(), connection);

    // ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    // JasperExportManager.exportReportToPdfStream(jasperPrint,
    // byteArrayOutputStream);
    // byte[] pdfBytes = byteArrayOutputStream.toByteArray();

    // LOGGER.info("Financial_Accounting_Department_Debit_Advice report generated
    // successfully. Size: {} bytes",
    // pdfBytes.length);

    // return DefaultStreamedContent.builder()
    // .name("Financial_Accounting_Department_Debit_Advice.pdf")
    // .contentType("application/pdf")
    // .stream(() -> new ByteArrayInputStream(pdfBytes))
    // .build();

    // } catch (Exception e) {
    // LOGGER.error("FATAL ERROR during report generation: {}", e.getMessage(), e);
    // return null;
    // } finally {
    // if (em != null && em.isOpen()) {
    // em.close();
    // }
    // }
    // }

    // Second Method to generate report using JasperPrint and
    // JRBeanCollectionDataSource

    // public StreamedContent getFile() {
    // LOGGER.info(
    // "Starting Financial_Accounting_Department_Debit_Advice report generation
    // process using JREmptyDataSource...");

    // try {
    // if (cachedJasperReport == null) {
    // LOGGER.error("CRITICAL ERROR: JasperReport is not compiled.");
    // return null;
    // }
    // // Use an empty data source so it relies on the template's embedded values
    // net.sf.jasperreports.engine.JREmptyDataSource dataSource = new
    // net.sf.jasperreports.engine.JREmptyDataSource();
    // // Fill report with the empty data source (no connection needed)
    // JasperPrint jasperPrint = JasperFillManager.fillReport(cachedJasperReport,
    // new HashMap<>(), dataSource);
    // ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    // JasperExportManager.exportReportToPdfStream(jasperPrint,
    // byteArrayOutputStream);
    // byte[] pdfBytes = byteArrayOutputStream.toByteArray();

    // LOGGER.info("Report generated successfully. Size: {} bytes",
    // pdfBytes.length);

    // return DefaultStreamedContent.builder()
    // .name("Financial_Accounting_Department_Debit_Advice.pdf")
    // .contentType("application/pdf")
    // .stream(() -> new ByteArrayInputStream(pdfBytes))
    // .build();

    // } catch (Exception e) {
    // LOGGER.error("FATAL ERROR during report generation: {}", e.getMessage(), e);
    // return null;
    // }
    // }
}
