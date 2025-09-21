package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.Exception.Empregado.EmpregadoNaoExisteException;
import br.ufal.ic.p2.wepayu.Exception.Sindicato.IdNaoNuloException;
import br.ufal.ic.p2.wepayu.Exception.Sindicato.MembroNaoExisteException;
import br.ufal.ic.p2.wepayu.controller.Controlador;

import java.io.IOException;
import java.io.Serializable;

import br.ufal.ic.p2.wepayu.Exception.Data.DataInvalidaException;
import br.ufal.ic.p2.wepayu.models.*;

public class Facade implements Serializable {
    Controlador c = new Controlador();

    public Facade () {
        iniciarSistema();
    }

    public void iniciarSistema () {
        c.iniciarSistema();
    }

    public void zerarSistema () {
        c.zerarSistema();
    }

    public String criarEmpregado (String nome, String endereco, String tipo, String salario) {
        return c.criarEmpregado(nome, endereco, tipo, salario);
    }

    public String criarEmpregado (String nome, String endereco, String tipo, String salario, String comissao) {
        return c.criarEmpregado(nome, endereco, tipo, salario, comissao);
    }

    public String getAtributoEmpregado (String emp, String atributo)
        throws EmpregadoNaoExisteException {
        return c.getAtributoEmpregado(emp, atributo);
    }

    public String getEmpregadoPorNome (String nome, String indice) {
            return c.getEmpregadoPorNome(nome, indice);
    }

    public void removerEmpregado (String emp) 
            throws EmpregadoNaoExisteException  {
        c.removerEmpregado(emp);
    }

    public void lancaCartao (String emp, String data, String horas) 
            throws EmpregadoNaoExisteException, DataInvalidaException {
        c.lancaCartao(emp, data, horas);
    }

    public String getHorasNormaisTrabalhadas (String emp, String dataInicial, String dataFinal) 
            throws EmpregadoNaoExisteException, DataInvalidaException {
        return c.getHorasNormaisTrabalhadas(emp, dataInicial, dataFinal);
    }

    public String getHorasExtrasTrabalhadas (String emp, String dataInicial, String dataFinal) 
            throws EmpregadoNaoExisteException, DataInvalidaException {
        return c.getHorasExtrasTrabalhadas(emp, dataInicial, dataFinal);
    }

    public void lancaVenda (String emp, String data, String valor)
            throws EmpregadoNaoExisteException, DataInvalidaException {
        c.lancaVenda(emp, data, valor);
    }

    public String getVendasRealizadas (String emp, String dataInicial, String dataFinal) 
            throws EmpregadoNaoExisteException, DataInvalidaException {
        return c.getVendasRealizadas(emp, dataInicial, dataFinal);
    }

    public void alteraEmpregado (String emp, String atributo, String valor)
        throws EmpregadoNaoExisteException {
            c.alteraEmpregado(emp, atributo, valor);
    }

    public void alteraEmpregado (String emp, String atributo, String valor, 
    String comissao) 
        throws EmpregadoNaoExisteException {
            c.alteraEmpregado(emp, atributo, valor, comissao);
    }
    
    public void alteraEmpregado (String emp, String atributo, String valor, 
    String idSindicato, String taxaSindical) 
    throws EmpregadoNaoExisteException {
        c.alteraEmpregado(emp, atributo, valor, idSindicato, taxaSindical);
    }

    public void alteraEmpregado (String emp, String atributo, String valor1, 
    String banco, String agencia, String contaCorrente) 
        throws EmpregadoNaoExisteException {
            c.alteraEmpregado(emp, atributo, valor1, banco, agencia, contaCorrente);
    }

    public void lancaTaxaServico (String membro, String data, String valor)
        throws EmpregadoNaoExisteException, DataInvalidaException,
        MembroNaoExisteException, IdNaoNuloException  {
            c.lancaTaxaServico(membro, data, valor);
    }

    public String getTaxasServico (String emp, String dataInicial, String dataFinal)
        throws EmpregadoNaoExisteException, DataInvalidaException {
            return c.getTaxasServico(emp, dataInicial, dataFinal);
    }

    public String totalFolha (String data)
        throws DataInvalidaException {
            return c.totalFolha(data);
        }
    
    public void rodaFolha (String data, String saida)
        throws IOException, DataInvalidaException,
        EmpregadoNaoExisteException {
            c.rodaFolha(data, saida);
        }

    public void encerrarSistema () {
        c.encerrarSistema();
    }
}
