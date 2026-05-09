package com.inventiapp.stocktrack;

import com.inventiapp.stocktrack.inventory.domain.model.aggregates.Provider;
import com.inventiapp.stocktrack.inventory.domain.model.commands.CreateProviderCommand;
import com.inventiapp.stocktrack.inventory.domain.model.commands.UpdateProviderCommand;
import com.inventiapp.stocktrack.inventory.domain.model.valueobject.Email;
import com.inventiapp.stocktrack.inventory.domain.model.valueobject.PhoneNumber;
import com.inventiapp.stocktrack.inventory.domain.model.valueobject.Ruc;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProviderServiceTest {

    @Test
    void testCreateProvider() {
        //arrange
        PhoneNumber phoneNumber = new PhoneNumber("978567485");
        Email email = new Email("pedro@hotmail.com");
        Ruc ruc = new Ruc("98765432101");
        CreateProviderCommand command = new CreateProviderCommand("Pedro","Rios","978567485","pedro@hotmail.com","98765432101", 1L);

        //act

        Provider provider= new Provider(command);

        //assert

        assert provider.getFirstName().equals("Pedro");
        assert provider.getLastName().equals("Rios");
        assert provider.getPhoneNumber().equals(phoneNumber);
        assert provider.getEmail().equals(email);
        assert provider.getRuc().equals(ruc);
        assert provider.getOwnerId().equals(1L);

    }

    @Test
    void shouldUpdateProviderInformation() {
        // arrange - creación inicial
        CreateProviderCommand create = new CreateProviderCommand(
                "Pedro",
                "Rios",
                "978567485",
                "pedro@hotmail.com",
                "98765432101",
                1L
        );
        Provider provider = new Provider(create);

        // act - actualizar información
        UpdateProviderCommand update = new UpdateProviderCommand(
                1L, // providerId (unit test: valor arbitrario > 0)
                "Juan",
                "Perez",
                "999888777",
                "juan.perez@example.com",
                "12345678901",
                1L
        );

        provider.updateInformation(update);

        // assert - campos actualizados
        assertEquals("Juan", provider.getFirstName());
        assertEquals("Perez", provider.getLastName());
        assertNotNull(provider.getEmail());
        assertEquals("juan.perez@example.com", provider.getEmail().address());
        assertNotNull(provider.getPhoneNumber());
        assertEquals("999888777", provider.getPhoneNumber().number());
        assertNotNull(provider.getRuc());
        assertEquals("12345678901", provider.getRuc().value());
        // full name concatenado
        assertEquals("Juan Perez", provider.getFullName());
    }

    @Test
    void shouldMarkAsDeletedWithoutChangingData() {
        // arrange
        CreateProviderCommand create = new CreateProviderCommand(
                "Ana",
                "Lopez",
                "987654321",
                "ana.lopez@example.com",
                "11122233344",
                2L
        );
        Provider provider = new Provider(create);

        // act - marcar como eliminado (no hay flag de persistencia aquí; solo registra evento)
        assertDoesNotThrow(() -> provider.markAsDeleted("Duplicated provider"));

        // assert - los datos siguen accesibles e inmutables en lo básico
        assertEquals("Ana", provider.getFirstName());
        assertEquals("Lopez", provider.getLastName());
        assertEquals("Ana Lopez", provider.getFullName());
    }

    @Test
    void shouldThrowWhenCreatingProviderWithInvalidEmail() {
        // arrange - email inválido
        CreateProviderCommand badEmailCommand = new CreateProviderCommand(
                "Luis",
                "Gomez",
                "987654321",
                "not-an-email", // formato inválido
                "11122233344",
                3L
        );

        // act & assert - la construcción del agregado debe lanzar IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> new Provider(badEmailCommand));
    }

}
