package br.ufal.ic.p2.wepayu.Exception.Sindicato;

public class NaoSindicalizadoException extends RuntimeException {
    public NaoSindicalizadoException () {
        super("Empregado nao eh sindicalizado.");
    }
}
