package br.ufal.ic.p2.wepayu.utils;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import br.ufal.ic.p2.wepayu.Exception.Data.DataInvalidaException;

public class ConversorUtils {
    
    public static String converteSalario (double salario) {
        String sal = String.format("%.2f", salario).replace(".", ",");
        return sal;
    }

    public static String converteSindicalizado (boolean sindicalizado) {
        String s = String.valueOf(sindicalizado);
        return s;
    }

    public static String formatarHoras (double horas) {
        String resultado = String.valueOf(horas).replace(".", ",");
        if (resultado.endsWith(",0")) { 
            return resultado.substring(0, resultado.length()-2);
        } else if (resultado.endsWith(",00")) {
            return resultado.substring(0, resultado.length()-3);
        }
        return resultado;
    }

    public static LocalDate stringToDate (String data, String tipo)
            throws DataInvalidaException {
        DateTimeFormatter[] formatos = {
            DateTimeFormatter.ofPattern("d/M/yyyy"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy")
        };
        String[] partes = data.split("/");
        if (partes.length != 3) throw new DataInvalidaException(tipo);
        int dia = Integer.parseInt(partes[0]);
        int mes = Integer.parseInt(partes[1]);
        int ano = Integer.parseInt(partes[2]);
        
        if (mes < 1 || mes > 12) throw new DataInvalidaException(tipo);
        if (dia < 1 || dia > 31) throw new DataInvalidaException(tipo);

        if (mes == 2) {
            boolean bissexto = (ano % 4 == 0 && (ano % 100 != 0 || ano % 400 == 0));
            if (dia > (bissexto ? 29:28)) throw new DataInvalidaException(tipo);
        } else if (mes == 4 || mes == 6 || mes == 9 || mes == 11) {
            if (dia > 30) throw new DataInvalidaException(tipo);
        }
        for (DateTimeFormatter formato : formatos) {
            try {
                LocalDate dataValida = LocalDate.parse(data, formato);
                
                if (dataValida.getDayOfMonth() != dia ||
                    dataValida.getMonthValue() != mes ||
                    dataValida.getYear() != ano) {
                        throw new DataInvalidaException(tipo);
                }
                
                return dataValida;
            } catch (DateTimeParseException e) {
                continue;
            } catch (DateTimeException e2) {
                throw new DataInvalidaException(tipo);
            }
        }
        throw new DataInvalidaException(tipo);
    }

    public static double truncar(double valor) {
        return Math.floor(valor * 100) / 100.0;
    }

}
