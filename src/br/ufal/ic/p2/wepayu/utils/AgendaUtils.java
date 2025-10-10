package br.ufal.ic.p2.wepayu.utils;

import br.ufal.ic.p2.wepayu.Exception.AgendaException;

import java.util.List;

public class AgendaUtils {
    
    public static void verificaCriaAgenda (String descricao, List<String> agenda) {
        String[] partes = descricao.split(" ");
        String tipo = partes[0];
        String valor1, valor2;
        if (partes.length < 2 || partes.length > 3) {
            throw new AgendaException("Descricao de agenda invalida");
        }

        valor1 = partes[1];
        if (tipo.equals("mensal")) {
            ConversorUtils.stringToIntLim(valor1, 1, 28);
        } else if (tipo.equals("semanal")) {
            if (partes.length == 2) {
                ConversorUtils.stringToIntLim(valor1, 1, 7);
            } else if (partes.length == 3) {
                valor2 = partes[2];
                ConversorUtils.stringToIntLim(valor1, 1, 52);
                ConversorUtils.stringToIntLim(valor2, 1, 7);
            }
        } else {
            throw new AgendaException("Descricao de agenda invalida");
        }

        boolean estaNaAgenda = jaExiste(agenda, descricao);
        if (estaNaAgenda) {
            throw new AgendaException("Agenda de pagamentos ja existe");
        } else {
            agenda.add(descricao);
        }
    }

    public static boolean jaExiste (List<String> agenda, String tipo) {
        for (String nome : agenda) {
            if (nome.equals(tipo)) return true;
        }
        return false;
    }
}
