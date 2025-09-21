package br.ufal.ic.p2.wepayu.models;

import java.util.Map;
import java.util.TreeMap;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public abstract class Empregado implements Serializable {
    private static long contador = 1;

    private String nome;
    private String endereco;
    private long id;
    private String tipo;
    private double salario;

    private boolean sindicalizado;
    private String idSindicato;
    private double taxaSindical;
    private Map<String, Double> taxaDia = new TreeMap<>();

    private String metodoPagamento;
    private String banco;
    private String agencia;
    private String contaCorrente;

    private LocalDate ultimoPagamento;
    private LocalDate dataInicio;

    public Empregado () {

    }

    public Empregado (String nome, String endereco, String tipo, String salario) {
        this.nome = nome;
        this.endereco = endereco;
        this.tipo = tipo;
        this.salario = Double.parseDouble(salario.replace(",", "."));
        this.id = contador++;

        this.sindicalizado = false;
        this.idSindicato = null;
        this.taxaSindical = 0;

        this.metodoPagamento = "emMaos";
        this.banco = null;
        this.agencia = null;
        this.contaCorrente = null;

        this.ultimoPagamento = null;
        this.dataInicio = null;
    }

    public long getId () {
        return this.id;
    }

    public void setId (long id) {
        this.id = id;
    }

    public String getNome () {
        return this.nome;
    }

    public void setNome (String nome) {
        this.nome = nome;
    }

    public String getEndereco () {
        return this.endereco;
    }

    public void setEndereco (String endereco) {
        this.endereco = endereco;
    }

    public String getTipo () {
        return this.tipo;
    }

    public void setTipo (String tipo) {
        this.tipo = tipo;
    }

    public double getSalario () {
        return this.salario;
    }

    public void setSalario (double salario) {
        this.salario = salario;
    }

    public LocalDate getUltimoPagamentoD () {
        return this.ultimoPagamento;
    }

    public void setUltimoPagamento (LocalDate data) {
        this.ultimoPagamento = data;
    }

    public LocalDate getDataInicioD () {
        return this.dataInicio;
    }

    public void setDataInicio (LocalDate data) {
        this.dataInicio = data;
    }

    public String getUltimoPagamento () {
        String pag = String.valueOf(getUltimoPagamentoD());
        return pag;
    }

    public String getDataInicio () {
        String dia = String.valueOf(getDataInicioD());
        return dia;
    }

    public boolean getSindicalizado () {
        return this.sindicalizado;
    }

    public void setSindicalizado (boolean sindicalizado) {
        this.sindicalizado = sindicalizado;
    }

    public String getIdSindicato () {
        return this.idSindicato;
    }

    public double getTaxaSindical () {
        return this.taxaSindical;
    }

    public void setSindicalizado (boolean sindicalizado, String idSindicato, 
            double taxaSindical) {
        this.sindicalizado = sindicalizado;
        this.idSindicato = idSindicato;
        this.taxaSindical = taxaSindical;
    }

    public double getTaxaDia (LocalDate data) {
        String d = data.format(DateTimeFormatter.ofPattern("d/M/yyyy"));
        return this.taxaDia.getOrDefault(d, 0.0);
    }

    public void setTaxaDia (LocalDate data, double taxa) {
        String d = data.format(DateTimeFormatter.ofPattern("d/M/yyyy"));
        this.taxaDia.put(d, taxa);
    }

    public Map<String, Double> getTaxaDiaMap() {
        return this.taxaDia;
    }

    public void setTaxaDiaMap (Map<String, Double> taxas) {
        this.taxaDia = taxas;
    }

    public String getMetodoPagamento () {
        return this.metodoPagamento;
    }

    public void setMetodoPagamento (String metodo) {
        this.metodoPagamento = metodo;
    }

    public String getBanco () {
        return this.banco;
    }

    public String getAgencia () {
        return this.agencia;
    }

    public String getContaCorrente () {
        return this.contaCorrente;
    }

    public void setMetodoPagamento (String metodoPagamento, String banco, 
            String agencia, String contaCorrente) {
        this.metodoPagamento = metodoPagamento;
        this.banco = banco;
        this.agencia = agencia;
        this.contaCorrente = contaCorrente;
    }

}
