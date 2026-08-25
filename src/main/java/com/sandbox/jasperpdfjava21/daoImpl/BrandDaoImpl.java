package com.sandbox.jasperpdfjava21.daoImpl;

import java.util.List;

import com.sandbox.jasperpdfjava21.dao.BrandDao;
import com.sandbox.jasperpdfjava21.entity.Brand;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

@ApplicationScoped
public class BrandDaoImpl implements BrandDao {

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("primefacesPU");

    @Override
    public List<Brand> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createNamedQuery("Brand.findAll", Brand.class).getResultList();
        } finally {
            em.close();
        }
    }
}
