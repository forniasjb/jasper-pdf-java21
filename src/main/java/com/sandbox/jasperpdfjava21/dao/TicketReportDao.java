package com.sandbox.jasperpdfjava21.dao;

import java.util.List;

import com.sandbox.jasperpdfjava21.entity.TicketReport;

public interface TicketReportDao {
    List<TicketReport> findAll();
}
