package br.ufal.ic.p2.wepayu.Exception.Atributo;

public class AtributoNaoExisteException extends RuntimeException {
    public AtributoNaoExisteException () {
        super("Atributo nao existe.");
    }
}
