package ecommerce.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ecommerce.dto.CompraDTO;
import ecommerce.dto.DisponibilidadeDTO;
import ecommerce.dto.EstoqueBaixaDTO;
import ecommerce.dto.PagamentoDTO;
import ecommerce.entity.CarrinhoDeCompras;
import ecommerce.entity.Cliente;
import ecommerce.entity.ItemCompra;
import ecommerce.external.IEstoqueExternal;
import ecommerce.external.IPagamentoExternal;
import jakarta.transaction.Transactional;

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

		List<Long> produtosIds = carrinho.getItens().stream().map(i -> i.getProduto().getId())
				.collect(Collectors.toList());
		List<Long> produtosQtds = carrinho.getItens().stream().map(i -> i.getQuantidade()).collect(Collectors.toList());

		DisponibilidadeDTO disponibilidade = estoqueExternal.verificarDisponibilidade(produtosIds, produtosQtds);

		if (!disponibilidade.disponivel()) {
			throw new IllegalStateException("Itens fora de estoque.");
		}

		BigDecimal custoTotal = calcularCustoTotal(carrinho, cliente);
        BigDecimal pesoTotal = calcularPesoTotal(carrinho);
        BigDecimal frete = calcularFrete(pesoTotal, cliente);
        custoTotal = custoTotal.add(frete);

			
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

	public BigDecimal calcularCustoTotal(CarrinhoDeCompras carrinho, Cliente cliente) {
        BigDecimal custoProdutos = BigDecimal.ZERO;

        for (ItemCompra item : carrinho.getItens()) {
            BigDecimal precoUnitario = item.getProduto().getPreco(); 
            Long quantidade = item.getQuantidade();
            custoProdutos = custoProdutos.add(precoUnitario.multiply(BigDecimal.valueOf(quantidade)));
			
        }

        return aplicarDesconto(custoProdutos, cliente);
    }

	private BigDecimal aplicarDesconto(BigDecimal total, Cliente cliente) {
        // Aplica descontos baseados no valor total dos itens
        if (total.compareTo(BigDecimal.valueOf(1000)) > 0) {
            return total.multiply(BigDecimal.valueOf(0.80)); // 20% de desconto
        } else if (total.compareTo(BigDecimal.valueOf(500)) > 0) {
            return total.multiply(BigDecimal.valueOf(0.90)); // 10% de desconto
        }
        return total; 
    }

    private BigDecimal calcularPesoTotal(CarrinhoDeCompras carrinho) {
        BigDecimal pesoTotal = BigDecimal.ZERO;

        for (ItemCompra item : carrinho.getItens()) {
            pesoTotal = BigDecimal.valueOf(item.getProduto().getPeso()).multiply(BigDecimal.valueOf(item.getQuantidade())); 
		}
        return pesoTotal;
    }

	private BigDecimal calcularFrete(BigDecimal pesoTotal, Cliente cliente) {
        BigDecimal frete = BigDecimal.ZERO;

        if (pesoTotal.compareTo(BigDecimal.valueOf(5)) <= 0) {
            return BigDecimal.ZERO; // Frete gratuito até 5 kg
        } else if (pesoTotal.compareTo(BigDecimal.valueOf(10)) < 0) {
            frete = pesoTotal.multiply(BigDecimal.valueOf(2)); // R$ 2,00 por kg
        } else if (pesoTotal.compareTo(BigDecimal.valueOf(50)) < 0) {
            frete = pesoTotal.multiply(BigDecimal.valueOf(4)); // R$ 4,00 por kg
        } else {
            frete = pesoTotal.multiply(BigDecimal.valueOf(7)); // R$ 7,00 por kg
        }

        switch (cliente.getTipo()) {
            case OURO:
                return BigDecimal.ZERO; 
            case PRATA:
                return frete.multiply(BigDecimal.valueOf(0.50)); 
            case BRONZE:
            default:
                return frete; 
        }
    }
}
