package br.ufal.ic.p2.wepayu.models;

import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;

public class Horista extends Empregado {
    private double horas;
    private Map<LocalDate, Double> horasNormais = new TreeMap<>();
    private Map<LocalDate, Double> horasExtras = new TreeMap<>();

    public Horista () {
        
    }

    public Horista(String nome, String endereco, String tipo, String salario) {
        super(nome, endereco, tipo, salario);
        this.horas = 8;
    }

    public void calcularSalario () {
        if (this.horas > 8) {
            setSalario((this.getSalario()*1.5)*this.horas-8);
        } else {
            setSalario(getSalario()*8);
        }
    }
    
    public double getHorasNormais (LocalDate data) {
        return this.horasNormais.getOrDefault(data, 0.0);
    }

    public double getHorasExtras (LocalDate data) {
        return this.horasExtras.getOrDefault(data, 0.0);
    }

    public void setHoras (LocalDate data, double horas) {
        if (horas <= 8) {
            this.horasNormais.put(data, horas);
            this.horasExtras.put(data, 0.0);
        } else {
            this.horasNormais.put(data, 8.0);
            this.horasExtras.put(data, horas-8.0);
        }
    }

}
