package ecommerce.service;

import ecommerce.dto.CompraDTO;
import ecommerce.dto.DisponibilidadeDTO;
import ecommerce.dto.EstoqueBaixaDTO;
import ecommerce.dto.PagamentoDTO;
import ecommerce.entity.*;
import ecommerce.external.IEstoqueExternal;
import ecommerce.external.IPagamentoExternal;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompraService {

    private final CarrinhoDeComprasService carrinhoService;
    private final ClienteService clienteService;

    private final IEstoqueExternal estoqueExternal;
    private final IPagamentoExternal pagamentoExternal;


    @Autowired
    public CompraService(CarrinhoDeComprasService carrinhoService, ClienteService clienteService,
                         IEstoqueExternal estoqueExternal, IPagamentoExternal pagamentoExternal) {
        this.carrinhoService = carrinhoService;
        this.clienteService = clienteService;

        this.estoqueExternal = estoqueExternal;
        this.pagamentoExternal = pagamentoExternal;
    }

    @Transactional
    public CompraDTO finalizarCompra(Long carrinhoId, Long clienteId) {
        Cliente cliente = clienteService.buscarPorId(clienteId);
        CarrinhoDeCompras carrinho = carrinhoService.buscarPorCarrinhoIdEClienteId(carrinhoId, cliente);

        validarCarrinho(carrinho);

        List<Long> produtosIds = carrinho.getItens().stream().map(i -> i.getProduto().getId())
                .collect(Collectors.toList());
        List<Long> produtosQtds = carrinho.getItens().stream().map(i -> i.getQuantidade()).collect(Collectors.toList());

        DisponibilidadeDTO disponibilidade = estoqueExternal.verificarDisponibilidade(produtosIds, produtosQtds);

        if (!disponibilidade.disponivel()) {
            throw new IllegalStateException("Itens fora de estoque.");
        }

        BigDecimal custoTotal = calcularCustoTotal(carrinho, cliente);

        PagamentoDTO pagamento = pagamentoExternal.autorizarPagamento(cliente.getId(), custoTotal.doubleValue());

        if (!pagamento.autorizado()) {
            throw new IllegalStateException("Pagamento não autorizado.");
        }

        EstoqueBaixaDTO baixaDTO = estoqueExternal.darBaixa(produtosIds, produtosQtds);

        if (!baixaDTO.sucesso()) {
            pagamentoExternal.cancelarPagamento(cliente.getId(), pagamento.transacaoId());
            throw new IllegalStateException("Erro ao dar baixa no estoque.");
        }

        CompraDTO compraDTO = new CompraDTO(true, pagamento.transacaoId(), "Compra finalizada com sucesso.");

        return compraDTO;
    }

    private void validarCarrinho(CarrinhoDeCompras carrinho) {
        if (carrinho == null) {
            throw new IllegalStateException("Carrinho não encontrado.");
        }
        if (carrinho.getItens() == null || carrinho.getItens().isEmpty()) {
            throw new IllegalStateException("Carrinho vazio.");
        }
    }
    
    public BigDecimal calcularCustoTotal(CarrinhoDeCompras carrinho, Cliente cliente) {
        BigDecimal custoProdutos = calcularCustoProdutos(carrinho);

        BigDecimal custoProdutosComDesconto = aplicarDescontoCusto(custoProdutos);

        BigDecimal pesoTotal = calcularPesoTotal(carrinho);

        BigDecimal frete = calcularFrete(pesoTotal, cliente);

        return custoProdutosComDesconto.add(frete);
    }

    public BigDecimal calcularCustoProdutos(CarrinhoDeCompras carrinho) {
        BigDecimal custoProdutos = BigDecimal.ZERO;

        for (ItemCompra item : carrinho.getItens()) {
            BigDecimal precoUnitario = item.getProduto().getPreco();
            Long quantidade = item.getQuantidade();
            custoProdutos = custoProdutos.add(precoUnitario.multiply(BigDecimal.valueOf(quantidade)));

        }

        return custoProdutos;
    }

    public BigDecimal aplicarDescontoCusto(BigDecimal total) {
        boolean custoMaiorQue1000 = total.compareTo(BigDecimal.valueOf(1000)) > 0;
        boolean custoMaiorQue500 = total.compareTo(BigDecimal.valueOf(500)) > 0;

        if (custoMaiorQue1000) {
            return DescontoCusto.DESCONTO_20.aplicar(total); // 20% de desconto
        } else if (custoMaiorQue500) {
            return DescontoCusto.DESCONTO_10.aplicar(total); // 10% de desconto
        }

        return total;
    }

    private BigDecimal aplicarDescontoFrete(BigDecimal frete, Cliente cliente) {
        if (cliente.getTipo() == TipoCliente.PRATA) {
            return frete.multiply(DescontoFrete.DESCONTO_50.getValor()); // 50% de desconto
        }

        return frete;
    }

    public BigDecimal calcularPesoTotal(CarrinhoDeCompras carrinho) {
        BigDecimal pesoTotal = BigDecimal.ZERO;
    
        for (ItemCompra item : carrinho.getItens()) {
            Produto produto = item.getProduto();
            if (produto != null) {
                Integer pesoProduto = produto.getPeso(); // Supondo que getPeso() retorna um Integer
                if (pesoProduto != null) {
                    if (pesoProduto < 0) {
                        throw new IllegalStateException("Peso do produto não pode ser negativo.");
                    }
                    pesoTotal = pesoTotal.add(BigDecimal.valueOf(pesoProduto).multiply(BigDecimal.valueOf(item.getQuantidade())));
                } else {
                    throw new IllegalStateException("Peso do produto não pode ser nulo.");
                }
            } else {
                throw new IllegalStateException("Produto não pode ser nulo.");
            }
        }
    
        return pesoTotal;
    }

    public BigDecimal calcularFrete(BigDecimal pesoTotal, Cliente cliente) {
        boolean pesoMenorIgual5 = pesoTotal.compareTo(BigDecimal.valueOf(5)) <= 0;

        if (cliente.getTipo() == TipoCliente.OURO || pesoMenorIgual5) {
            return DescontoFrete.FRETE_GRATIS.getValor();
        }

        BigDecimal frete;

        boolean pesoMenorQue10 = pesoTotal.compareTo(BigDecimal.valueOf(10)) < 0;
        boolean pesoMenorQue50 = pesoTotal.compareTo(BigDecimal.valueOf(50)) < 0;

        if (pesoMenorQue10) {
            frete = pesoTotal.multiply(DescontoFrete.DOIS_POR_KG.getValor()); // R$ 2,00 por kg
        } else if (pesoMenorQue50) {
            frete = pesoTotal.multiply(DescontoFrete.QUATRO_POR_KG.getValor()); // R$ 4,00 por kg
        } else {
            frete = pesoTotal.multiply(DescontoFrete.SETE_POR_KG.getValor()); // R$ 7,00 por kg
        }

        return aplicarDescontoFrete(frete, cliente);
    }
}