package br.ufal.ic.p2.wepayu.Exception.Sindicato;

public class IdIgualException extends RuntimeException {
    public IdIgualException () {
        super("Ha outro empregado com esta identificacao de sindicato");
    }
}
