package faang.school.accountservice.validator.tariff;


import faang.school.accountservice.exception.tariff.TariffNotFoundException;
import faang.school.accountservice.model.tariff.Tariff;
import faang.school.accountservice.repository.TariffRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class TariffValidatorTest {

    @InjectMocks
    private TariffValidator tariffValidator;

    @Mock
    private TariffRepository tariffRepository;

    @Test
    void validateTariffExists_ShouldThrowException_WhenTariffNotFound() {
        Long tariffId = 1L;
        Mockito.when(tariffRepository.findById(tariffId)).thenReturn(Optional.empty());

        TariffNotFoundException exception = assertThrows(
                TariffNotFoundException.class,
                () -> tariffValidator.validateTariffExists(tariffId)
        );

        assertEquals("Tariff with id: " + tariffId + " does not exist", exception.getMessage());
        Mockito.verify(tariffRepository).findById(tariffId);
    }

    @Test
    void validateTariffExists_ShouldReturnTariff_WhenTariffExists() {
        Long tariffId = 1L;
        Tariff tariff = new Tariff();
        tariff.setId(tariffId);

        Mockito.when(tariffRepository.findById(tariffId)).thenReturn(Optional.of(tariff));

        Tariff result = tariffValidator.validateTariffExists(tariffId);

        assertNotNull(result);
        assertEquals(tariffId, result.getId());
        Mockito.verify(tariffRepository).findById(tariffId);
    }
}