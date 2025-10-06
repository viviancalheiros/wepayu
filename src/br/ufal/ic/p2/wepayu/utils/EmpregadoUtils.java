package br.ufal.ic.p2.wepayu.utils;

import br.ufal.ic.p2.wepayu.Exception.Atributo.TipoAtributoException;
import br.ufal.ic.p2.wepayu.models.Assalariado;
import br.ufal.ic.p2.wepayu.models.Comissionado;
import br.ufal.ic.p2.wepayu.models.Empregado;
import br.ufal.ic.p2.wepayu.models.Horista;

public class EmpregadoUtils {
    
    public static Empregado mudaTipo (Empregado e, String novoTipo) {
        if (!(novoTipo.equals("comissionado") ||
                novoTipo.equals("horista") ||
                novoTipo.equals("assalariado"))) {
            throw new TipoAtributoException("Tipo invalido.");
        }
        if (novoTipo.equals("assalariado")) {
            Assalariado a = new Assalariado(
                e.getNome(), 
                e.getEndereco(), 
                "assalariado", 
                ConversorUtils.converteSalario(e.getSalario())
            );
            a.setId(e.getId());
            return a;
        } else if (novoTipo.equals("horista")) {
            Horista h = new Horista(
                e.getNome(), 
                e.getEndereco(), 
                "horista", 
                ConversorUtils.converteSalario(e.getSalario())
            );
            h.setId(e.getId());
            return h;
        } else if (novoTipo.equals("comissionado")) {
            Comissionado c = new Comissionado(
                e.getNome(), 
                e.getEndereco(), 
                "comissionado", 
                ConversorUtils.converteSalario(e.getSalario()),
                "0,0"
            );
            c.setId(e.getId());
            return c;
        }
        throw new TipoAtributoException("Tipo invalido.");
    }

}
