package br.ufal.ic.p2.wepayu.controller;

import java.beans.XMLEncoder;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.beans.XMLDecoder;


import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Map;
import java.util.TreeMap;

import br.ufal.ic.p2.wepayu.Exception.Atributo.*;
import br.ufal.ic.p2.wepayu.Exception.Data.*;
import br.ufal.ic.p2.wepayu.Exception.Empregado.*;
import br.ufal.ic.p2.wepayu.Exception.Horas.*;
import br.ufal.ic.p2.wepayu.Exception.Venda.*;
import br.ufal.ic.p2.wepayu.models.Assalariado;
import br.ufal.ic.p2.wepayu.models.Comissionado;
import br.ufal.ic.p2.wepayu.models.Empregado;
import br.ufal.ic.p2.wepayu.models.Horista;
import br.ufal.ic.p2.wepayu.Exception.Sindicato.*;

public class Controlador {
    Scanner s = new Scanner(System.in);
    ArrayList<Empregado> empregados = new ArrayList<>();
    Map<String, String> dadosSindicais = new TreeMap<>(); //id, idSindical

    public void iniciarSistema () {
        try {
            FileInputStream f = new FileInputStream("empregados.xml");
            XMLDecoder decoder = new XMLDecoder(f);
            this.empregados = (ArrayList<Empregado>) decoder.readObject();
            decoder.close();
            f.close();
        } catch (Exception e) {
            this.empregados = new ArrayList<>();
        }
    }

    public void zerarSistema () {
        empregados.clear();
        dadosSindicais.clear();
    }

    public void salvarDados () {
        try {
            FileOutputStream f = new FileOutputStream("empregados.xml");
            XMLEncoder encoder = new XMLEncoder(f);
            encoder.writeObject(empregados);
            encoder.close();
            f.close();
        } catch (Exception e) {

        }
    }

    private void verificarEmpregado (Empregado e) {
        if (e.getNome().equals("") || e.getNome().equals(null)) {
            throw new AtributoNaoNulo("Nome");
        } else if (e.getEndereco().equals("") || e.getEndereco().equals(null)) {
            throw new AtributoNaoNulo("Endereco");
        }
    }

    private String converteSalario (double salario) {
        String sal = String.format("%.2f", salario).replace(".", ",");
        return sal;
    }

    private String converteSindicalizado (boolean sindicalizado) {
        String s = String.valueOf(sindicalizado);
        return s;
    }

    private void verificarSalario (String salario) {
        if (salario == null || salario.isEmpty()) {
            throw new SalarioException("Salario nao pode ser nulo.");
        }
        try {
            Double sal = Double.parseDouble(salario.replace(",", "."));
            if (sal < 0) {
                throw new SalarioException("Salario deve ser nao-negativo.");
            }
        } catch (NumberFormatException e) {
            throw new SalarioException("Salario deve ser numerico.");
        }
    }

    private void verificarComissao (String comissao) {
        if (comissao == null || comissao.isEmpty()) {
            throw new ComissaoException("Comissao nao pode ser nula.");
        }
        try {
            Double com = Double.parseDouble(comissao.replace(",", "." ));
            if (com < 0) {
                throw new ComissaoException("Comissao deve ser nao-negativa.");
            }
        } catch (NumberFormatException e) {
            throw new ComissaoException("Comissao deve ser numerica.");
        }
    }

    public String criarEmpregado (String nome, String endereco, String tipo, String salario) {
        Empregado e;
        
        verificarSalario(salario);

        if (tipo.equals("assalariado")) {
            e = new Assalariado(nome, endereco, tipo, salario);
            verificarEmpregado(e);
        } else if (tipo.equals("horista")) {
            e = new Horista(nome, endereco, tipo, salario);
            verificarEmpregado(e);
        } else if (tipo.equals("comissionado")) {
            throw new TipoAtributoException("Tipo nao aplicavel.");
        } else {
            throw new TipoAtributoException("Tipo invalido.");
        }

        empregados.add(e);
        return String.valueOf(e.getId());
    }

    public String criarEmpregado (String nome, String endereco, String tipo, String salario, 
            String comissao) {
        
        verificarSalario(salario);

        if (tipo.equals("comissionado")) {
            verificarComissao(comissao);
            Comissionado c = new Comissionado(nome, endereco, tipo, salario, comissao);
            empregados.add(c);
            return String.valueOf(c.getId());
        } else if (tipo.equals("assalariado") || tipo.equals("horista")) {
            throw new TipoAtributoException("Tipo nao aplicavel.");
        } else {
            throw new TipoAtributoException("Tipo invalido.");
        }
    }

    public Empregado getEmpregadoPorId (String emp) throws EmpregadoNaoExisteException {
        if (emp == null || emp.isEmpty()) {
            if (empregados.isEmpty()) {
                throw new EmpregadoNaoExisteException();
            } else {
                throw new IdNuloException("Identificacao do empregado nao pode ser nula.");
            }
        }
        for (Empregado e : empregados) {
            String id = String.valueOf(e.getId());
            if (id.equals(emp)) {
                return e;
            }
        }
        throw new EmpregadoNaoExisteException();
    }

    public String getEmpregadoPorNome (String nome, String indice) {
            int i = Integer.parseInt(indice);
            for (Empregado e : empregados) {
                if (e.getNome().equals(nome)) {
                    if (i == 1) {
                        return String.valueOf(e.getId());
                    } 
                    i -= 1;
                }
            }
            throw new TipoEmpregadoException("Nao ha empregado com esse nome.");
    }

    private boolean recebeEmBanco (String emp)
        throws EmpregadoNaoExisteException {
            Empregado e = getEmpregadoPorId(emp);
            if (e.getMetodoPagamento().equals(("banco"))) {
                return true;
            }
            return false;
    }

    public String getAtributoEmpregado (String emp, String atributo)
            throws EmpregadoNaoExisteException, NaoSindicalizadoException {
        Empregado e = getEmpregadoPorId(emp);
        if (atributo.equals("nome")) {
            return e.getNome();
        } else if (atributo.equals("endereco")) {
            return e.getEndereco();
        } else if (atributo.equals("tipo")) {
            return e.getTipo();
        } else if (atributo.equals("salario")) {
            return converteSalario(e.getSalario());
        } else if (atributo.equals("sindicalizado")) {
            return converteSindicalizado(e.getSindicalizado());
        } else if (atributo.equals("comissao")) {
            if (e instanceof Comissionado) {
                Comissionado c = (Comissionado) e;
                return String.valueOf(c.getComissao()).replace(".", ",");
            } else {
                throw new ComissaoException("Empregado nao eh comissionado.");
            }
        } else if (atributo.equals("metodoPagamento")) {
            return e.getMetodoPagamento();
        } else if (atributo.equals("banco")) {
            if (recebeEmBanco(emp)) return e.getBanco();
            else throw new NaoBancoException();
        } else if (atributo.equals("agencia")) {
            if (recebeEmBanco(emp)) return e.getAgencia();
            else throw new NaoBancoException();
        } else if (atributo.equals("contaCorrente")) {
            if (recebeEmBanco(emp)) return e.getContaCorrente();
            else throw new NaoBancoException();
        } else if (atributo.equals("idSindicato")) {
            Empregado s = getEmpSindicato(e.getIdSindicato());
            return s.getIdSindicato();
        } else if (atributo.equals("taxaSindical")) {
            Empregado s = getEmpSindicato(e.getIdSindicato());
            String t = converteSalario(s.getTaxaSindical());
            return t;
        } else {
            throw new AtributoNaoExisteException();
        }
    }

    public void removerEmpregado (String emp) throws EmpregadoNaoExisteException {
        Empregado e = getEmpregadoPorId(emp);
        empregados.remove(e);
    }

    private LocalDate stringToDate (String data, String tipo)
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

    private String formatarHoras (double horas) {
        String resultado = String.valueOf(horas).replace(".", ",");
        if (resultado.endsWith(",0")) { 
            return resultado.substring(0, resultado.length()-2);
        } else if (resultado.endsWith(",00")) {
            return resultado.substring(0, resultado.length()-3);
        }
        return resultado;
    }

    public void lancaCartao (String emp, String data, String horas)
            throws EmpregadoNaoExisteException, DataInvalidaException {
        Empregado e = getEmpregadoPorId(emp);
        if (!(e instanceof Horista)) {
            throw new TipoEmpregadoException("Empregado nao eh horista.");
        }
        Horista h = (Horista) e;
        LocalDate d = stringToDate(data, "data");
        double hrs = Double.parseDouble(horas.replace(",","."));
        if (hrs <= 0) {
            throw new HoraNulaException();
        }
        h.setHoras(d, hrs);
    }

    public String getHorasNormaisTrabalhadas (String emp, String dataInicial, String dataFinal)
            throws EmpregadoNaoExisteException, DataInvalidaException {
        Empregado e = getEmpregadoPorId(emp);
        if (!(e instanceof Horista)) {
            throw new TipoEmpregadoException("Empregado nao eh horista.");
        }
        LocalDate di = stringToDate(dataInicial, "inicial");
        LocalDate df = stringToDate(dataFinal, "final");
        if (di.isAfter(df)) {
            throw new OrdemException();
        }
        double horas = 0;
        Horista h = (Horista) e;
        for (LocalDate date = di; date.isBefore(df); date = date.plusDays(1)) {
            horas += h.getHorasNormais(date);
        }
        return formatarHoras(horas);
    }

    public String getHorasExtrasTrabalhadas (String emp, String dataInicial, String dataFinal) 
            throws EmpregadoNaoExisteException, DataInvalidaException {
        Empregado e = getEmpregadoPorId(emp);
        if (!(e instanceof Horista)) {
            throw new TipoEmpregadoException("Empregado nao eh horista.");
        }
        LocalDate di = stringToDate(dataInicial, "inicial");
        LocalDate df = stringToDate(dataFinal, "final");
        if (di.isAfter(df)) {
            throw new OrdemException();
        }
        double horas = 0;
        Horista h = (Horista) e;
        for (LocalDate date = di; date.isBefore(df); date = date.plusDays(1)) {
            horas += h.getHorasExtras(date);
        }
        return formatarHoras(horas);
    }

    public void lancaVenda (String emp, String data, String valor)
            throws EmpregadoNaoExisteException, DataInvalidaException {
        Empregado e = getEmpregadoPorId(emp);
        if (!(e instanceof Comissionado)) {
            throw new TipoEmpregadoException("Empregado nao eh comissionado.");
        }
        Comissionado c = (Comissionado) e;
        LocalDate d = stringToDate(data, "data");
        double v = Double.parseDouble(valor.replace(",", "."));
        if (v <= 0) throw new ValorNaoNuloException();
        c.setVendas(d, v);
    }

    public String getVendasRealizadas (String emp, String dataInicial, String dataFinal) 
            throws EmpregadoNaoExisteException, DataInvalidaException {
        Empregado e = getEmpregadoPorId(emp);
        if (!(e instanceof Comissionado)) {
            throw new TipoEmpregadoException("Empregado nao eh comissionado.");
        }
        Comissionado c = (Comissionado) e;
        LocalDate di = stringToDate(dataInicial, "inicial");
        LocalDate df = stringToDate(dataFinal, "final");
        if (di.isAfter(df)) {
            throw new OrdemException();
        }
        double totalVendas = 0;
        for (LocalDate date = di; date.isBefore(df); date = date.plusDays(1)) {
            totalVendas += c.getVendas(date);
        }
        return converteSalario(totalVendas);
    }

    private Empregado mudaTipo (Empregado e, String novoTipo) {
        if (!(novoTipo.equals("comissionado") ||
                novoTipo.equals("horista") ||
                novoTipo.equals("assalariado"))) {
            throw new TipoAtributoException("Tipo invalido.");
        }
        if (novoTipo.equals("assalariado")) {
            Assalariado a = new Assalariado(
                e.getNome(), 
                e.getEndereco(), 
                "assalariado", 
                converteSalario(e.getSalario())
            );
            a.setId(e.getId());
            return a;
        } else if (novoTipo.equals("horista")) {
            Horista h = new Horista(
                e.getNome(), 
                e.getEndereco(), 
                "horista", 
                converteSalario(e.getSalario())
            );
            System.out.println(h.getNome() + "mudou");
            h.setId(e.getId());
            return h;
        } else if (novoTipo.equals("comissionado")) {
            Comissionado c = new Comissionado(
                e.getNome(), 
                e.getEndereco(), 
                "comissionado", 
                converteSalario(e.getSalario()),
                "0,0"
            );
            c.setId(e.getId());
            return c;
        }
        throw new TipoAtributoException("Tipo invalido.");
    }

    public void alteraEmpregado (String emp, String atributo, String valor)
            throws EmpregadoNaoExisteException {
        Empregado e = getEmpregadoPorId(emp);
        if (atributo.equals("nome")) {
            if (valor == null || valor.isEmpty()) {
                throw new AtributoNaoNulo("Nome");
            }
            e.setNome(valor);
        } else if (atributo.equals("endereco")) {
            if (valor == null || valor.isEmpty()) {
                throw new AtributoNaoNulo("Endereco");
            }
            e.setEndereco(valor);
        } else if (atributo.equals("sindicalizado")) {
            if (!(valor.equals("true") || valor.equals("false"))) {
                throw new SindicatoException("Valor deve ser true ou false.");
            }
            boolean v = Boolean.parseBoolean(valor);
            e.setSindicalizado(v, null, 0);
        } else if (atributo.equals("comissao")) {
            if (valor == null || valor.isEmpty()) {
                throw new ComissaoException("Comissao nao pode ser nula.");
            }
            try {
                double v = Double.parseDouble(valor.replace(",", "."));
                if (v < 0) {
                    throw new ComissaoException("Comissao deve ser nao-negativa.");
                }
                if (!(e instanceof Comissionado)) {
                    throw new TipoEmpregadoException("Empregado nao eh comissionado.");
                }
                Comissionado c = (Comissionado) e;
                c.setComissao(v);
            } catch (NumberFormatException ex) {
                throw new ComissaoException("Comissao deve ser numerica.");
            }
        } else if (atributo.equals("salario")) {
            if (valor == null || valor.isEmpty()) {
                throw new SalarioException("Salario nao pode ser nulo.");
            }
            try {
                double sal = Double.parseDouble(valor.replace(",", "."));
                if (sal < 0) {
                    throw new SalarioException("Salario deve ser nao-negativo.");
                }
                e.setSalario(sal);
            } catch (NumberFormatException ex) {
                throw new SalarioException("Salario deve ser numerico.");
            } 
        } else if (atributo.equals("tipo")) {
            Empregado novo = mudaTipo(e, valor);
            novo.setId(e.getId());
            empregados.remove(e);
            empregados.add(novo);
        } else if (atributo.equals("metodoPagamento")) {
            if (!(valor.equals("emMaos") ||
                valor.equals("dinheiro") ||
                valor.equals("banco") ||
                valor.equals("correios"))) {
                    throw new TipoAtributoException("Metodo de pagamento invalido.");
            }
            e.setMetodoPagamento(valor, e.getBanco(), 
                e.getAgencia(), e.getContaCorrente());
        } else {
            throw new TipoAtributoException("Atributo nao existe.");
        }
    }

    public void alteraEmpregado (String emp, String atributo, String valor, 
    String comissao) 
        throws EmpregadoNaoExisteException {
            Empregado e = getEmpregadoPorId(emp);
            if (atributo.equals("tipo")) {
                if (valor.equals("comissionado")) {
                    Comissionado c = new Comissionado(e.getNome(), e.getEndereco(), 
                    "comissionado", converteSalario(e.getSalario()), comissao);
                    c.setId(e.getId());
                    empregados.remove(e);
                    empregados.add(c);
                } else if (valor.equals("horista")) {
                    Horista h = new Horista  (
                        e.getNome(),
                        e.getEndereco(),
                        "horista",
                        comissao
                    );
                    h.setId(e.getId());
                    empregados.remove(e);
                    empregados.add(h);
                }
            }
        }

    public void alteraEmpregado (String emp, String atributo, String valor, 
    String idSindicato, String taxaSindical) 
    throws EmpregadoNaoExisteException {
        Empregado e = getEmpregadoPorId(emp);
        if (atributo.equals("sindicalizado")) {
            for (String empregadoId : dadosSindicais.keySet()) {
                String sindicatoId = dadosSindicais.get(empregadoId);
                if (idSindicato.equals(sindicatoId) && !empregadoId.equals(emp)) {
                    throw new IdIgualException();
                }
            }
            boolean v = Boolean.parseBoolean(valor);
            if (taxaSindical == null || taxaSindical.isEmpty()) {
                throw new ComissaoException("Taxa sindical nao pode ser nula.");
            }
            if (idSindicato == null || idSindicato.isEmpty()) {
                throw new ComissaoException("Identificacao do sindicato nao pode ser nula.");
            }
            try {
                double t = Double.parseDouble(taxaSindical.replace(",", "."));
                if (t < 0) throw new ComissaoException("Taxa sindical deve ser nao-negativa.");
                e.setSindicalizado(v, idSindicato, t);
                dadosSindicais.put(String.valueOf(e.getId()), e.getIdSindicato());
            } catch (NumberFormatException ex) {
                throw new ComissaoException("Taxa sindical deve ser numerica.");
            }
            
        }
    }

    public void alteraEmpregado (String emp, String atributo, String valor1, 
    String banco, String agencia, String contaCorrente) 
        throws EmpregadoNaoExisteException {
            Empregado e = getEmpregadoPorId(emp);
            if (atributo.equals("metodoPagamento")) {
                if (valor1.equals("banco")) {
                    if (banco == null || banco.isEmpty()) {
                        throw new TipoBancoException("Banco nao pode ser nulo.");
                    }
                    if (agencia == null || agencia.isEmpty()) {
                        throw new TipoBancoException("Agencia nao pode ser nulo.");
                    }
                    if (contaCorrente == null || contaCorrente.isEmpty()) {
                        throw new TipoBancoException("Conta corrente nao pode ser nulo.");
                    }
                    e.setMetodoPagamento(valor1, banco, agencia, contaCorrente);
                } 
            } 
        }

    private Empregado getEmpSindicato (String idSindicato)
        throws EmpregadoNaoExisteException {
            if (idSindicato == null) {
                throw new NaoSindicalizadoException();
            }
            for (String emp : dadosSindicais.keySet()) {
                String id = dadosSindicais.get(emp);
                if (idSindicato.equals(id)) {
                    return getEmpregadoPorId(emp);
                }
            }
            throw new NaoSindicalizadoException();
    }

    public void validarMembro (String membro) {
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

    public void lancaTaxaServico (String membro, String data, String valor)
        throws EmpregadoNaoExisteException, DataInvalidaException, 
        MembroNaoExisteException, IdNaoNuloException {
            validarMembro(membro);
            Empregado e = getEmpSindicato(membro);
            LocalDate d = stringToDate(data, "data");
            Double v = Double.parseDouble(valor.replace(",", "."));
            if (v <= 0) throw new ValorNaoNuloException();
            e.setTaxaDia(d, v);
    }

    public String getTaxasServico (String emp, String dataInicial, String dataFinal)
        throws EmpregadoNaoExisteException, DataInvalidaException {
            Empregado e = getEmpregadoPorId(emp);
            if (!e.getSindicalizado()) {
                throw new NaoSindicalizadoException();
            }
            LocalDate di = stringToDate(dataInicial, "inicial");
            LocalDate df = stringToDate(dataFinal, "final");
            if (di.isAfter(df)) {
                throw new OrdemException();
            }
            double total = 0;
            for (LocalDate date = di; date.isBefore(df); date = date.plusDays(1)) {
                total += e.getTaxaDia(date);
            }
            return converteSalario(total);
    }

    // public String totalFolha (String data) {

    // }

    public void encerrarSistema () {
        try {
            FileOutputStream f = new FileOutputStream("empregados.xml");
            XMLEncoder encoder = new XMLEncoder(f);
            encoder.writeObject(empregados);
            encoder.close();
            f.close();
        } catch (Exception e) {
            
        }
    }

}
