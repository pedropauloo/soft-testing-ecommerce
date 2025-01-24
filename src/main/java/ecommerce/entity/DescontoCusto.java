package ecommerce.entity;

import java.math.BigDecimal;

public enum DescontoCusto {
    DESCONTO_10(BigDecimal.valueOf(0.90)),
    DESCONTO_20(BigDecimal.valueOf(0.80));

    private final BigDecimal valor;

    DescontoCusto(BigDecimal valor) {
        this.valor = valor;
    }

    public BigDecimal aplicar(BigDecimal total) {
        return total.multiply(valor);
    }
}
