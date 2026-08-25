package com.sandbox.com.jasperpdfjava21.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory; // Import ng SLF4J

import com.sandbox.com.jasperpdfjava21.dao.BrandDao;
import com.sandbox.com.jasperpdfjava21.entity.Brand;

import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Named("reportBean")
@ViewScoped
public class ReportBean implements Serializable {
    private static final long serialVersionUID = 1L;

    // Logger initialization
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportBean.class);

    @Inject
    private BrandDao brandDao;
    private StreamedContent file;

    public void setFile(StreamedContent file) {
        this.file = file;
    }

    // DOWNLOAD REPORT METHOD
    public StreamedContent getFile() {
        LOGGER.info("Starting report generation process...");

        try {
            // Fetch data
            List<Brand> brandList = brandDao.findAll();
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(brandList);

            // Load report template
            InputStream reportStream = ReportBean.class.getClassLoader()
                    .getResourceAsStream("reports/reportbankSummary.jrxml");

            if (reportStream == null) {
                LOGGER.error("CRITICAL ERROR: Could not find reports/reportbankSummary.jrxml in the classpath");
                return null;
            }

            // Compile, fill, and export report
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap<>(), dataSource);

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
        }
    }

    // VIEW REPORT METHOD
    public void viewBrandPerGrouping() {
        LOGGER.info("Starting 'Brand Per Grouping' report generation for inline viewing...");

        try {
            List<Brand> brandList = brandDao.findAll();
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(brandList, false);

            InputStream reportStream = getClass().getClassLoader()
                    .getResourceAsStream("reports/brandPerGrouping.jrxml");

            if (reportStream == null) {
                LOGGER.error("CRITICAL ERROR: Could not find reports/brandPerGrouping.jrxml in the classpath");
                return;
            }

            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap<>(), dataSource);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, byteArrayOutputStream);
            byte[] pdfBytes = byteArrayOutputStream.toByteArray();

            FacesContext facesContext = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) facesContext
                    .getExternalContext().getResponse();

            response.reset();
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "inline; filename=\"brandPerGrouping.pdf\"");
            response.setContentLength(pdfBytes.length);

            response.getOutputStream().write(pdfBytes);
            response.getOutputStream().flush();

            facesContext.responseComplete();
            LOGGER.info("Brand Per Grouping report sent to browser successfully.");

        } catch (Exception e) {
            LOGGER.error("FATAL ERROR during grouped report viewing: {}", e.getMessage(), e);
        }
    }
    // =================================================================================================

    // DOWNLOAD REPORT METHOD (As Attachment)
    public void downloadBrandPerGrouping() {
        LOGGER.info("Starting 'Brand Per Grouping' report generation for download...");

        try {
            List<Brand> brandList = brandDao.findAll();
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(brandList, false);

            InputStream reportStream = getClass().getClassLoader()
                    .getResourceAsStream("reports/brandPerGrouping.jrxml");

            if (reportStream == null) {
                LOGGER.error("CRITICAL ERROR: Could not find reports/brandPerGrouping.jrxml in the classpath");
                return;
            }

            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap<>(), dataSource);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, byteArrayOutputStream);
            byte[] pdfBytes = byteArrayOutputStream.toByteArray();

            FacesContext facesContext = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();

            response.reset();
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=\"brandPerGrouping.pdf\"");
            response.setContentLength(pdfBytes.length);

            response.getOutputStream().write(pdfBytes);
            response.getOutputStream().flush();

            facesContext.responseComplete();
            LOGGER.info("Brand Per Grouping report downloaded successfully.");

        } catch (Exception e) {
            LOGGER.error("FATAL ERROR during grouped report download: {}", e.getMessage(), e);
        }
    }

}
