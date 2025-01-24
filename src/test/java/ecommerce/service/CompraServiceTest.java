package ecommerce.service;

import ecommerce.entity.*;
import ecommerce.external.IEstoqueExternal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    void testFreteAbaixo5kg() {
        Integer peso = 4;
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
        Integer peso = 5;
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
        Integer peso = 6;

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
        Integer peso = 9;

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
        Integer peso = 10;

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
        Integer peso = 11;

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
        Integer peso = 49;

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
        Integer peso = 50;

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
        Integer peso = 51;

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


}
