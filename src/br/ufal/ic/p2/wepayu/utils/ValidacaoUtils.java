package br.ufal.ic.p2.wepayu.utils;

import br.ufal.ic.p2.wepayu.Exception.Atributo.AtributoNaoNulo;
import br.ufal.ic.p2.wepayu.models.Empregado;

public class ValidacaoUtils {
    
    public static void verificarEmpregado (Empregado e) {
        if (e.getNome().equals("") || e.getNome().equals(null)) {
            throw new AtributoNaoNulo("Nome");
        } else if (e.getEndereco().equals("") || e.getEndereco().equals(null)) {
            throw new AtributoNaoNulo("Endereco");
        }
    }

    public static void verificarSalario (String salario) {
        if (salario == null || salario.isEmpty()) {
            throw new IllegalStateException("Salario nao pode ser nulo.");
        }
        try {
            Double sal = Double.parseDouble(salario.replace(",", "."));
            if (sal < 0) {
                throw new IllegalStateException("Salario deve ser nao-negativo.");
            }
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Salario deve ser numerico.");
        }
    }

    public static void verificarComissao (String comissao) {
        if (comissao == null || comissao.isEmpty()) {
            throw new IllegalStateException("Comissao nao pode ser nula.");
        }
        try {
            Double com = Double.parseDouble(comissao.replace(",", "." ));
            if (com < 0) {
                throw new IllegalStateException("Comissao deve ser nao-negativa.");
            }
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Comissao deve ser numerica.");
        }
    }

}
