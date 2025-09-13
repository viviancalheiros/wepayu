package br.ufal.ic.p2.wepayu.Exception.Atributo;

public class AtributoNaoNulo extends RuntimeException {
    public AtributoNaoNulo (String atributo) {
        super(atributo + " nao pode ser nulo.");
        //para nome, endereco e salario
    }
}
