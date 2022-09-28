package br.com.sw2you.realmeet.unit;

import static br.com.sw2you.realmeet.report.enumeration.ReportFormat.*;
import static br.com.sw2you.realmeet.util.Constants.*;
import static br.com.sw2you.realmeet.utils.TestConstants.DEFAULT_ROOM_ID;
import static br.com.sw2you.realmeet.utils.TestConstants.EMAIL_TO;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.sw2you.realmeet.config.JasperReportsConfiguration;
import br.com.sw2you.realmeet.core.BaseUnitTest;
import br.com.sw2you.realmeet.domain.repository.AllocationRepository;
import br.com.sw2you.realmeet.domain.repository.RoomRepository;
import br.com.sw2you.realmeet.exception.InvalidRequestException;
import br.com.sw2you.realmeet.exception.RoomNotFoundException;
import br.com.sw2you.realmeet.report.enumeration.ReportFormat;
import br.com.sw2you.realmeet.report.enumeration.ReportHandlerType;
import br.com.sw2you.realmeet.report.handler.AllocationReportHandler;
import br.com.sw2you.realmeet.report.resolver.ReportHandlerResolver;
import br.com.sw2you.realmeet.report.validator.AllocationReportValidator;
import br.com.sw2you.realmeet.service.ReportCreationService;
import br.com.sw2you.realmeet.service.ReportDispatcherService;
import br.com.sw2you.realmeet.service.RoomService;
import br.com.sw2you.realmeet.util.Constants;
import br.com.sw2you.realmeet.utils.MapperUtils;
import br.com.sw2you.realmeet.utils.TestDataCreator;
import br.com.sw2you.realmeet.validator.RoomValidator;
import br.com.sw2you.realmeet.validator.ValidationError;
import br.com.sw2you.realmeet.validator.ValidatorConstants;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AllocationReportCreationServiceUnitTest extends BaseUnitTest {
    private static final int MAX_MONTHS = 2;

    private ReportCreationService victim;

    @Mock
    private ReportHandlerResolver reportHandlerResolver;

    @Mock
    private ReportDispatcherService reportDispatcherService;

    @Mock
    private AllocationRepository allocationRepository;

    @BeforeEach
    void setupEach() {
        victim = new ReportCreationService(reportHandlerResolver, reportDispatcherService);

        given(reportHandlerResolver.resolverReportHandler(any()))
            .willReturn(
                new AllocationReportHandler(
                    new JasperReportsConfiguration().allocationReport(),
                    allocationRepository,
                    new AllocationReportValidator(MAX_MONTHS)
                )
            );
    }

    @Test
    void testCreateAllocationReportSuccess() {
        victim.createdAllocationReport(
            LocalDate.now().minusDays(5),
            LocalDate.now().minusDays(1),
            EMAIL_TO,
            PDF.name()
        );
        verify(reportDispatcherService).dispatch(any());
    }

    @Test
    void testCreateAllocationReportNoEmail() {
        var exception = assertThrows(
            InvalidRequestException.class,
            () ->
                victim.createdAllocationReport(
                    LocalDate.now().minusDays(5),
                    LocalDate.now().minusDays(1),
                    EMPTY,
                    PDF.name()
                )
        );
        assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
        assertEquals(new ValidationError(EMAIL, EMAIL + MISSING), exception.getValidationErrors().getError(0));
    }

    @Test
    void testCreateAllocationReportDateFromAfterDateTo() {
        var exception = assertThrows(
            InvalidRequestException.class,
            () ->
                victim.createdAllocationReport(
                    LocalDate.now().minusDays(1),
                    LocalDate.now().minusDays(3),
                    EMAIL_TO,
                    PDF.name()
                )
        );
        assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
        assertEquals(
            new ValidationError(DATE_FROM, DATE_FROM + INCONSISTENT),
            exception.getValidationErrors().getError(0)
        );
    }

    @Test
    void testCreateAllocationReportDateIntervalExceedsMax() {
        var exception = assertThrows(
            InvalidRequestException.class,
            () ->
                victim.createdAllocationReport(
                    LocalDate.now().minusMonths(MAX_MONTHS + 2),
                    LocalDate.now().minusMonths(1),
                    EMAIL_TO,
                    PDF.name()
                )
        );
        assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
        assertEquals(
            new ValidationError(DATE_TO, DATE_TO + EXCEEDS_INTERVAL),
            exception.getValidationErrors().getError(0)
        );
    }
}
