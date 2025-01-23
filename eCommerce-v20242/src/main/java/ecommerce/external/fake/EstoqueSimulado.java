package ecommerce.external.fake;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import ecommerce.dto.DisponibilidadeDTO;
import ecommerce.dto.EstoqueBaixaDTO;
import ecommerce.external.IEstoqueExternal;

@Service
public class EstoqueSimulado implements IEstoqueExternal {

    private final Map<Long, Integer> estoque;

     public EstoqueSimulado() {
        estoque = new HashMap<>();
        estoque.put(1L, 10); 
        estoque.put(2L, 5);  
        estoque.put(3L, 0);  
    }
    @Override
    public EstoqueBaixaDTO darBaixa(List<Long> produtosIds, List<Long> produtosQuantidades) {
        for (int i = 0; i < produtosIds.size(); i++) {
            Long produtoId = produtosIds.get(i);
            Integer quantidade = produtosQuantidades.get(i).intValue();

            if (estoque.containsKey(produtoId) && estoque.get(produtoId) >= quantidade) {
                estoque.put(produtoId, estoque.get(produtoId) - quantidade); 
            } else {
                return new EstoqueBaixaDTO(false); 
            }
        }
        return new EstoqueBaixaDTO(true);
    }

    @Override
    public DisponibilidadeDTO verificarDisponibilidade(List<Long> produtosIds, List<Long> produtosQuantidades) {
        List<Long> indisponiveis = new ArrayList<>();

        for (int i = 0; i < produtosIds.size(); i++) {
            Long produtoId = produtosIds.get(i);
            Integer quantidade = produtosQuantidades.get(i).intValue();

            if (!estoque.containsKey(produtoId) || estoque.get(produtoId) < quantidade) {
                indisponiveis.add(produtoId);   
            }
        }
        return new DisponibilidadeDTO(indisponiveis.isEmpty(), indisponiveis);
    }
}
