package com.sandbox.jasperpdfjava21.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.util.HashMap;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    @PostConstruct
    public void init() {
        LOGGER.info("Initializing TransactionBean and pre-compiling Jasper report template...");
        try {
            InputStream reportStream = TransactionBean.class.getClassLoader()
                    .getResourceAsStream("reports/reportbankSummary.jrxml");

            if (reportStream != null) {
                this.cachedJasperReport = JasperCompileManager.compileReport(reportStream);
            } else {
                LOGGER.error("CRITICAL ERROR: Could not find reports/reportbankSummary.jrxml in the classpath");
            }
        } catch (Exception e) {
            LOGGER.error("FATAL ERROR during report compilation in init: {}", e.getMessage(), e);
        }
    }

    public StreamedContent getFile() {
        LOGGER.info("Starting Bank Summary report generation process using JDBC Connection...");

        EntityManager em = emf.createEntityManager();
        try {
            if (cachedJasperReport == null) {
                LOGGER.error("CRITICAL ERROR: JasperReport is not compiled.");
                return null;
            }

            // Extract java.sql.Connection safely using Hibernate's Session doReturningWork
            // API
            Connection connection = em.unwrap(org.hibernate.Session.class)
                    .doReturningWork(conn -> conn);

            // Fill report using the SQL query defined in your JRXML file
            JasperPrint jasperPrint = JasperFillManager.fillReport(cachedJasperReport, new HashMap<>(), connection);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, byteArrayOutputStream);
            byte[] pdfBytes = byteArrayOutputStream.toByteArray();

            LOGGER.info("Report generated successfully. Size: {} bytes", pdfBytes.length);

            return DefaultStreamedContent.builder()
                    .name("reportbankSummary.pdf")
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
}
