package com.sandbox.jasperpdfjava21.daoImpl;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sandbox.jasperpdfjava21.dao.BankSummaryDao;
import com.sandbox.jasperpdfjava21.entity.BankSummary;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@ApplicationScoped
public class BankSummaryDaoImpl implements BankSummaryDao {

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("primefacesPU");

    @Override
    public List<BankSummary> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createNamedQuery("BankSummary.findAll", BankSummary.class).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Generates and fills a JasperPrint object using the findAll() data.
     *
     * @InputStream reportTemplateStream - The compiled .jasper file input stream
     * @Map<String, Object> parameters - Optional parameters to pass into the report
     */
    public JasperPrint generateBankSummaryReport(InputStream reportTemplateStream, Map<String, Object> parameters)
            throws JRException {
        // 1. Fetch the data list using your existing pattern
        List<BankSummary> bankSummaryList = findAll();

        // 2. Wrap the list into a Jasper-compatible collection data source
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(bankSummaryList);

        // 3. Ensure parameters map is initialized
        if (parameters == null) {
            parameters = new HashMap<>();
        }

        // 4. Fill the report template with data and parameters
        return JasperFillManager.fillReport(reportTemplateStream, parameters, dataSource);
    }
}
