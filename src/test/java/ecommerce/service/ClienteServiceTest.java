package ecommerce.service;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ecommerce.entity.Cliente;
import ecommerce.repository.ClienteRepository;

class ClienteServiceTest {

    @Mock
    private ClienteRepository repository;

    @InjectMocks
    private ClienteService service;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cliente = new Cliente(); // Inicialize conforme sua implementação
    }

    @Test
    void testBuscarPorId_Sucesso() {
        Long clienteId = 1L;
        when(repository.findById(clienteId)).thenReturn(Optional.of(cliente));

        Cliente resultado = service.buscarPorId(clienteId);
        assertNotNull(resultado);
        assertEquals(cliente, resultado);
        verify(repository).findById(clienteId);
    }

    @Test
    void testBuscarPorId_ClienteNaoEncontrado() {
        Long clienteId = 1L;
        when(repository.findById(clienteId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.buscarPorId(clienteId);
        });

        assertEquals("Cliente não encontrado", exception.getMessage());
        verify(repository).findById(clienteId);
    }
}
