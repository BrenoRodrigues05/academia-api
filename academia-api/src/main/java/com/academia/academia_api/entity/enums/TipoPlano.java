package com.academia.academia_api.entity.enums;

public enum TipoPlano {
    MENSAL(1), TRIMESTRAL(3), ANUAL(12);
    private final int meses;
    TipoPlano(int meses) { this.meses = meses; }
    public int meses() { return meses; }
}
