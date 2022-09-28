package br.com.sw2you.realmeet.service;

import static br.com.sw2you.realmeet.report.enumeration.ReportHandlerType.ALLOCATION;

import br.com.sw2you.realmeet.report.enumeration.ReportFormat;
import br.com.sw2you.realmeet.report.enumeration.ReportHandlerType;
import br.com.sw2you.realmeet.report.model.AllocationDataReport;
import br.com.sw2you.realmeet.report.model.GeneratedReport;
import br.com.sw2you.realmeet.report.resolver.ReportHandlerResolver;
import br.com.sw2you.realmeet.util.Constants;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportCreationService {
    private final ReportHandlerResolver reportHandlerResolver;
    private final ReportDispatcherService reportDispatcherService;

    public ReportCreationService(
        ReportHandlerResolver reportHandlerResolver,
        ReportDispatcherService reportDispatcherService
    ) {
        this.reportHandlerResolver = reportHandlerResolver;
        this.reportDispatcherService = reportDispatcherService;
    }

    @Transactional(readOnly = true)
    public void createdAllocationReport(LocalDate dateFrom, LocalDate dateTo, String email, String reportFormatStr) {
        var reportData = AllocationDataReport.newBuilder().dateFrom(dateFrom).dateTo(dateTo).email(email).build();

        createReport(ALLOCATION, email, reportFormatStr, reportData);
    }

    private void createReport(
        ReportHandlerType reportHandlerType,
        String email,
        String reportFormatStr,
        AllocationDataReport reportData
    ) {
        var reportFormat = ReportFormat.fromString(reportFormatStr);
        var reportHandler = reportHandlerResolver.resolverReportHandler(reportHandlerType);
        reportHandler.getReportValidator().validate(reportData);
        var bytes = reportHandler.createReportBytes(reportData, reportFormat);

        reportDispatcherService.dispatch(
            GeneratedReport
                .newBuilder()
                .bytes(bytes)
                .fileName(buildFileName(reportHandlerType, reportFormat))
                .reportFormat(reportFormat)
                .email(email)
                .templateType(reportHandler.getTemplateType())
                .build()
        );
    }

    private String buildFileName(ReportHandlerType reportHandlerType, ReportFormat reportFormat) {
        return Constants.REPORT + reportHandlerType.name().toLowerCase() + reportFormat.getExtension();
    }
}
