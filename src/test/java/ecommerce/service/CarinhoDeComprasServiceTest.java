package ecommerce.service;

import ecommerce.entity.*;
import ecommerce.repository.CarrinhoDeComprasRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Test
    void testeIdsDosProdutosNoCarrinho() {
        ItemCompra item1 = new ItemCompra();
        item1.setProduto(new Produto(1L, "Produto A", "Descrição A", BigDecimal.valueOf(100), 5, TipoProduto.ELETRONICO));
        item1.setQuantidade(1L);

        ItemCompra item2 = new ItemCompra();
        item2.setProduto(new Produto(2L, "Produto B", "Descrição B", BigDecimal.valueOf(200), 3, TipoProduto.ELETRONICO));
        item2.setQuantidade(2L);

        CarrinhoDeCompras carrinho = new CarrinhoDeCompras();
        carrinho.setItens(List.of(item1, item2));

        List<Long> produtosIds = carrinho.getItens().stream()
                .map(i -> i.getProduto().getId())
                .collect(Collectors.toList());

        assertEquals(List.of(1L, 2L), produtosIds);
    }
}
