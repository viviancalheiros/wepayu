package br.ufal.ic.p2.wepayu.services;

import br.ufal.ic.p2.wepayu.Exception.Data.DataInvalidaException;
import br.ufal.ic.p2.wepayu.Exception.Data.OrdemException;
import br.ufal.ic.p2.wepayu.Exception.Empregado.EmpregadoNaoExisteException;
import br.ufal.ic.p2.wepayu.Exception.Sindicato.IdNaoNuloException;
import br.ufal.ic.p2.wepayu.Exception.Sindicato.MembroNaoExisteException;
import br.ufal.ic.p2.wepayu.Exception.Sindicato.NaoSindicalizadoException;
import br.ufal.ic.p2.wepayu.Exception.Venda.ValorNaoNuloException;
import br.ufal.ic.p2.wepayu.models.Empregado;
import br.ufal.ic.p2.wepayu.utils.ConversorUtils;

import java.util.Map;
import java.time.LocalDate;
import java.util.ArrayList;

public class SindicatoService {
    
    public static void validarMembro (String membro) {
        if (membro.equals("") || membro.isEmpty()) {
                throw new IdNaoNuloException();
        } else if (membro.charAt(0) != 's') {
            try {
                Integer.parseInt(membro.substring(1));
            } catch (NumberFormatException e) {
                throw new MembroNaoExisteException();
            }
        } 
    }

    public static Empregado getEmpSindicato (String idSindicato, Map<String, String> dadosSindicais, 
        ArrayList<Empregado> empregados) throws EmpregadoNaoExisteException {
            if (idSindicato == null) {
                throw new NaoSindicalizadoException();
            }
            for (String emp : dadosSindicais.keySet()) {
                String id = dadosSindicais.get(emp);
                if (idSindicato.equals(id)) {
                    return EmpregadoService.getEmpregadoPorId(emp, empregados);
                }
            }
            throw new NaoSindicalizadoException();
    }

    public static void lancaTaxaServico (Empregado e, String data, String valor,
        Map<String, String> dadosSindicais, ArrayList<Empregado> empregados)
            throws EmpregadoNaoExisteException, DataInvalidaException, 
            MembroNaoExisteException {
                LocalDate d = ConversorUtils.stringToDate(data, "data");
                Double v = Double.parseDouble(valor.replace(",", "."));
                if (v <= 0) throw new ValorNaoNuloException();
                e.setTaxaDia(d, v);
    }

    public static String getTaxasServico (Empregado e, String dataInicial, String dataFinal)
        throws EmpregadoNaoExisteException, DataInvalidaException {
            if (!e.getSindicalizado()) {
                throw new NaoSindicalizadoException();
            }
            LocalDate di = ConversorUtils.stringToDate(dataInicial, "inicial");
            LocalDate df = ConversorUtils.stringToDate(dataFinal, "final");
            if (di.isAfter(df)) {
                throw new OrdemException();
            }
            double total = 0;
            for (LocalDate date = di; date.isBefore(df); date = date.plusDays(1)) {
                total += e.getTaxaDia(date);
            }
            return ConversorUtils.converteSalario(total);
    }

}
