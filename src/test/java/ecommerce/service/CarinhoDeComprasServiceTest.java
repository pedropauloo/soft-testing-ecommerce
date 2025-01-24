package ecommerce.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import ecommerce.entity.CarrinhoDeCompras;
import ecommerce.entity.Cliente;
import ecommerce.repository.CarrinhoDeComprasRepository;

class CarrinhoDeComprasServiceTest {

    @Mock
    private CarrinhoDeComprasRepository repository;

    @InjectMocks
    private CarrinhoDeComprasService service;

    private Cliente cliente;
    private CarrinhoDeCompras carrinho;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cliente = new Cliente();
        carrinho = new CarrinhoDeCompras();
    }

    @Test
    void testBuscarPorCarrinhoIdEClienteId_Sucesso() {
        Long carrinhoId = 1L;
        when(repository.findByIdAndCliente(carrinhoId, cliente)).thenReturn(Optional.of(carrinho));

        CarrinhoDeCompras resultado = service.buscarPorCarrinhoIdEClienteId(carrinhoId, cliente);
        assertNotNull(resultado);
        assertEquals(carrinho, resultado);
        verify(repository).findByIdAndCliente(carrinhoId, cliente);
    }

    @Test
    void testBuscarPorCarrinhoIdEClienteId_CarrinhoNaoEncontrado() {
        Long carrinhoId = 1L;
        when(repository.findByIdAndCliente(carrinhoId, cliente)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.buscarPorCarrinhoIdEClienteId(carrinhoId, cliente);
        });

        assertEquals("Carrinho não encontrado.", exception.getMessage());
        verify(repository).findByIdAndCliente(carrinhoId, cliente);
    }
}
