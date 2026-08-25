package com.sandbox.com.jasperpdfjava21.daoImpl;

import java.util.List;

import com.sandbox.com.jasperpdfjava21.dao.BrandDao;
import com.sandbox.com.jasperpdfjava21.entity.Brand;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class BrandDaoImpl implements BrandDao {

    @PersistenceContext(unitName = "primefacesPU")
    private EntityManager em;

    @Override
    public List<Brand> findAll() {
        return em.createNamedQuery("Brand.findAll", Brand.class).getResultList();
    }
}
