package br.ufal.ic.p2.wepayu.models;

import java.util.TreeMap;

import br.ufal.ic.p2.wepayu.utils.ConversorUtils;

import java.util.Map;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

public class Comissionado extends Empregado {
    private double comissao;
    private Map<String, Double> vendas = new TreeMap<>();

    public Comissionado () {
        
    }

    public Comissionado (String nome, String endereco, String tipo, String salario, String comissao) {
        super(nome, endereco, tipo, salario);
        this.comissao = Double.parseDouble(comissao.replace(",","."));
        setDataInicio(LocalDate.of(2005, 1, 1));
        setAgendaPagamento("semanal 2 5");
    }

    public double getComissao () {
        return this.comissao;
    }

    public void setComissao (double comissao) {
        this.comissao = comissao;
    }

    public double getVendas (LocalDate data) { 
        String d = data.format(DateTimeFormatter.ofPattern("d/M/yyyy"));
        return this.vendas.getOrDefault(d, 0.0);
    }

    public void setVendas(LocalDate data, double valor) {
        String d = data.format(DateTimeFormatter.ofPattern("d/M/yyyy"));
        this.vendas.put(d, valor);
    }

    public Map<String, Double> getVendasMap() {
        return this.vendas;
    }
    
    public void setVendasMap(Map<String, Double> vendas) {
        this.vendas = vendas;
    }

    private double calculaTaxas (LocalDate inicio, LocalDate fim) {
        double total = 0.0;
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
            inicio = data.minusDays(14);
            long dias = ChronoUnit.DAYS.between(inicio, data);
            double taxaSindTotal = dias * getTaxaSindical();
            double taxasTotal = calculaTaxas(inicio, data);
            descontos = taxaSindTotal + taxasTotal;
        }
        return descontos;
    }

    public double getSalario (LocalDate data) {
        double salarioBase = ConversorUtils.truncar(getSalario() * 12 / 26.0);

        if (this.getAgendaPagamento().equals("mensal $")) {
            salarioBase = ConversorUtils.truncar(getSalario());
        } else if (this.getAgendaPagamento().equals("semanal 5")) {
            salarioBase = ConversorUtils.truncar(getSalario() * 12 / 52.0);
        }
    
        double totalVendas = getTotalVendas(data);

        double comissaoVendas = ConversorUtils.truncar(totalVendas * getComissao());
        double salarioBruto = salarioBase + comissaoVendas;

        if (salarioBruto < 0) return 0;
        return salarioBruto;
    }

    public double getFixo (LocalDate data) {
        double totalVendas = ConversorUtils.truncar(getTotalVendas(data));
        double fixo = getSalario(data) - ConversorUtils.truncar(totalVendas*getComissao());
        return fixo;
    }

    public double getSalarioLiquido (LocalDate data) {
        double totalVendas = getTotalVendas(data);
        double salario = getFixo(data) + ConversorUtils.truncar(totalVendas*getComissao());
        double descontos = getDescontos(data);
        return salario - descontos;
    }

    public double getTotalVendas(LocalDate dataPagamento) {
        double totalVendas = 0;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
        
        LocalDate inicioPeriodo;
        
        if (getUltimoPagamentoD() == null) {
            inicioPeriodo = getDataInicioD();
        } else {
            inicioPeriodo = getUltimoPagamentoD();
        }

        if (this.getAgendaPagamento().equals("mensal $")) {
            inicioPeriodo = dataPagamento.withDayOfMonth(1);
        } else if (this.getAgendaPagamento().equals("semanal 5")) {
            inicioPeriodo = dataPagamento.minusDays(6);
        }
        
        for (Map.Entry<String, Double> entry : vendas.entrySet()) {
            try {
                LocalDate dataVenda = LocalDate.parse(entry.getKey(), formatter);
                
                if (!dataVenda.isBefore(inicioPeriodo) && !dataVenda.isAfter(dataPagamento)) {
                    totalVendas += entry.getValue();
                }
            } catch (DateTimeParseException e) {
                
            }
        }
        
        return totalVendas;
    }

    public double getComissaoTotal (LocalDate data) {
        double vendas = ConversorUtils.truncar(getTotalVendas(data));
        return ConversorUtils.truncar(vendas*getComissao());
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Comissionado clone = (Comissionado) super.clone();
        clone.comissao = this.comissao;
        clone.vendas = new TreeMap<>(this.vendas);
        return clone;
    }
}
