package br.com.sw2you.realmeet.unit;

import static br.com.sw2you.realmeet.utils.TestDataCreator.newCreateRoomDTO;
import static br.com.sw2you.realmeet.utils.TestDataCreator.newRoomBuilder;

import br.com.sw2you.realmeet.api.model.CreateRoomDTO;
import br.com.sw2you.realmeet.core.BaseUnitTest;
import br.com.sw2you.realmeet.domain.repository.RoomRepository;
import br.com.sw2you.realmeet.exception.InvalidRequestException;
import br.com.sw2you.realmeet.utils.TestConstants;
import br.com.sw2you.realmeet.validator.RoomValidator;
import br.com.sw2you.realmeet.validator.ValidationError;
import br.com.sw2you.realmeet.validator.ValidatorConstants;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;

public class RoomValidatorUnitTest extends BaseUnitTest {
    private RoomValidator victim;

    @Mock
    private RoomRepository roomRepository;

    @BeforeEach
    void setupEach() {
        victim = new RoomValidator(roomRepository);
    }

    @Test
    void testValidateWhenRoomIsValid() {
        victim.validate(newCreateRoomDTO());
    }

    @Test
    void testValidateWhenRoomIsMissing() {
        var exception = Assertions.assertThrows(
            InvalidRequestException.class,
            () -> victim.validate((CreateRoomDTO) newCreateRoomDTO().name(null))
        );
        Assertions.assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
        Assertions.assertEquals(
            new ValidationError(
                ValidatorConstants.ROOM_NAME,
                ValidatorConstants.ROOM_NAME.concat(ValidatorConstants.MISSING)
            ),
            exception.getValidationErrors().getError(0)
        );
    }

    @Test
    void testValidateWhenRoomNameExceedsLength() {
        var exception = Assertions.assertThrows(
            InvalidRequestException.class,
            () ->
                victim.validate(
                    (CreateRoomDTO) newCreateRoomDTO()
                        .name(StringUtils.rightPad("X", ValidatorConstants.ROOM_NAME_MAX_LENGTH + 1, 'x'))
                )
        );
        Assertions.assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
        Assertions.assertEquals(
            new ValidationError(
                ValidatorConstants.ROOM_NAME,
                ValidatorConstants.ROOM_NAME.concat(ValidatorConstants.EXCEEDS_MAX_LENGTH)
            ),
            exception.getValidationErrors().getError(0)
        );
    }

    @Test
    void testValidateWhenSeatsAreMissing() {
        var exception = Assertions.assertThrows(
            InvalidRequestException.class,
            () -> victim.validate((CreateRoomDTO) newCreateRoomDTO().seats(null))
        );
        Assertions.assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
        Assertions.assertEquals(
            new ValidationError(
                ValidatorConstants.ROOM_SEATS,
                ValidatorConstants.ROOM_SEATS.concat(ValidatorConstants.MISSING)
            ),
            exception.getValidationErrors().getError(0)
        );
    }

    @Test
    void testValidateWhenSeatsAreLessThanMinValue() {
        var exception = Assertions.assertThrows(
            InvalidRequestException.class,
            () -> victim.validate((CreateRoomDTO) newCreateRoomDTO().seats(ValidatorConstants.ROOM_SEATS_MIN_VALUE - 1))
        );
        Assertions.assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
        Assertions.assertEquals(
            new ValidationError(
                ValidatorConstants.ROOM_SEATS,
                ValidatorConstants.ROOM_SEATS.concat(ValidatorConstants.BELOW_MAX_VALUE)
            ),
            exception.getValidationErrors().getError(0)
        );
    }

    @Test
    void testValidateWhenSeatsAreThanMaxValue() {
        var exception = Assertions.assertThrows(
            InvalidRequestException.class,
            () -> victim.validate((CreateRoomDTO) newCreateRoomDTO().seats(ValidatorConstants.ROOM_SEATS_MAX_VALUE + 1))
        );
        Assertions.assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
        Assertions.assertEquals(
            new ValidationError(
                ValidatorConstants.ROOM_SEATS,
                ValidatorConstants.ROOM_SEATS.concat(ValidatorConstants.EXCEEDS_MAX_VALUE)
            ),
            exception.getValidationErrors().getError(0)
        );
    }

    @Test
    void testValidateWhenRoomNameIsDuplicate() {
        BDDMockito
            .given(roomRepository.findByNameAndActive(TestConstants.DEFAULT_ROOM_NAME, true))
            .willReturn(Optional.of(newRoomBuilder().build()));

        var exception = Assertions.assertThrows(
            InvalidRequestException.class,
            () -> victim.validate(newCreateRoomDTO())
        );
        Assertions.assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
        Assertions.assertEquals(
            new ValidationError(
                ValidatorConstants.ROOM_NAME,
                ValidatorConstants.ROOM_NAME.concat(ValidatorConstants.DUPLICATE)
            ),
            exception.getValidationErrors().getError(0)
        );
    }
}
