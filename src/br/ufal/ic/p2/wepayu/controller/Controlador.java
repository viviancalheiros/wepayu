package br.ufal.ic.p2.wepayu.controller;

import java.beans.XMLEncoder;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.beans.XMLDecoder;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;
import java.util.Map;
import java.util.TreeMap;
import java.util.List;

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

public class Controlador implements Serializable {
    Scanner s = new Scanner(System.in);
    ArrayList<Empregado> empregados = new ArrayList<>();
    Map<String, String> dadosSindicais = new TreeMap<>(); //id, idSindical
    Map<String, List<String>> folha = new TreeMap<>(); //data, id
    Map<String, Map<String, String>> folhaPorTipo = new TreeMap<>(); //data -> (tipo, total)

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
        if (h.getDataInicioD() == null) h.setDataInicio(d);
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

    public String totalFolha (String data)
        throws DataInvalidaException {
        double total = 0, totalAs = 0, totalCom = 0, totalHr = 0;
        LocalDate d = stringToDate(data, "data");
        for (Empregado e : empregados) {
            boolean recebeu = false;
            if (e instanceof Assalariado) {
                Assalariado a = (Assalariado) e;
                if (a.recebeHoje(d)) {
                    recebeu = true;
                    totalAs += a.getSalario();
                }
            } else if (e instanceof Comissionado) {
                Comissionado c = (Comissionado) e;
                if (c.recebeHoje(d)) {
                    recebeu = true;
                    totalCom += c.getSalario(d);
                }
            } else if (e instanceof Horista) {
                Horista h = (Horista) e;
                if (h.recebeHoje(d)) {
                    recebeu = true;
                    totalHr += h.getSalarioBruto(d);
                }
            }
            if (recebeu) {
                String id = String.valueOf(e.getId());
                folha.computeIfAbsent(data, k -> new ArrayList<>()).add(id);
            }
        }

        total = totalAs + totalCom + totalHr;

        Map<String, String> porTipo = new TreeMap<>();
        porTipo.put("assalariado", converteSalario(totalAs));
        porTipo.put("comissionado", converteSalario(totalCom));
        porTipo.put("horista", converteSalario(totalHr));
        folhaPorTipo.put(data, porTipo);

        double arredondado = Math.round(total * 100.0)/100.0;
        String v = String.format("%.2f", arredondado).replace(".", ",");
        return v;
    }

    public void rodaFolha (String data, String saida)
            throws IOException, DataInvalidaException, 
            EmpregadoNaoExisteException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(saida))) {
            double total = 0;
            LocalDate d = stringToDate(data, "data");
            writer.write("FOLHA DE PAGAMENTO DO DIA " + d);
            writer.newLine();
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
            
            String strHn, strHx, strBruto, strLiquido, strDescontos;
            double totalBruto = 0, totalDescontos = 0, totalLiquido = 0;
            double hnTotal = 0, hxTotal = 0;
            List<String> idsDoDia = folha.get(data);

            //HORISTAS
            if (idsDoDia != null) {
                List<Horista> horistasDoDia = new ArrayList<>();
                for (String ids : idsDoDia) {
                    Empregado e = getEmpregadoPorId(ids);
                    if (e instanceof Horista) {
                        horistasDoDia.add((Horista) e);
                    }
                }

                horistasDoDia.sort(Comparator.comparing(Horista::getNome));

                for (Horista h : horistasDoDia) {
                    double bruto = h.getSalarioBruto(d);
                    double liquido = h.getSalarioLiquido(d);
                    double descontos = h.getDescontos(d, bruto);
                    double hnSemanal = h.getHnSemanal(d.minusDays(6), d);
                    double hxSemanal = h.getHxSemanal(d.minusDays(6), d);
                    String metodo;

                    if (bruto <= 0) {
                        hnSemanal = 0;
                        hxSemanal = 0;
                    }

                    if (h.getMetodoPagamento().equals("emMaos")) {
                        metodo = "Em maos";
                    } else if (h.getMetodoPagamento().equals("banco")) {
                        metodo = h.getBanco() + 
                                ", Ag. " + h.getAgencia() + 
                                " CC " + h.getContaCorrente();
                    } else if (h.getMetodoPagamento().equals("correios")) {
                        metodo = "Correios, " + h.getEndereco(); 
                    } else {
                        metodo = h.getMetodoPagamento();
                    }

                    hnTotal += hnSemanal;
                    hxTotal += hxSemanal;
                    totalBruto += bruto;
                    totalDescontos += descontos;
                    totalLiquido += liquido;

                    strBruto = converteSalario(bruto);
                    strLiquido = converteSalario(liquido);
                    strDescontos = converteSalario(descontos);
                    strHn = String.format("%.0f", hnSemanal);
                    strHx = String.format("%.0f", hxSemanal);

                    writer.write(String.format(
                        "%-36s %5s %5s %13s %9s %15s %-38s",
                        h.getNome(), 
                        strHn,
                        strHx,
                        strBruto,
                        strDescontos,
                        strLiquido,
                        metodo
                    ));
                    writer.newLine();

                    if (bruto > 0) h.setUltimoPagamento(d);
                }

                strBruto = converteSalario(totalBruto);
                strLiquido = converteSalario(totalLiquido);
                strDescontos = converteSalario(totalDescontos);
                strHn = String.format("%.0f", hnTotal);
                strHx = String.format("%.0f", hxTotal);

                total += totalBruto;

                writer.newLine();
                writer.write(String.format(
                    "%-36s %5s %5s %13s %9s %15s", 
                    "TOTAL HORISTAS",
                    strHn,
                    strHx,
                    strBruto,
                    strDescontos,
                    strLiquido
                    ));
                
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
            
            //ASSALARIADOS
            if (idsDoDia != null) {
                totalBruto = 0;
                totalDescontos = 0;
                totalLiquido = 0;

                List<Assalariado> AssalariadosDoDia = new ArrayList<>();
                for (String ids : idsDoDia) {
                    Empregado e = getEmpregadoPorId(ids);
                    if (e instanceof Assalariado) {
                        AssalariadosDoDia.add((Assalariado) e);
                    }
                }
                AssalariadosDoDia.sort(Comparator.comparing(Assalariado::getNome));

                for (Assalariado a : AssalariadosDoDia) {

                    double bruto = a.getSalario();
                    double liquido = a.getSalarioLiquido(d);
                    double descontos = a.getDescontos(d);
                    String metodo;

                    if (a.getMetodoPagamento().equals("emMaos")) {
                        metodo = "Em maos";
                    } else if (a.getMetodoPagamento().equals("banco")) {
                        metodo = a.getBanco() + 
                                ", Ag. " + a.getAgencia() + 
                                " CC " + a.getContaCorrente();
                    } else if (a.getMetodoPagamento().equals("correios")) {
                        metodo = "Correios, " + a.getEndereco();  
                    } else {
                        metodo = a.getMetodoPagamento();
                    }

                    a.setUltimoPagamento(d);

                    totalBruto += bruto;
                    totalDescontos += descontos;
                    totalLiquido += liquido;

                    strBruto = converteSalario(bruto);
                    strLiquido = converteSalario(liquido);
                    strDescontos = converteSalario(descontos);

                    writer.write(String.format(
                        "%-48s %13s %9s %15s %-38s",
                        a.getNome(), 
                        strBruto,
                        strDescontos,
                        strLiquido,
                        metodo
                    ));
                    writer.newLine();
                }

                strBruto = converteSalario(totalBruto);
                strLiquido = converteSalario(totalLiquido);
                strDescontos = converteSalario(totalDescontos);

                total += totalBruto;

                writer.newLine();
                writer.write(String.format(
                    "%-48s %13s %9s %15s", 
                    "TOTAL ASSALARIADOS",
                    strBruto,
                    strDescontos,
                    strLiquido
                    ));
                }
            }
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
        
            //COMISSIONADOS
            if (idsDoDia != null) {
                totalBruto = 0;
                totalDescontos = 0;
                totalLiquido = 0;
                double totalFixo = 0, totalVendas = 0, totalComissao = 0;
                List<Comissionado> comissionadosDoDia = new ArrayList<>();
                for (String ids : idsDoDia) {
                    Empregado e = getEmpregadoPorId(ids);
                    if (e instanceof Comissionado) {
                        comissionadosDoDia.add((Comissionado) e);
                    }
                }

                comissionadosDoDia.sort(Comparator.comparing(Comissionado::getNome));

                for (Comissionado c : comissionadosDoDia) {
                    double fixo = c.getFixo(d);
                    double liquido = c.getSalarioLiquido(d);
                    double descontos = c.getDescontos(d);
                    double vendas = c.getTotalVendas(d);
                    double comissao = c.getComissaoTotal(d);
                    double bruto = c.getSalario(d);
                    String metodo;


                    if (c.getMetodoPagamento().equals("emMaos")) {
                        metodo = "Em maos";
                    } else if (c.getMetodoPagamento().equals("banco")) {
                        metodo = c.getBanco() + 
                                ", Ag. " + c.getAgencia() + 
                                " CC " + c.getContaCorrente();
                    } else if (c.getMetodoPagamento().equals("correios")) {
                        metodo = "Correios, " + c.getEndereco(); 
                    } else {
                        metodo = c.getMetodoPagamento();
                    }

                    totalBruto += bruto;
                    totalDescontos += descontos;
                    totalLiquido += liquido;
                    totalFixo += fixo;
                    totalVendas += vendas;
                    totalComissao += comissao;

                    String strFixo = converteSalario(fixo);
                    strLiquido = converteSalario(liquido);
                    strDescontos = converteSalario(descontos);
                    String strComissao = converteSalario(comissao);
                    String strVendas = converteSalario(vendas);
                    strBruto = converteSalario(bruto);

                    writer.write(String.format(
                        "%-21s %8s %8s %8s %13s %9s %15s %-38s",
                        c.getNome(), 
                        strFixo,
                        strVendas,
                        strComissao,                       
                        strBruto,
                        strDescontos,
                        strLiquido,
                        metodo
                    ));
                    writer.newLine();

                    c.setUltimoPagamento(d);
                }

                strBruto = converteSalario(totalBruto);
                strLiquido = converteSalario(totalLiquido);
                strDescontos = converteSalario(totalDescontos);
                String strFixo = converteSalario(totalFixo);
                String strVendas = converteSalario(totalVendas);
                String strComissao = converteSalario(totalComissao);

                total += totalBruto;

                writer.newLine();
                writer.write(String.format(
                    "%-21s %8s %8s %8s %13s %9s %15s", 
                    "TOTAL COMISSIONADOS",
                    strFixo,
                    strVendas,
                    strComissao,
                    strBruto,
                    strDescontos,
                    strLiquido
                    ));
                writer.newLine();
                writer.newLine();
                String strTotal = converteSalario(total);
                writer.write("TOTAL FOLHA: " + strTotal);
            }
        }
    }

    public void encerrarSistema () {
        try {
            FileOutputStream f = new FileOutputStream("empregados.xml");
            XMLEncoder encoder = new XMLEncoder(f);
            encoder.writeObject(empregados);
            encoder.close();
            f.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
