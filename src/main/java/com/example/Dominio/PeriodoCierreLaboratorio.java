package com.example.Dominio;

public class PeriodoCierreLaboratorio {
    private int periodoId;
    private int cierreLaboratorioId;

    public PeriodoCierreLaboratorio() {}

    public PeriodoCierreLaboratorio(int periodoId, int cierreLaboratorioId) {
        this.periodoId = periodoId;
        this.cierreLaboratorioId = cierreLaboratorioId;
    }

    public int getPeriodoId() { return periodoId; }
    public void setPeriodoId(int periodoId) { this.periodoId = periodoId; }

    public int getCierreLaboratorioId() { return cierreLaboratorioId; }
    public void setCierreLaboratorioId(int cierreLaboratorioId) { this.cierreLaboratorioId = cierreLaboratorioId; }
}

