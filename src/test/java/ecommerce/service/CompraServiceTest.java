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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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
    void testDesconto10PorcentoAcima500() {
        BigDecimal precoProduto = BigDecimal.valueOf(600);
        CarrinhoDeCompras carrinho = new CarrinhoDeCompras();
        ItemCompra item = new ItemCompra();
        item.setProduto(new Produto(1L, "Produto L", "Descrição L", precoProduto, 10, TipoProduto.ELETRONICO));
        item.setQuantidade(1L);
        carrinho.setItens(List.of(item));

        BigDecimal custoProdutos = compraService.calcularCustoProdutos(carrinho);

        BigDecimal desconto = compraService.aplicarDescontoCusto(custoProdutos);

        assertEquals(DescontoCusto.DESCONTO_10.aplicar(precoProduto), desconto); // 10% de R$ 600
    }

    @Test
    void testDesconto20PorcentoAcima1000() {
        BigDecimal precoProduto = BigDecimal.valueOf(1200);
        CarrinhoDeCompras carrinho = new CarrinhoDeCompras();
        ItemCompra item = new ItemCompra();
        item.setProduto(new Produto(1L, "Produto M", "Descrição M", precoProduto, 10, TipoProduto.ELETRONICO));
        item.setQuantidade(1L);
        carrinho.setItens(List.of(item));

        BigDecimal custoProdutos = compraService.calcularCustoProdutos(carrinho);

        BigDecimal desconto = compraService.aplicarDescontoCusto(custoProdutos);

        assertEquals(DescontoCusto.DESCONTO_20.aplicar(precoProduto), desconto); // 20% de R$ 1200
    }

    @Test
    void testCalcularCustoTotal() {
        BigDecimal precoProduto = BigDecimal.valueOf(100);
        int peso = 1;

        Cliente cliente = new Cliente();
        cliente.setTipo(TipoCliente.BRONZE);

        CarrinhoDeCompras carrinho = new CarrinhoDeCompras();
        ItemCompra item = new ItemCompra();
        item.setProduto(new Produto(1L, "Produto N", "Descrição N", precoProduto, peso, TipoProduto.ELETRONICO));
        item.setQuantidade(1L);
        carrinho.setItens(List.of(item));

        BigDecimal custoTotal = compraService.calcularCustoTotal(carrinho, cliente);

        assertEquals(precoProduto, custoTotal);
    }

    @Test
    void testFinalizarCompra_ClienteInexistente() {
        Long carrinhoId = 1L;
        Long clienteId = 999L; // ID de cliente que não existe

        when(clienteService.buscarPorId(clienteId)).thenThrow(new EntityNotFoundException("Cliente não encontrado"));

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            compraService.finalizarCompra(carrinhoId, clienteId);
        });

        assertEquals("Cliente não encontrado", exception.getMessage());
    }
    
    @Test
    void testFinalizarCompra_CarrinhoVazio() {
        Long carrinhoId = 1L;
        Long clienteId = 1L;

        Cliente cliente = new Cliente();
        cliente.setId(clienteId);
        cliente.setTipo(TipoCliente.BRONZE);

        CarrinhoDeCompras carrinho = new CarrinhoDeCompras();
        carrinho.setItens(List.of()); // Carrinho vazio

        when(clienteService.buscarPorId(clienteId)).thenReturn(cliente);
        when(carrinhoService.buscarPorCarrinhoIdEClienteId(carrinhoId, cliente)).thenReturn(carrinho);

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            compraService.finalizarCompra(carrinhoId, clienteId);
        });

        assertEquals("Carrinho vazio.", exception.getMessage()); // Assume que você lança essa exceção
    }

    @Test
    void testFinalizarCompra_VerificaDisponibilidade() {
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
        
        // Mock para verificar disponibilidade
        when(estoqueExternal.verificarDisponibilidade(any(), any())).thenReturn(new DisponibilidadeDTO(true, new ArrayList<>()));
        
        // Mock para autorizar pagamento
        when(pagamentoExternal.autorizarPagamento(clienteId, BigDecimal.valueOf(400).doubleValue()))
    .thenReturn(new PagamentoDTO(true, 1L)); 
        
        // Mock para dar baixa no estoque
        when(estoqueExternal.darBaixa(any(), any())).thenReturn(new EstoqueBaixaDTO(true));

        CompraDTO resultado = compraService.finalizarCompra(carrinhoId, clienteId);

        assertTrue(resultado.sucesso());
        assertEquals(1, resultado.transacaoPagamentoId());
    }
}
