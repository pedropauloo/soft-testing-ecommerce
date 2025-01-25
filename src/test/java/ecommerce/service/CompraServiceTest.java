package ecommerce.service;

import ecommerce.dto.CompraDTO;
import ecommerce.dto.DisponibilidadeDTO;
import ecommerce.dto.EstoqueBaixaDTO;
import ecommerce.dto.PagamentoDTO;
import ecommerce.entity.*;
import ecommerce.external.IEstoqueExternal;
import ecommerce.external.IPagamentoExternal;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CompraServiceTest {

    @Mock
    private CarrinhoDeComprasService carrinhoService;

    @Mock
    private ClienteService clienteService;

    @Mock
    private IEstoqueExternal estoqueExternal;

    @Mock
    private IPagamentoExternal pagamentoExternal;

    @InjectMocks
    private CompraService compraService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFreteAbaixo5kg() {
        int peso = 4;
        Cliente cliente = new Cliente();
        cliente.setTipo(TipoCliente.BRONZE);

        CarrinhoDeCompras carrinho = new CarrinhoDeCompras();
        ItemCompra item = new ItemCompra();
        item.setProduto(new Produto(1L, "Produto A", "Descrição A", BigDecimal.valueOf(100), peso, TipoProduto.ELETRONICO));
        item.setQuantidade(1L);
        carrinho.setItens(List.of(item));

        BigDecimal pesoTotal = compraService.calcularPesoTotal(carrinho);
        BigDecimal frete = compraService.calcularFrete(pesoTotal, cliente);

        assertEquals(BigDecimal.ZERO, frete);
    }

    @Test
    void testFretePara5kg() {
        int peso = 5;
        Cliente cliente = new Cliente();
        cliente.setTipo(TipoCliente.BRONZE);


        CarrinhoDeCompras carrinho = new CarrinhoDeCompras();
        ItemCompra item = new ItemCompra();
        item.setProduto(new Produto(1L, "Produto B", "Descrição B", BigDecimal.valueOf(200), peso, TipoProduto.ELETRONICO));
        item.setQuantidade(1L);
        carrinho.setItens(List.of(item));

        BigDecimal pesoTotal = compraService.calcularPesoTotal(carrinho);
        BigDecimal frete = compraService.calcularFrete(pesoTotal, cliente);

        assertEquals(BigDecimal.ZERO, frete);
    }

    @Test
    void testFreteAcima5kg() {
        int peso = 6;

        Cliente cliente = new Cliente();
        cliente.setTipo(TipoCliente.BRONZE);

        CarrinhoDeCompras carrinho = new CarrinhoDeCompras();
        ItemCompra item = new ItemCompra();
        item.setProduto(new Produto(1L, "Produto C", "Descrição C", BigDecimal.valueOf(200), peso, TipoProduto.ELETRONICO));
        item.setQuantidade(1L);
        carrinho.setItens(List.of(item));

        BigDecimal pesoTotal = compraService.calcularPesoTotal(carrinho);
        BigDecimal frete = compraService.calcularFrete(pesoTotal, cliente);


        assertEquals(DescontoFrete.DOIS_POR_KG.getValor().multiply(BigDecimal.valueOf(peso)), frete);
    }

    @Test
    void testFreteAbaixo10kg() {
        int peso = 9;

        Cliente cliente = new Cliente();
        cliente.setTipo(TipoCliente.BRONZE);

        CarrinhoDeCompras carrinho = new CarrinhoDeCompras();
        ItemCompra item = new ItemCompra();
        item.setProduto(new Produto(1L, "Produto D", "Descrição D", BigDecimal.valueOf(200), peso, TipoProduto.ELETRONICO));
        item.setQuantidade(1L);
        carrinho.setItens(List.of(item));

        BigDecimal pesoTotal = compraService.calcularPesoTotal(carrinho);
        BigDecimal frete = compraService.calcularFrete(pesoTotal, cliente);

        assertEquals(DescontoFrete.DOIS_POR_KG.getValor().multiply(BigDecimal.valueOf(peso)), frete);
    }

    @Test
    void testFretePara10kg() {
        int peso = 10;

        Cliente cliente = new Cliente();
        cliente.setTipo(TipoCliente.BRONZE);

        CarrinhoDeCompras carrinho = new CarrinhoDeCompras();
        ItemCompra item = new ItemCompra();
        item.setProduto(new Produto(1L, "Produto E", "Descrição E", BigDecimal.valueOf(300), peso, TipoProduto.ELETRONICO));
        item.setQuantidade(1L);
        carrinho.setItens(List.of(item));

        BigDecimal pesoTotal = compraService.calcularPesoTotal(carrinho);
        BigDecimal frete = compraService.calcularFrete(pesoTotal, cliente);

        assertEquals(DescontoFrete.QUATRO_POR_KG.getValor().multiply(BigDecimal.valueOf(peso)), frete);
    }

    @Test
    void testeFreteAcima10kg() {
        int peso = 11;

        Cliente cliente = new Cliente();
        cliente.setTipo(TipoCliente.BRONZE);

        CarrinhoDeCompras carrinho = new CarrinhoDeCompras();
        ItemCompra item = new ItemCompra();
        item.setProduto(new Produto(1L, "Produto F", "Descrição F", BigDecimal.valueOf(300), peso, TipoProduto.ELETRONICO));
        item.setQuantidade(1L);
        carrinho.setItens(List.of(item));

        BigDecimal pesoTotal = compraService.calcularPesoTotal(carrinho);
        BigDecimal frete = compraService.calcularFrete(pesoTotal, cliente);

        assertEquals(DescontoFrete.QUATRO_POR_KG.getValor().multiply(BigDecimal.valueOf(peso)), frete);
    }

    @Test
    void testFreteAbaixo50kg() {
        int peso = 49;

        Cliente cliente = new Cliente();
        cliente.setTipo(TipoCliente.BRONZE);

        CarrinhoDeCompras carrinho = new CarrinhoDeCompras();
        ItemCompra item = new ItemCompra();
        item.setProduto(new Produto(1L, "Produto G", "Descrição G", BigDecimal.valueOf(300), peso, TipoProduto.ELETRONICO));
        item.setQuantidade(1L);
        carrinho.setItens(List.of(item));

        BigDecimal pesoTotal = compraService.calcularPesoTotal(carrinho);
        BigDecimal frete = compraService.calcularFrete(pesoTotal, cliente);

        assertEquals(DescontoFrete.QUATRO_POR_KG.getValor().multiply(BigDecimal.valueOf(peso)), frete);
    }

    @Test
    void testFretePara50kg() {
        int peso = 50;

        Cliente cliente = new Cliente();
        cliente.setTipo(TipoCliente.BRONZE);

        CarrinhoDeCompras carrinho = new CarrinhoDeCompras();
        ItemCompra item = new ItemCompra();
        item.setProduto(new Produto(1L, "Produto H", "Descrição H", BigDecimal.valueOf(300), peso, TipoProduto.ELETRONICO));
        item.setQuantidade(1L);
        carrinho.setItens(List.of(item));

        BigDecimal pesoTotal = compraService.calcularPesoTotal(carrinho);
        BigDecimal frete = compraService.calcularFrete(pesoTotal, cliente);

        assertEquals(DescontoFrete.SETE_POR_KG.getValor().multiply(BigDecimal.valueOf(peso)), frete);
    }

    @Test
    void testFreteAcima50kg() {
        int peso = 51;

        Cliente cliente = new Cliente();
        cliente.setTipo(TipoCliente.BRONZE);

        CarrinhoDeCompras carrinho = new CarrinhoDeCompras();
        ItemCompra item = new ItemCompra();
        item.setProduto(new Produto(1L, "Produto I", "Descrição I", BigDecimal.valueOf(400), peso, TipoProduto.ELETRONICO));
        item.setQuantidade(1L);
        carrinho.setItens(List.of(item));

        BigDecimal pesoTotal = compraService.calcularPesoTotal(carrinho);
        BigDecimal frete = compraService.calcularFrete(pesoTotal, cliente);

        assertEquals(DescontoFrete.SETE_POR_KG.getValor().multiply(BigDecimal.valueOf(peso)), frete);
    }

    @Test
    void testFreteClientePrata() {
        int peso = 51;

        Cliente cliente = new Cliente();
        cliente.setTipo(TipoCliente.PRATA);

        CarrinhoDeCompras carrinho = new CarrinhoDeCompras();
        ItemCompra item = new ItemCompra();
        item.setProduto(new Produto(1L, "Produto J", "Descrição J", BigDecimal.valueOf(400), peso, TipoProduto.ELETRONICO));
        item.setQuantidade(1L);
        carrinho.setItens(List.of(item));

        BigDecimal pesoTotal = compraService.calcularPesoTotal(carrinho);
        BigDecimal frete = compraService.calcularFrete(pesoTotal, cliente);

        BigDecimal valorFrete = DescontoFrete.SETE_POR_KG.getValor().multiply(BigDecimal.valueOf(peso));
        BigDecimal freteCom50PorcentoDesconto = valorFrete.multiply(DescontoFrete.DESCONTO_50.getValor());

        assertEquals(freteCom50PorcentoDesconto, frete);
    }

    @Test
    void testFreteClienteOuro() {
        int peso = 51;

        Cliente cliente = new Cliente();
        cliente.setTipo(TipoCliente.OURO);

        CarrinhoDeCompras carrinho = new CarrinhoDeCompras();
        ItemCompra item = new ItemCompra();
        item.setProduto(new Produto(1L, "Produto K", "Descrição K", BigDecimal.valueOf(400), peso, TipoProduto.ELETRONICO));
        item.setQuantidade(1L);
        carrinho.setItens(List.of(item));

        BigDecimal pesoTotal = compraService.calcularPesoTotal(carrinho);
        BigDecimal frete = compraService.calcularFrete(pesoTotal, cliente);

        assertEquals(BigDecimal.ZERO, frete);
    }

    @Test
    void testFreteClienteSemTipo() {
        int peso = 10;

        Cliente cliente = new Cliente();
        cliente.setTipo(null);

        CarrinhoDeCompras carrinho = new CarrinhoDeCompras();
        ItemCompra item = new ItemCompra();
        item.setProduto(new Produto(1L, "Produto L", "Descrição L", BigDecimal.valueOf(200), peso, TipoProduto.ELETRONICO));
        item.setQuantidade(1L);
        carrinho.setItens(List.of(item));

        BigDecimal pesoTotal = compraService.calcularPesoTotal(carrinho);
        BigDecimal frete = compraService.calcularFrete(pesoTotal, cliente);

        assertEquals(DescontoFrete.QUATRO_POR_KG.getValor().multiply(BigDecimal.valueOf(peso)), frete);
    }

    @Test
    void testSemDescontoAbaixo500() {
        BigDecimal precoProduto = BigDecimal.valueOf(499);
        CarrinhoDeCompras carrinho = new CarrinhoDeCompras();
        ItemCompra item = new ItemCompra();
        item.setProduto(new Produto(1L, "Produto M", "Descrição M", precoProduto, 5, TipoProduto.ELETRONICO));
        item.setQuantidade(1L);
        carrinho.setItens(List.of(item));

        BigDecimal custoProdutos = compraService.calcularCustoProdutos(carrinho);
        BigDecimal custoComDesconto = compraService.aplicarDescontoCusto(custoProdutos);

        assertEquals(BigDecimal.valueOf(499), custoComDesconto);
    }

    @Test
    void testSemDescontoPara500() {
        BigDecimal precoProduto = BigDecimal.valueOf(500);
        CarrinhoDeCompras carrinho = new CarrinhoDeCompras();
        ItemCompra item = new ItemCompra();
        item.setProduto(new Produto(1L, "Produto N", "Descrição N", precoProduto, 5, TipoProduto.ELETRONICO));
        item.setQuantidade(1L);
        carrinho.setItens(List.of(item));

        BigDecimal custoProdutos = compraService.calcularCustoProdutos(carrinho);
        BigDecimal custoComDesconto = compraService.aplicarDescontoCusto(custoProdutos);

        assertEquals(BigDecimal.valueOf(500), custoComDesconto);
    }

    @Test
    void testDesconto10PorcentoAcima500() {
        BigDecimal precoProduto = BigDecimal.valueOf(501);
        CarrinhoDeCompras carrinho = new CarrinhoDeCompras();
        ItemCompra item = new ItemCompra();
        item.setProduto(new Produto(1L, "Produto O", "Descrição O", precoProduto, 10, TipoProduto.ELETRONICO));
        item.setQuantidade(1L);
        carrinho.setItens(List.of(item));

        BigDecimal custoProdutos = compraService.calcularCustoProdutos(carrinho);

        BigDecimal custoComDesconto = compraService.aplicarDescontoCusto(custoProdutos);

        assertEquals(BigDecimal.valueOf(501 * 0.9).setScale(1, RoundingMode.HALF_UP), custoComDesconto);
    }

    @Test
    void testDesconto10PorcentoAbaixo1000() {
        BigDecimal precoProduto = BigDecimal.valueOf(999);
        CarrinhoDeCompras carrinho = new CarrinhoDeCompras();
        ItemCompra item = new ItemCompra();
        item.setProduto(new Produto(1L, "Produto P", "Descrição P", precoProduto, 10, TipoProduto.ELETRONICO));
        item.setQuantidade(1L);
        carrinho.setItens(List.of(item));

        BigDecimal custoProdutos = compraService.calcularCustoProdutos(carrinho);

        BigDecimal custoComDesconto = compraService.aplicarDescontoCusto(custoProdutos);

        assertEquals(BigDecimal.valueOf(999 * 0.9), custoComDesconto);
    }

    @Test
    void testDesconto10PorcentoPara1000() {
        BigDecimal precoProduto = BigDecimal.valueOf(1000);
        CarrinhoDeCompras carrinho = new CarrinhoDeCompras();
        ItemCompra item = new ItemCompra();
        item.setProduto(new Produto(1L, "Produto Q", "Descrição Q", precoProduto, 10, TipoProduto.ELETRONICO));
        item.setQuantidade(1L);
        carrinho.setItens(List.of(item));

        BigDecimal custoProdutos = compraService.calcularCustoProdutos(carrinho);

        BigDecimal custoComDesconto = compraService.aplicarDescontoCusto(custoProdutos);

        assertEquals(BigDecimal.valueOf(1000 * 0.9), custoComDesconto);
    }

    @Test
    void testDesconto20PorcentoAcima1000() {
        BigDecimal precoProduto = BigDecimal.valueOf(1001);
        CarrinhoDeCompras carrinho = new CarrinhoDeCompras();
        ItemCompra item = new ItemCompra();
        item.setProduto(new Produto(1L, "Produto R", "Descrição R", precoProduto, 10, TipoProduto.ELETRONICO));
        item.setQuantidade(1L);
        carrinho.setItens(List.of(item));

        BigDecimal custoProdutos = compraService.calcularCustoProdutos(carrinho);

        BigDecimal custoComDesconto = compraService.aplicarDescontoCusto(custoProdutos);

        assertEquals(BigDecimal.valueOf(1001 * 0.8).setScale(1, RoundingMode.HALF_UP), custoComDesconto);
    }

    @Test
    void testCalcularCustoTotal() {
        BigDecimal precoProduto = BigDecimal.valueOf(100);
        int peso = 1;

        Cliente cliente = new Cliente();
        cliente.setTipo(TipoCliente.BRONZE);

        CarrinhoDeCompras carrinho = new CarrinhoDeCompras();
        ItemCompra item = new ItemCompra();
        item.setProduto(new Produto(1L, "Produto S", "Descrição S", precoProduto, peso, TipoProduto.ELETRONICO));
        item.setQuantidade(1L);
        carrinho.setItens(List.of(item));

        BigDecimal custoTotal = compraService.calcularCustoTotal(carrinho, cliente);

        assertEquals(precoProduto, custoTotal);
    }

    @Test
    void testFinalizarCompraClienteInexistente() {
        Long carrinhoId = 1L;
        Long clienteId = 999L;
        when(clienteService.buscarPorId(clienteId)).thenThrow(new EntityNotFoundException("Cliente não encontrado"));

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            compraService.finalizarCompra(carrinhoId, clienteId);
        });

        assertEquals("Cliente não encontrado", exception.getMessage());
    }

    @Test
    void testFinalizarCompraCarrinhoVazio() {
        Long carrinhoId = 1L;
        Long clienteId = 1L;

        Cliente cliente = new Cliente();
        cliente.setId(clienteId);
        cliente.setTipo(TipoCliente.BRONZE);

        CarrinhoDeCompras carrinho = new CarrinhoDeCompras();
        carrinho.setItens(List.of());
        when(clienteService.buscarPorId(clienteId)).thenReturn(cliente);
        when(carrinhoService.buscarPorCarrinhoIdEClienteId(carrinhoId, cliente)).thenReturn(carrinho);

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            compraService.finalizarCompra(carrinhoId, clienteId);
        });

        assertEquals("Carrinho vazio.", exception.getMessage());
    }

    @Test
    void testFinalizaCompraCarrinhoItensNulo() {
        Long carrinhoId = 1L;
        Long clienteId = 1L;

        Cliente cliente = new Cliente();
        cliente.setId(clienteId);
        cliente.setTipo(TipoCliente.BRONZE);

        CarrinhoDeCompras carrinho = new CarrinhoDeCompras();
        carrinho.setItens(null);

        when(clienteService.buscarPorId(clienteId)).thenReturn(cliente);
        when(carrinhoService.buscarPorCarrinhoIdEClienteId(carrinhoId, cliente)).thenReturn(carrinho);

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            compraService.finalizarCompra(carrinhoId, clienteId);
        });

        assertEquals("Carrinho vazio.", exception.getMessage());
    }

    @Test
    void testFinalizarCompraVerificaDisponibilidade() {
        Long carrinhoId = 1L;
        Long clienteId = 1L;

        Cliente cliente = new Cliente();
        cliente.setId(clienteId);
        cliente.setTipo(TipoCliente.BRONZE);

        CarrinhoDeCompras carrinho = new CarrinhoDeCompras();
        ItemCompra item = new ItemCompra();
        item.setProduto(new Produto(1L, "Produto I", "Descrição I", BigDecimal.valueOf(400), 1, TipoProduto.ELETRONICO));
        item.setQuantidade(1L);
        carrinho.setItens(List.of(item));

        when(clienteService.buscarPorId(clienteId)).thenReturn(cliente);
        when(carrinhoService.buscarPorCarrinhoIdEClienteId(carrinhoId, cliente)).thenReturn(carrinho);

        when(estoqueExternal.verificarDisponibilidade(any(), any())).thenReturn(new DisponibilidadeDTO(true, new ArrayList<>()));

        when(pagamentoExternal.autorizarPagamento(clienteId, BigDecimal.valueOf(400).doubleValue()))
                .thenReturn(new PagamentoDTO(true, 1L));

        when(estoqueExternal.darBaixa(any(), any())).thenReturn(new EstoqueBaixaDTO(true));

        CompraDTO resultado = compraService.finalizarCompra(carrinhoId, clienteId);

        assertTrue(resultado.sucesso());
        assertEquals(1, resultado.transacaoPagamentoId());
    }

    @Test
    void testFinalizarCompraEstoqueIndisponivel() {
        Long carrinhoId = 1L;
        Long clienteId = 1L;

        Cliente cliente = new Cliente();
        cliente.setId(clienteId);
        cliente.setTipo(TipoCliente.BRONZE);

        CarrinhoDeCompras carrinho = new CarrinhoDeCompras();
        ItemCompra item = new ItemCompra();
        item.setProduto(new Produto(1L, "Produto I", "Descrição I", BigDecimal.valueOf(400), 1, TipoProduto.ELETRONICO));
        item.setQuantidade(1L);
        carrinho.setItens(List.of(item));

        when(clienteService.buscarPorId(clienteId)).thenReturn(cliente);
        when(carrinhoService.buscarPorCarrinhoIdEClienteId(carrinhoId, cliente)).thenReturn(carrinho);

        when(estoqueExternal.verificarDisponibilidade(any(), any())).thenReturn(new DisponibilidadeDTO(false, new ArrayList<>()));

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            compraService.finalizarCompra(carrinhoId, clienteId);
        });

        assertEquals("Itens fora de estoque.", exception.getMessage());
    }

    @Test
    void testFinalizarCompraPagamentoNaoAutorizado() {
        Long carrinhoId = 1L;
        Long clienteId = 1L;

        Cliente cliente = new Cliente();
        cliente.setId(clienteId);
        cliente.setTipo(TipoCliente.BRONZE);

        CarrinhoDeCompras carrinho = new CarrinhoDeCompras();
        ItemCompra item = new ItemCompra();
        item.setProduto(new Produto(1L, "Produto I", "Descrição I", BigDecimal.valueOf(400), 1, TipoProduto.ELETRONICO));
        item.setQuantidade(1L);
        carrinho.setItens(List.of(item));

        when(clienteService.buscarPorId(clienteId)).thenReturn(cliente);
        when(carrinhoService.buscarPorCarrinhoIdEClienteId(carrinhoId, cliente)).thenReturn(carrinho);

        when(estoqueExternal.verificarDisponibilidade(any(), any())).thenReturn(new DisponibilidadeDTO(true, new ArrayList<>()));

        when(pagamentoExternal.autorizarPagamento(clienteId, BigDecimal.valueOf(400).doubleValue()))
                .thenReturn(new PagamentoDTO(false, 1L));

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            compraService.finalizarCompra(carrinhoId, clienteId);
        });

        assertEquals("Pagamento não autorizado.", exception.getMessage());
    }

    @Test
    void testFinalizarCompraFalhaBaixaEstoqueCancelaPagamento() {
        Long carrinhoId = 1L;
        Long clienteId = 1L;

        Cliente cliente = new Cliente();
        cliente.setId(clienteId);
        cliente.setTipo(TipoCliente.BRONZE);

        CarrinhoDeCompras carrinho = new CarrinhoDeCompras();
        ItemCompra item = new ItemCompra();
        item.setProduto(new Produto(1L, "Produto I", "Descrição I", BigDecimal.valueOf(400), 1, TipoProduto.ELETRONICO));
        item.setQuantidade(1L);
        carrinho.setItens(List.of(item));

        when(clienteService.buscarPorId(clienteId)).thenReturn(cliente);
        when(carrinhoService.buscarPorCarrinhoIdEClienteId(carrinhoId, cliente)).thenReturn(carrinho);

        when(estoqueExternal.verificarDisponibilidade(any(), any())).thenReturn(new DisponibilidadeDTO(true, new ArrayList<>()));

        when(pagamentoExternal.autorizarPagamento(clienteId, BigDecimal.valueOf(400).doubleValue()))
                .thenReturn(new PagamentoDTO(true, 1L));

        when(estoqueExternal.darBaixa(any(), any())).thenReturn(new EstoqueBaixaDTO(false));

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            compraService.finalizarCompra(carrinhoId, clienteId);
        });

        assertEquals("Erro ao dar baixa no estoque.", exception.getMessage());

        verify(pagamentoExternal).cancelarPagamento(clienteId, 1L);
    }

    @Test
    void testFinalizarCompraCarrinhoNulo() {
        Long carrinhoId = 1L;
        Long clienteId = 1L;

        Cliente cliente = new Cliente();
        cliente.setId(clienteId);

        when(clienteService.buscarPorId(clienteId)).thenReturn(cliente);
        when(carrinhoService.buscarPorCarrinhoIdEClienteId(carrinhoId, cliente)).thenReturn(null);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            compraService.finalizarCompra(carrinhoId, clienteId);
        });

        assertEquals("Carrinho não encontrado.", exception.getMessage());
    }

    @Test
    void testCalcularPesoTotal() {
        // Arrange
        CarrinhoDeCompras carrinho = new CarrinhoDeCompras();
        Produto produto1 = new Produto();
        produto1.setPeso(10); // Defina o peso
        ItemCompra item1 = new ItemCompra();
        item1.setProduto(produto1);
        item1.setQuantidade(1L);
    
        Produto produto2 = new Produto();
        produto2.setPeso(5); // Defina o peso
        ItemCompra item2 = new ItemCompra();
        item2.setProduto(produto2);
        item2.setQuantidade(2L);
    
        carrinho.setItens(List.of(item1, item2)); // Adicione os itens ao carrinho
    
        // Act
        BigDecimal pesoTotal = compraService.calcularPesoTotal(carrinho);
    
        // Assert
        assertEquals(BigDecimal.valueOf(20), pesoTotal); // 10 + 5*2 = 20
    }

    @Test
    void testCalcularCustoTotal_ValoresNormais() {
        // Arrange
        CarrinhoDeCompras carrinho = new CarrinhoDeCompras();
        Cliente cliente = new Cliente();
        

        // Defina os itens do carrinho e outros necessários
        // Exemplo:
        Produto produto = new Produto();
        produto.setPeso(2);
        produto.setPreco(BigDecimal.valueOf(50)); // Definindo preço do produto
        ItemCompra item = new ItemCompra();
        item.setProduto(produto);
        item.setQuantidade(2L);
        carrinho.setItens(List.of(item)); // Adicionando item ao carrinho

        // Act
        BigDecimal resultado = compraService.calcularCustoTotal(carrinho, cliente);

        // Assert
        assertEquals(BigDecimal.valueOf(100), resultado); // Exemplo de resultado esperado
    }

    @Test
    void testCalcularPesoTotal_PesoNegativo() {
        // Arrange
        CarrinhoDeCompras carrinho = new CarrinhoDeCompras();
        Produto produto = new Produto();
        produto.setPeso(-1); // Peso negativo
        ItemCompra item = new ItemCompra();
        item.setProduto(produto);
        item.setQuantidade(1L);
        carrinho.setItens(List.of(item));
    
        // Act & Assert
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            compraService.calcularPesoTotal(carrinho);
        });
    
        assertEquals("Peso do produto não pode ser negativo.", exception.getMessage());
    }

    

    @Test
    void testCalcularPesoTotal_PesoNulo() {
        // Arrange
        CarrinhoDeCompras carrinho = new CarrinhoDeCompras();
        Produto produto = new Produto();
        produto.setPeso(null); // Peso nulo
        ItemCompra item = new ItemCompra();
        item.setProduto(produto);
        item.setQuantidade(1L);
        carrinho.setItens(List.of(item));

        // Act & Assert
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            compraService.calcularPesoTotal(carrinho);
        });

        assertEquals("Peso do produto não pode ser nulo.", exception.getMessage());
    }

    
    @Test
    void testCalcularPesoTotal_ProdutoNulo() {
        // Arrange
        CarrinhoDeCompras carrinho = new CarrinhoDeCompras();
        ItemCompra item = new ItemCompra();
        item.setProduto(null); // Produto nulo
        item.setQuantidade(1L);
        carrinho.setItens(List.of(item));

        // Act & Assert
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            compraService.calcularPesoTotal(carrinho);
        });

        assertEquals("Produto não pode ser nulo.", exception.getMessage());
    }
    @Test
    void testCalcularPesoTotal_PesoValido() {
        // Arrange
        CarrinhoDeCompras carrinho = new CarrinhoDeCompras();
        Produto produto = new Produto();
        produto.setPeso(5); // Peso válido
        ItemCompra item = new ItemCompra();
        item.setProduto(produto);
        item.setQuantidade(2L); // 2 itens
        carrinho.setItens(List.of(item));

        // Act
        BigDecimal pesoTotal = compraService.calcularPesoTotal(carrinho);

        // Assert
        assertEquals(BigDecimal.valueOf(10), pesoTotal); // 5 * 2
    }

    private void verifiquePesoProduto(int peso) {
        // Arrange
        CarrinhoDeCompras carrinho = new CarrinhoDeCompras();
        Produto produto = new Produto();
        produto.setPeso(peso); // Peso variável baseado no teste
        ItemCompra item = new ItemCompra();
        item.setProduto(produto);
        item.setQuantidade(1L); // Apenas 1 item
        carrinho.setItens(List.of(item));
    
        if (peso < 0) {
            // Act & Assert para peso negativo
            Exception exception = assertThrows(IllegalStateException.class, () -> {
                compraService.calcularPesoTotal(carrinho);
            });
            assertEquals("Peso do produto não pode ser negativo.", exception.getMessage());
        } else {
            // Act para peso zero ou positivo
            BigDecimal pesoTotal = compraService.calcularPesoTotal(carrinho);
            assertEquals(BigDecimal.valueOf(peso), pesoTotal);
        }
    }
    @Test
    void testCalcularPesoTotal_PesoLimite() {
        // Teste para peso negativo
        verifiquePesoProduto(-1);

        // Teste para peso zero
        verifiquePesoProduto(0);

        // Teste para peso positivo
        verifiquePesoProduto(5);
    }
    @Test
    void testFinalizarCompra_CarrinhoNulo() {
        // Arrange
        Long carrinhoId = 1L;
        Long clienteId = 1L;

        when(clienteService.buscarPorId(clienteId)).thenReturn(new Cliente());
        when(carrinhoService.buscarPorCarrinhoIdEClienteId(eq(carrinhoId), any())).thenReturn(null);
        // Act & Assert
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            compraService.finalizarCompra(carrinhoId, clienteId);
        });

        assertEquals("Carrinho não encontrado.", exception.getMessage());
    }

    @Test
    void testFinalizarCompra_CarrinhoVazio() {
        // Arrange
        Long carrinhoId = 1L;
        Long clienteId = 1L;
        Cliente cliente = new Cliente();
        CarrinhoDeCompras carrinho = new CarrinhoDeCompras();
        carrinho.setItens(new ArrayList<>()); // Carrinho vazio

        when(clienteService.buscarPorId(clienteId)).thenReturn(cliente);
        when(carrinhoService.buscarPorCarrinhoIdEClienteId(carrinhoId, cliente)).thenReturn(carrinho);

        // Act & Assert
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            compraService.finalizarCompra(carrinhoId, clienteId);
        });

        assertEquals("Carrinho vazio.", exception.getMessage());
    }

    @Test
    void testFinalizarCompra_ItemForaDeEstoque() {
        // Arrange
        Long carrinhoId = 1L;
        Long clienteId = 1L;
        Cliente cliente = new Cliente();
        CarrinhoDeCompras carrinho = new CarrinhoDeCompras();
        
        // Adicionando um item ao carrinho
        Produto produto = new Produto();
        produto.setId(1L);
        ItemCompra item = new ItemCompra();
        item.setProduto(produto);
        item.setQuantidade(1L);
        carrinho.setItens(List.of(item));

        when(clienteService.buscarPorId(clienteId)).thenReturn(cliente);
        when(carrinhoService.buscarPorCarrinhoIdEClienteId(carrinhoId, cliente)).thenReturn(carrinho);
        when(estoqueExternal.verificarDisponibilidade(any(), any())).thenReturn(new DisponibilidadeDTO(false, new ArrayList<>()));

        // Act & Assert
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            compraService.finalizarCompra(carrinhoId, clienteId);
        });

        assertEquals("Itens fora de estoque.", exception.getMessage());
    }

}

