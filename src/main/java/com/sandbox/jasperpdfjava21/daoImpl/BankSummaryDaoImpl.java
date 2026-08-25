package com.sandbox.jasperpdfjava21.daoImpl;

import java.math.BigDecimal;
import java.util.List;

import com.sandbox.jasperpdfjava21.dao.BankSummaryDao;
import com.sandbox.jasperpdfjava21.entity.BankSummary;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

@ApplicationScoped
public class BankSummaryDaoImpl implements BankSummaryDao {

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("primefacesPU");

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    @Override
    public List<BankSummary> findAll() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<BankSummary> query = em.createNamedQuery("BankSummary.findAll", BankSummary.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<BankSummary> findByAccountCode(String accountCode) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<BankSummary> query = em.createNamedQuery("BankSummary.findByAccountCode", BankSummary.class);
            query.setParameter("accountCode", accountCode);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<BankSummary> findByBankName(String bankName) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<BankSummary> query = em.createNamedQuery("BankSummary.findByBankName", BankSummary.class);
            query.setParameter("bankName", bankName);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<BankSummary> findByStatus(String status) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<BankSummary> query = em.createNamedQuery("BankSummary.findByStatus", BankSummary.class);
            query.setParameter("status", status);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<BankSummary> findByCategory(String category) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<BankSummary> query = em.createNamedQuery("BankSummary.findByCategory", BankSummary.class);
            query.setParameter("category", category);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<BankSummary> findByAccountCodeAndBankName(String accountCode, String bankName) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<BankSummary> query = em.createNamedQuery("BankSummary.findByAccountCodeAndBankName",
                    BankSummary.class);
            query.setParameter("accountCode", accountCode);
            query.setParameter("bankName", bankName);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<BankSummary> findByStatusAndCategory(String status, String category) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<BankSummary> query = em.createNamedQuery("BankSummary.findByStatusAndCategory",
                    BankSummary.class);
            query.setParameter("status", status);
            query.setParameter("category", category);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<BankSummary> findByDateRange(Long startId, Long endId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<BankSummary> query = em.createNamedQuery("BankSummary.findByDateRange", BankSummary.class);
            query.setParameter("startId", startId);
            query.setParameter("endId", endId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<BankSummary> findByClosingBalanceGreaterThan(BigDecimal balance) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<BankSummary> query = em.createNamedQuery("BankSummary.findByClosingBalanceGreaterThan",
                    BankSummary.class);
            query.setParameter("balance", balance);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Long countByStatus(String status) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Long> query = em.createNamedQuery("BankSummary.countByStatus", Long.class);
            query.setParameter("status", status);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public int updateStatusByAccountCode(String accountCode, String newStatus) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            int updatedCount = em.createNamedQuery("BankSummary.updateStatusByAccountCode")
                    .setParameter("newStatus", newStatus)
                    .setParameter("accountCode", accountCode)
                    .executeUpdate();
            em.getTransaction().commit();
            return updatedCount;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public int deleteByAccountCode(String accountCode) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            int deletedCount = em.createNamedQuery("BankSummary.deleteByAccountCode")
                    .setParameter("accountCode", accountCode)
                    .executeUpdate();
            em.getTransaction().commit();
            return deletedCount;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public BankSummary findById(Long transactionId) {
        EntityManager em = getEntityManager();
        try {
            return em.find(BankSummary.class, transactionId);
        } finally {
            em.close();
        }
    }

    @Override
    public BankSummary save(BankSummary bankSummary) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            if (bankSummary.getTransactionId() == null) {
                em.persist(bankSummary);
            } else {
                bankSummary = em.merge(bankSummary);
            }
            em.getTransaction().commit();
            return bankSummary;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(BankSummary bankSummary) {
        if (bankSummary != null && bankSummary.getTransactionId() != null) {
            deleteById(bankSummary.getTransactionId());
        }
    }

    @Override
    public void deleteById(Long transactionId) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            BankSummary entity = em.find(BankSummary.class, transactionId);
            if (entity != null) {
                em.remove(entity);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean existsById(Long transactionId) {
        return findById(transactionId) != null;
    }
}
