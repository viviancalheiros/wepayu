package br.ufal.ic.p2.wepayu.services;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import br.ufal.ic.p2.wepayu.Exception.Data.*;
import br.ufal.ic.p2.wepayu.Exception.Empregado.*;
import br.ufal.ic.p2.wepayu.models.Comissionado;
import br.ufal.ic.p2.wepayu.models.Empregado;
import br.ufal.ic.p2.wepayu.utils.ConversorUtils;

public class ComissionadoService {

    public static String getVendasRealizadas (Empregado e, String dataInicial, String dataFinal) 
            throws EmpregadoNaoExisteException, DataInvalidaException {
        if (!(e instanceof Comissionado)) {
            throw new TipoEmpregadoException("Empregado nao eh comissionado.");
        }
        Comissionado c = (Comissionado) e;
        LocalDate di = ConversorUtils.stringToDate(dataInicial, "inicial");
        LocalDate df = ConversorUtils.stringToDate(dataFinal, "final");
        if (di.isAfter(df)) {
            throw new OrdemException();
        }
        double totalVendas = 0;
        for (LocalDate date = di; date.isBefore(df); date = date.plusDays(1)) {
            totalVendas += c.getVendas(date);
        }
        return ConversorUtils.converteSalario(totalVendas);
    }

    


}
