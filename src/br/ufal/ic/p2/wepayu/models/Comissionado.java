package br.ufal.ic.p2.wepayu.models;

import java.util.TreeMap;
import java.util.Map;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Comissionado extends Empregado {
    private double comissao;
    private Map<String, Double> vendas = new TreeMap<>();
    
    public Comissionado () {
        
    }

    public Comissionado (String nome, String endereco, String tipo, String salario, String comissao) {
        super(nome, endereco, tipo, salario);
        this.comissao = Double.parseDouble(comissao.replace(",","."));
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

    // public Double calculaSalario (LocalDate data) {
    //     LocalDate di = LocalDate.of(2005, 1, 1);
    //     for (LocalDate d = di; d.isBefore(data); d = d.plusDays(1)) {
            
    //     }
    // }
}
