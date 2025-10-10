package br.ufal.ic.p2.wepayu.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;

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

    public static String semanaOuMes (String agenda) {
        String partes[] = agenda.split(" ");
        return partes[0];
    }

    public static boolean verificaMensal (LocalDate data, String partes[]) {
        LocalDate dia;
        if (partes[1].equals("$")) {
            YearMonth mes = YearMonth.from(data);
            dia = mes.atEndOfMonth();
        } else {
            int diaInt = ConversorUtils.stringToInt(partes[1]);
            dia = data.withDayOfMonth(diaInt);
        }
        if (dia.getDayOfWeek() == DayOfWeek.SATURDAY) {
            if (data.isEqual(LocalDate.of(2005, 1, 1))) {
                return true;
            }
            dia = dia.minusDays(1);
        } else if (dia.getDayOfWeek() == DayOfWeek.SUNDAY) {
            dia = dia.minusDays(2);
        }
        return dia.isEqual(data);
    }

    public static boolean verificaSemanal1 (LocalDate data, String partes) {
        int diaInt = ConversorUtils.stringToInt(partes);
        DayOfWeek diaSemana = DayOfWeek.of(diaInt);
        return data.getDayOfWeek() == diaSemana;
    }

    public static boolean verificaSemanal2 (LocalDate data, String partes[], Empregado e) {
        if (!(verificaSemanal1(data, partes[2]))) {
            return false;
        }
        int semana = ConversorUtils.stringToInt(partes[1]);
        LocalDate inicio = e.getDataInicioD();
        if (inicio == null) return false;
        long diasDesdeInicio = ChronoUnit.DAYS.between(e.getUltimoPagamentoD(), data);
        if (diasDesdeInicio/7 < semana) return false;



        long diasDesdePag = diasDesdeInicio - 13;
        return diasDesdePag % (7*semana) == 0;
    }

    public static int calculaSemanas (LocalDate data, LocalDate inicio) {
        long diasEntre = ChronoUnit.DAYS.between(inicio, data);
        long semanasEntre = diasEntre/7;
        return (int) semanasEntre;
    }

}
