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

import com.sandbox.jasperpdfjava21.dao.BankSummaryDao;
import com.sandbox.jasperpdfjava21.entity.BankSummary;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Named("transactionBean")
@ViewScoped
public class TransactionBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionBean.class);

    @Inject
    private BankSummaryDao bankSummaryDao;

    @PostConstruct
    public void init() {
        LOGGER.info("Initializing TransactionBean and loading data...");
    }

    // ==========================================
    // DOWNLOAD REPORT METHOD
    // ==========================================
    public StreamedContent getFile() {
        LOGGER.info("Starting Bank Summary report generation process...");

        try {
            List<BankSummary> summaryList = bankSummaryDao.findAll();
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(summaryList);

            InputStream reportStream = TransactionBean.class.getClassLoader()
                    .getResourceAsStream("reports/reportbankSummary.jrxml");

            if (reportStream == null) {
                LOGGER.error("CRITICAL ERROR: Could not find reports/reportbankSummary.jrxml in the classpath");
                return null;
            }

            // 3. Compile, fill, and export report
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap<>(), dataSource);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, byteArrayOutputStream);
            byte[] pdfBytes = byteArrayOutputStream.toByteArray();

            LOGGER.info("Report generated successfully. Size: {} bytes", pdfBytes.length);

            // 4. Return as PrimeFaces StreamedContent
            return DefaultStreamedContent.builder()
                    .name("reportbankSummary.pdf")
                    .contentType("application/pdf")
                    .stream(() -> new ByteArrayInputStream(pdfBytes))
                    .build();

        } catch (Exception e) {
            LOGGER.error("FATAL ERROR during report generation: {}", e.getMessage(), e);
            return null;
        }
    }
}
