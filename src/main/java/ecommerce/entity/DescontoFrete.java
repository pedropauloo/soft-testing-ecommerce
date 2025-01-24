package ecommerce.entity;

import java.math.BigDecimal;

public enum DescontoFrete {
    FRETE_GRATIS(BigDecimal.ZERO),
    DOIS_POR_KG(BigDecimal.valueOf(2)),
    QUATRO_POR_KG(BigDecimal.valueOf(4)),
    SETE_POR_KG(BigDecimal.valueOf(7)),
    DESCONTO_50(BigDecimal.valueOf(0.50));


    private final BigDecimal valor;

    DescontoFrete(BigDecimal valor) {
        this.valor = valor;
    }

    public BigDecimal getValor() {
        return valor;
    }
}
