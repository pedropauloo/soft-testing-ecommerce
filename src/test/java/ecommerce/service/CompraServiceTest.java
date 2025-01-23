package ecommerce.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;

import ecommerce.entity.*;
import ecommerce.external.IEstoqueExternal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import ecommerce.dto.DisponibilidadeDTO;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CompraServiceTest {

    @Mock
    private CarrinhoDeComprasService carrinhoService;

    @Mock
    private ClienteService clienteService;

    @Mock
    private IEstoqueExternal estoqueExternal;

    @Mock
    private IEstoqueExternal pagamentoExternal;

    @InjectMocks
    private CompraService compraService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCalcularCustoTotalComDescontoDe20PorCento() {

        Cliente cliente = new Cliente();
        cliente.setTipo(TipoCliente.BRONZE);

        ItemCompra item1 = new ItemCompra();
        item1.setProduto(new Produto(1L, "Produto A", "Descrição A", BigDecimal.valueOf(100), 3, TipoProduto.ELETRONICO));
        item1.setQuantidade(2L);


        CarrinhoDeCompras carrinho = new CarrinhoDeCompras();
        carrinho.setItens(List.of(item1));

        BigDecimal total = compraService.calcularCustoTotal(carrinho, cliente);

        assertEquals(BigDecimal.valueOf(200), total);
    }

    @Test
    void testCalcularFreteGratisParaClienteOuro() {
        Cliente cliente = new Cliente();
        cliente.setTipo(TipoCliente.OURO);

        CarrinhoDeCompras carrinho = new CarrinhoDeCompras();
        ItemCompra item = new ItemCompra();
        item.setProduto(new Produto(1L, "Produto A", "Descrição A", BigDecimal.valueOf(100), 3, TipoProduto.ELETRONICO));
        item.setQuantidade(1L);
        carrinho.setItens(List.of(item));

        BigDecimal frete = compraService.calcularFrete(BigDecimal.valueOf(6), cliente);

        assertEquals(BigDecimal.ZERO, frete);
    }

}
