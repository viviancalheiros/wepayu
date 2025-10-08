package br.ufal.ic.p2.wepayu.models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import br.ufal.ic.p2.wepayu.utils.*;

public class Assalariado extends Empregado {

    public Assalariado () {
        
    }
    
    public Assalariado (String nome, String endereco, String tipo, String salario) {
        super(nome, endereco, tipo, salario);
        setDataInicio(LocalDate.of(2005, 1, 1));
        setAgendaPagamento("mensal $");
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
            LocalDate inicio = data.withDayOfMonth(1);
            if (this.getAgendaPagamento() == "semanal 2 5") {
                inicio = getUltimoPagamentoD();
                if (inicio == null) {
                    inicio = getDataInicioD();
                }
            }
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

    @Override
    public double getSalario () {
        double salario = super.getSalario();
        if (this.getAgendaPagamento().equals("semanal 2 5")) {
            salario = ConversorUtils.truncar(salario * 12 / 26.0);
        } else if (this.getAgendaPagamento().equals("semanal 5")) {
            salario = ConversorUtils.truncar(salario * 12 / 52.0);
        }
        return salario;
    }
}
