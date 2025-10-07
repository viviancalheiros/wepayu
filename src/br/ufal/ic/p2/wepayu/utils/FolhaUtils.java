package br.ufal.ic.p2.wepayu.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.time.LocalDate;

import br.ufal.ic.p2.wepayu.models.*;

public class FolhaUtils {
    
    public static void printaHoristas (BufferedWriter writer, LocalDate d)
        throws IOException {
        writer.write("====================================");
        writer.newLine();
        writer.newLine();
        writer.write("===============================================================================================================================");
        writer.newLine();
        writer.write("===================== HORISTAS ================================================================================================");
        writer.newLine();
        writer.write("===============================================================================================================================");
        writer.newLine();
        writer.write("Nome                                 Horas Extra Salario Bruto Descontos Salario Liquido Metodo");
        writer.newLine();
        writer.write("==================================== ===== ===== ============= ========= =============== ======================================");
        writer.newLine();
    }

    public static void printaValorHoristas (String nome, double hn, double hx, double bruto,
        double descontos, double liquido, String metodo, BufferedWriter writer)
        throws IOException {
            writer.write(String.format(
                "%-36s %5s %5s %13s %9s %15s %-38s",
                nome, 
                String.format("%.0f", hn),
                 String.format("%.0f", hx),
                ConversorUtils.converteSalario(bruto),
                ConversorUtils.converteSalario(descontos),
                ConversorUtils.converteSalario(liquido),
                metodo
            ));
            writer.newLine();
    }

    public static void printaTotalHoristas (double hn, double hx, double bruto, double descontos, 
        double liquido, BufferedWriter writer) throws IOException {
                writer.newLine();
                writer.write(String.format(
                    "%-36s %5s %5s %13s %9s %15s", 
                    "TOTAL HORISTAS",
                    String.format("%.0f", hn),
                    String.format("%.0f", hx),
                    ConversorUtils.converteSalario(bruto),
                    ConversorUtils.converteSalario(descontos),
                    ConversorUtils.converteSalario(liquido)
                    ));
    }

    public static void printaAssalariados (BufferedWriter writer, LocalDate d)
        throws IOException {
        writer.newLine();
        writer.newLine();
        writer.write("===============================================================================================================================");
        writer.newLine();
        writer.write("===================== ASSALARIADOS ============================================================================================");
        writer.newLine();
        writer.write("===============================================================================================================================");
        writer.newLine();
        writer.write("Nome                                             Salario Bruto Descontos Salario Liquido Metodo");
        writer.newLine();
        writer.write("================================================ ============= ========= =============== ======================================");
        writer.newLine();
    }

    public static void printaValorAssalariados (String nome, double bruto, double liquido, 
        double descontos, String metodo, BufferedWriter writer) throws IOException {
            writer.write(String.format(
                "%-48s %13s %9s %15s %-38s",
                nome, 
                ConversorUtils.converteSalario(bruto),
                ConversorUtils.converteSalario(descontos),
                ConversorUtils.converteSalario(liquido),
                metodo
            ));
            writer.newLine();
    }

    public static void printaTotalAssalariados (double bruto, double descontos, double liquido,
        BufferedWriter writer) throws IOException {
            writer.newLine();
            writer.write(String.format(
                "%-48s %13s %9s %15s", 
                "TOTAL ASSALARIADOS",
                ConversorUtils.converteSalario(bruto),
                ConversorUtils.converteSalario(descontos),
                ConversorUtils.converteSalario(liquido)
                ));
    }

    public static void printaComissionados (BufferedWriter writer, LocalDate d)
        throws IOException {
        writer.newLine();
        writer.newLine();
        writer.write("===============================================================================================================================");
        writer.newLine();
        writer.write("===================== COMISSIONADOS ===========================================================================================");
        writer.newLine();
        writer.write("===============================================================================================================================");
        writer.newLine();
        writer.write("Nome                  Fixo     Vendas   Comissao Salario Bruto Descontos Salario Liquido Metodo");
        writer.newLine();
        writer.write("===================== ======== ======== ======== ============= ========= =============== ======================================");
        writer.newLine();
    }

    public static void printaValorComissionados (String nome, double fixo, double vendas, 
        double comissao, double bruto, double descontos, double liquido, String metodo,
        BufferedWriter writer) throws IOException {
            writer.write(String.format(
                "%-21s %8s %8s %8s %13s %9s %15s %-38s",
                nome,
                ConversorUtils.converteSalario(fixo),
                ConversorUtils.converteSalario(vendas),
                ConversorUtils.converteSalario(comissao),                     
                ConversorUtils.converteSalario(bruto),
                ConversorUtils.converteSalario(descontos),
                ConversorUtils.converteSalario(liquido),
                metodo
            ));
            writer.newLine();
    }
 
    public static void printaTotalComissionados (double fixo, double vendas, 
        double comissao, double bruto, double descontos, double liquido, 
        BufferedWriter writer) throws IOException {
            writer.newLine();
            writer.write(String.format(
                "%-21s %8s %8s %8s %13s %9s %15s", 
                "TOTAL COMISSIONADOS",
                ConversorUtils.converteSalario(fixo),
                ConversorUtils.converteSalario(vendas),
                ConversorUtils.converteSalario(comissao),                     
                ConversorUtils.converteSalario(bruto),
                ConversorUtils.converteSalario(descontos),
                ConversorUtils.converteSalario(liquido)
            ));
            writer.newLine();
            writer.newLine();
    }

    public static String formataMetodoPagamento (Empregado e) {
        String metodo;
        if (e.getMetodoPagamento().equals("emMaos")) {
            metodo = "Em maos";
        } else if (e.getMetodoPagamento().equals("banco")) {
            metodo = e.getBanco() + 
                    ", Ag. " + e.getAgencia() + 
                    " CC " + e.getContaCorrente();
        } else if (e.getMetodoPagamento().equals("correios")) {
            metodo = "Correios, " + e.getEndereco(); 
        } else {
            metodo = e.getMetodoPagamento();
        }
        return metodo;
    }

}
