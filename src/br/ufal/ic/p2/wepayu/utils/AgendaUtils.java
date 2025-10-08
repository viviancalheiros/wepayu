package br.ufal.ic.p2.wepayu.utils;

import br.ufal.ic.p2.wepayu.Exception.AgendaException;

import java.util.ArrayList;

public class AgendaUtils {
    
    public static void verificaAgenda (String descricao, ArrayList<String> agenda) {
        String[] partes = descricao.split(" ");
        String tipo = null, valor1 = null, valor2 = null;
        int numero, numero2;
        tipo = partes[0];
        if (!(tipo.equals("semanal") ||
            tipo.equals("mensal"))) {
                throw new AgendaException("Descricao de agenda invalida");
        }
        if (partes.length < 2 || partes.length > 3) {
            throw new AgendaException("Descricao de agenda invalida");
        }
        valor1 = partes[1];
        numero = ConversorUtils.stringToInt(valor1);
        if (partes.length == 3) {
            valor2 = partes[2];
            numero2 = ConversorUtils.stringToInt(valor2);
        }
        boolean estaNaAgenda = jaExiste(agenda, descricao);
        if (estaNaAgenda) {
            throw new AgendaException("Agenda de pagamentos ja existe");
        } else {
            agenda.add(descricao);
        }
    }

    public static boolean jaExiste (ArrayList<String> agenda, String tipo) {
        for (String nome : agenda) {
            if (nome.equals(tipo)) return true;
        }
        return false;
    }
}
