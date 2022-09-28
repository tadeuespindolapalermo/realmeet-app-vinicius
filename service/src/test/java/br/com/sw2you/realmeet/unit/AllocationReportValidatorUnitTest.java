package br.com.sw2you.realmeet.unit;

import static br.com.sw2you.realmeet.utils.TestDataCreator.newCreateRoomDTO;
import static br.com.sw2you.realmeet.utils.TestDataCreator.newRoomBuilder;

import br.com.sw2you.realmeet.api.model.CreateRoomDTO;
import br.com.sw2you.realmeet.core.BaseUnitTest;
import br.com.sw2you.realmeet.domain.repository.RoomRepository;
import br.com.sw2you.realmeet.exception.InvalidRequestException;
import br.com.sw2you.realmeet.report.model.AllocationDataReport;
import br.com.sw2you.realmeet.report.validator.AllocationReportValidator;
import br.com.sw2you.realmeet.utils.TestConstants;
import br.com.sw2you.realmeet.validator.RoomValidator;
import br.com.sw2you.realmeet.validator.ValidationError;
import br.com.sw2you.realmeet.validator.ValidatorConstants;
import java.time.LocalDate;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;

public class AllocationReportValidatorUnitTest extends BaseUnitTest {
    private static final int MAX_MONTHS_INTERVAL = 6;
    private static final LocalDate PARAM_DATE_FROM = LocalDate.of(2030, 1, 1);
    private static final LocalDate PARAM_DATE_TO = LocalDate.of(2030, 4, 30);
    private static final String PARAM_EMAIL = "a@.com";

    private AllocationReportValidator victim;

    private AllocationDataReport.Builder reportDataBuilder;

    @BeforeEach
    void setupEach() {
        victim = new AllocationReportValidator(MAX_MONTHS_INTERVAL);
        reportDataBuilder =
            AllocationDataReport.newBuilder().dateFrom(PARAM_DATE_FROM).dateTo(PARAM_DATE_TO).email(PARAM_EMAIL);
    }

    @Test
    void testValidateWhenReportDataIsValid() {
        victim.validate(reportDataBuilder.build());
    }

    @Test
    void testValidateWhenEmailIsMissing() {
        var exception = Assertions.assertThrows(
            InvalidRequestException.class,
            () -> victim.validate(reportDataBuilder.email(null).build())
        );
        Assertions.assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
        Assertions.assertEquals(
            new ValidationError(ValidatorConstants.EMAIL, ValidatorConstants.EMAIL.concat(ValidatorConstants.MISSING)),
            exception.getValidationErrors().getError(0)
        );
    }

    @Test
    void testValidateWhenDateFromIsMissing() {
        var exception = Assertions.assertThrows(
            InvalidRequestException.class,
            () -> victim.validate(reportDataBuilder.dateFrom(null).build())
        );
        Assertions.assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
        Assertions.assertEquals(
            new ValidationError(
                ValidatorConstants.DATE_FROM,
                ValidatorConstants.DATE_FROM.concat(ValidatorConstants.MISSING)
            ),
            exception.getValidationErrors().getError(0)
        );
    }

    @Test
    void testValidateWhenDateToIsMissing() {
        var exception = Assertions.assertThrows(
            InvalidRequestException.class,
            () -> victim.validate(reportDataBuilder.dateTo(null).build())
        );
        Assertions.assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
        Assertions.assertEquals(
            new ValidationError(
                ValidatorConstants.DATE_TO,
                ValidatorConstants.DATE_TO.concat(ValidatorConstants.MISSING)
            ),
            exception.getValidationErrors().getError(0)
        );
    }

    @Test
    void testValidateWhenDatesAreInverted() {
        var exception = Assertions.assertThrows(
            InvalidRequestException.class,
            () -> victim.validate(reportDataBuilder.dateFrom(PARAM_DATE_TO).dateTo(PARAM_DATE_FROM).build())
        );
        Assertions.assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
        Assertions.assertEquals(
            new ValidationError(
                ValidatorConstants.DATE_FROM,
                ValidatorConstants.DATE_FROM.concat(ValidatorConstants.INCONSISTENT)
            ),
            exception.getValidationErrors().getError(0)
        );
    }

    @Test
    void testValidateWhenDatesExceedsInterval() {
        var exception = Assertions.assertThrows(
            InvalidRequestException.class,
            () -> victim.validate(reportDataBuilder.dateTo(PARAM_DATE_TO.plusMonths(MAX_MONTHS_INTERVAL + 1)).build())
        );
        Assertions.assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
        Assertions.assertEquals(
            new ValidationError(
                ValidatorConstants.DATE_TO,
                ValidatorConstants.DATE_TO.concat(ValidatorConstants.EXCEEDS_INTERVAL)
            ),
            exception.getValidationErrors().getError(0)
        );
    }
}
