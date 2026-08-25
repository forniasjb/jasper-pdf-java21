package com.sandbox.jasperpdfjava21.dao;

import java.util.List;

import com.sandbox.jasperpdfjava21.entity.BankSummary;

public interface BankSummaryDao {

    List<BankSummary> findAll();
}
