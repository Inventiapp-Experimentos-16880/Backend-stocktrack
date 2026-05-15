package com.inventiapp.stocktrack.reports.application;

import com.inventiapp.stocktrack.reports.interfaces.rest.resources.ProviderReportResource;
import java.util.List;

public interface ProviderReportService {
    List<ProviderReportResource> getProviderReport();
}