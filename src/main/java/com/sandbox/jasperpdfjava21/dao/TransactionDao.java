package com.sandbox.jasperpdfjava21.dao;

import java.util.List;

import com.sandbox.jasperpdfjava21.entity.Transaction;

public interface TransactionDao {
    List<Transaction> findAll();

}
