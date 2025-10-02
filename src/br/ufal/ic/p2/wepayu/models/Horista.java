package br.ufal.ic.p2.wepayu.models;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.TreeMap;

public class Horista extends Empregado {
    private Map<String, Double> horasNormais = new TreeMap<>();
    private Map<String, Double> horasExtras = new TreeMap<>();
    private LocalDate dataInicio = null;

    public Horista () {
        
    }

    public Horista(String nome, String endereco, String tipo, String salario) {
        super(nome, endereco, tipo, salario);
    }
    
    public double getHorasNormais (LocalDate data) {
        String d = String.valueOf(data);
        return this.horasNormais.getOrDefault(d, 0.0);
    }

    public double getHorasExtras (LocalDate data) {
        String d = String.valueOf(data);
        return this.horasExtras.getOrDefault(d, 0.0);
    }

    public void setHoras (LocalDate data, double horas) {
        if (this.dataInicio == null) {
            this.dataInicio = data;
        }
        String d = String.valueOf(data);
        if (horas <= 8) {
            this.horasNormais.put(d, horas);
            this.horasExtras.put(d, 0.0);
        } else {
            this.horasNormais.put(d, 8.0);
            this.horasExtras.put(d, horas-8.0);
        }
    }

    public Map<String, Double> getHorasNormaisMap() {
        return horasNormais;
    }

    public void setHorasNormaisMap(Map<String, Double> horas) {
        this.horasNormais = horas;
    }

    public Map<String, Double> getHorasExtrasMap() {
        return horasExtras;
    }

    public void setHorasExtrasMap(Map<String, Double> horas) {
        this.horasExtras = horas;
    }

    public boolean recebeHoje(LocalDate data) {
        return data.getDayOfWeek() == DayOfWeek.FRIDAY;
    }

    private double calculaTaxas(LocalDate inicio, LocalDate fim) {
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

    public double getDescontos (LocalDate data, double salarioBruto) {
        double descontos = 0;
        if (salarioBruto > 0 && getSindicalizado() == true) {
            LocalDate inicio;
            if (getUltimoPagamentoD() == null) inicio = getDataInicioD();
            else inicio = getUltimoPagamentoD();
            if (inicio != null) {
                long dias = ChronoUnit.DAYS.between(inicio, data);
                double taxaSindTotal = (dias+1) * getTaxaSindical();
                double totalTaxas = calculaTaxas(inicio, data);
                descontos = taxaSindTotal + totalTaxas;
            }
        }
        return descontos;
    }

    public double getSalarioBruto (LocalDate data) {
        LocalDate inicioSemana = data.minusDays(6);
        double hn = getHnSemanal(inicioSemana, data);
        double hx = getHxSemanal(inicioSemana, data);

        double valorHora = getSalario();
        double salarioBruto = (hn * valorHora) + (hx * valorHora * 1.5);
        
        if (salarioBruto < 0) salarioBruto = 0;
        return Math.max(salarioBruto, 0);
    }

    public double getSalarioLiquido (LocalDate data) {
        double bruto = getSalarioBruto(data);
        double descontos = getDescontos(data, bruto);
        return bruto - descontos;
    }

    public double getHnSemanal (LocalDate inicio, LocalDate fim) {
        double hn = 0;
        if (inicio == null) {
            return 0.0;
        }
        for (; !inicio.isAfter(fim); inicio = inicio.plusDays(1)) {
            hn += getHorasNormais(inicio);
        }
        return hn;
    }

    public double getHxSemanal (LocalDate inicio, LocalDate fim) {
        double hx = 0;
        if (inicio == null) {
            return 0.0;
        }
        for (; !inicio.isAfter(fim); inicio = inicio.plusDays(1)) {
            hx += getHorasExtras(inicio);
        }
        return hx;
    }

}
