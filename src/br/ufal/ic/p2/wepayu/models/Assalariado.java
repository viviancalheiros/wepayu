package br.ufal.ic.p2.wepayu.models;

public class Assalariado extends Empregado {

    public Assalariado () {
        
    }
    
    public Assalariado (String nome, String endereco, String tipo, String salario) {
        super(nome, endereco, tipo, salario);
    }
}
