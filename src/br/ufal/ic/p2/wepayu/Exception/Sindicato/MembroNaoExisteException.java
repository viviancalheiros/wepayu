package br.ufal.ic.p2.wepayu.Exception.Sindicato;

public class MembroNaoExisteException extends RuntimeException {
    public MembroNaoExisteException () {
        super("Membro nao existe.");
    }
}
