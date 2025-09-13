package br.ufal.ic.p2.wepayu.Exception.Venda;

public class ValorNaoNuloException extends RuntimeException {
    public ValorNaoNuloException () {
        super("Valor deve ser positivo.");
    }
}
