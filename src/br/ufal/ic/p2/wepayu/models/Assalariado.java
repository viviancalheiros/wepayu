package br.ufal.ic.p2.wepayu.models;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.time.DayOfWeek;

public class Assalariado extends Empregado {

    public Assalariado () {
        
    }
    
    public Assalariado (String nome, String endereco, String tipo, String salario) {
        super(nome, endereco, tipo, salario);
        setDataInicio(LocalDate.of(2005, 1, 1));
    }

    public boolean recebeHoje (LocalDate data) {
        YearMonth mes = YearMonth.from(data);
        LocalDate ehUltimo = mes.atEndOfMonth();

        if (ehUltimo.getDayOfWeek() == DayOfWeek.SATURDAY) {
            ehUltimo = ehUltimo.minusDays(1);
        } else if (ehUltimo.getDayOfWeek() == DayOfWeek.SUNDAY) {
            ehUltimo = ehUltimo.minusDays(2);
        }

        return data.equals(ehUltimo);
    }

    private double calculaTaxas (LocalDate inicio, LocalDate fim) {
        double total = 0;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
        for (Map.Entry<String, Double> entry : getTaxaDiaMap().entrySet()) {
            try {
                LocalDate dataTaxa = LocalDate.parse(entry.getKey(), formatter);
                if (!dataTaxa.isBefore(inicio) && !dataTaxa.isAfter(fim)) {
                    total += entry.getValue();
                }
            } catch (Exception e) {

            }
        }
        return total;
    }

    public double getDescontos (LocalDate data) {
        double descontos = 0;
        if (getSindicalizado() == true) {
            LocalDate inicio;
            inicio = data.withDayOfMonth(1);
            long dias = ChronoUnit.DAYS.between(inicio, data)+1;
            double taxaSindTotal = dias * getTaxaSindical();
            double taxasTotal = calculaTaxas(inicio, data);
            descontos = taxasTotal + taxaSindTotal;  
        }
        return descontos;
    }

    public double getSalarioLiquido (LocalDate data) {
        return getSalario() - getDescontos(data);
    }
}
