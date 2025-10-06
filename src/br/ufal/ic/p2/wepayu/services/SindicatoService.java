package br.ufal.ic.p2.wepayu.services;

import br.ufal.ic.p2.wepayu.Exception.Empregado.EmpregadoNaoExisteException;
import br.ufal.ic.p2.wepayu.Exception.Sindicato.IdNaoNuloException;
import br.ufal.ic.p2.wepayu.Exception.Sindicato.MembroNaoExisteException;
import br.ufal.ic.p2.wepayu.Exception.Sindicato.NaoSindicalizadoException;
import br.ufal.ic.p2.wepayu.models.Empregado;

import java.util.Map;
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
}
