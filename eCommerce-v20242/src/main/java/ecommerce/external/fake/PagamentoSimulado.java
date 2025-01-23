package ecommerce.external.fake;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import ecommerce.dto.PagamentoDTO;
import ecommerce.external.IPagamentoExternal;

@Service
public class PagamentoSimulado implements IPagamentoExternal{
    
    private Map<Long, PagamentoDTO> pagamentos;
    private long proximoId = 1; 

    public PagamentoSimulado() {
        pagamentos = new HashMap<>(); 
    }
    @Override
    public PagamentoDTO autorizarPagamento(Long clienteId, Double custoTotal) {
        if (custoTotal > 0) {
            PagamentoDTO pagamento = new PagamentoDTO(true, proximoId); 
            pagamentos.put(proximoId, pagamento); 
            proximoId++; 
            return pagamento; 
        } else {
            return new PagamentoDTO(false, null);
        }
    }
    @Override
    public void cancelarPagamento(Long clienteId, Long pagamentoTransacaoId) {
        if (pagamentos.containsKey(pagamentoTransacaoId)) {
            pagamentos.remove(pagamentoTransacaoId); 
            System.out.println("Pagamento cancelado para a transação ID: " + pagamentoTransacaoId);
        } else {
            System.out.println("Transação ID não encontrada: " + pagamentoTransacaoId);
        }
    }
}
