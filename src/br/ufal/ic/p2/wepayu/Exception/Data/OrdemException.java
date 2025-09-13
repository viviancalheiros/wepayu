package br.ufal.ic.p2.wepayu.Exception.Data;

public class OrdemException extends RuntimeException {
    public OrdemException () {
        super("Data inicial nao pode ser posterior aa data final.");
    }
}
