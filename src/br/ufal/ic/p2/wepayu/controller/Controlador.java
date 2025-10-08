package br.ufal.ic.p2.wepayu.controller;

import java.beans.XMLEncoder;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.beans.XMLDecoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;
import java.util.Map;
import java.util.TreeMap;
import java.util.List;

import br.ufal.ic.p2.wepayu.Exception.Atributo.*;
import br.ufal.ic.p2.wepayu.Exception.Data.*;
import br.ufal.ic.p2.wepayu.Exception.Empregado.*;
import br.ufal.ic.p2.wepayu.Exception.Venda.*;
import br.ufal.ic.p2.wepayu.Exception.Sindicato.*;
import br.ufal.ic.p2.wepayu.models.*;
import br.ufal.ic.p2.wepayu.services.*;
import br.ufal.ic.p2.wepayu.utils.*;

public class Controlador implements Serializable {
    Scanner s = new Scanner(System.in);
    ArrayList<Empregado> empregados = new ArrayList<>();
    Map<String, String> dadosSindicais = new TreeMap<>(); //id, idSindical

    Map<String, List<String>> folha = new TreeMap<>(); //data, id
    Map<String, Map<String, String>> folhaPorTipo = new TreeMap<>(); //data -> (tipo, total)

    ArrayList<String> tiposAgenda = new ArrayList<>(Arrays.asList("mensal $", "semanal 5", "semanal 2 5"));
    
    private boolean status = false;
    Historico historico = new Historico();

    public void iniciarSistema () {
        try {
            FileInputStream f = new FileInputStream("empregados.xml");
            XMLDecoder decoder = new XMLDecoder(f);
            this.empregados = (ArrayList<Empregado>) decoder.readObject();
            decoder.close();
            f.close();
            this.status = true;
        } catch (Exception e) {
            this.empregados = new ArrayList<>();
        }
    }

    public void zerarSistema () {
        historico.salvarEstado(empregados, dadosSindicais);
        empregados.clear();
        dadosSindicais.clear();
    }

    public String criarEmpregado (String nome, String endereco, String tipo, String salario)
        throws SalarioException, AtributoNaoNulo {
        
        ValidacaoUtils.verificarSalario(salario);

        Empregado e = EmpregadoService.criarEmpregado(nome, endereco, tipo, salario);

        historico.salvarEstado(empregados, dadosSindicais);

        empregados.add(e);
        return String.valueOf(e.getId());
    }

    public String criarEmpregado (String nome, String endereco, String tipo, String salario, 
            String comissao) throws SalarioException, ComissaoException {
        
        ValidacaoUtils.verificarSalario(salario);
        Empregado e = EmpregadoService.criarEmpregado(nome, endereco, tipo, salario, comissao);
        historico.salvarEstado(this.empregados, this.dadosSindicais);
        empregados.add(e);
        return String.valueOf(e.getId());
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

    public String getAtributoEmpregado (String emp, String atributo)
            throws EmpregadoNaoExisteException, NaoSindicalizadoException {
        Empregado e = EmpregadoService.getEmpregadoPorId(emp, empregados);
        return EmpregadoService.getAtributoEmpregado(
            e, atributo, this.dadosSindicais, this.empregados
        );
    }

    public void removerEmpregado (String emp) throws EmpregadoNaoExisteException {
        Empregado e = EmpregadoService.getEmpregadoPorId(emp, empregados);
        historico.salvarEstado(empregados, dadosSindicais);
        empregados.remove(e);
    }

    public void lancaCartao (String emp, String data, String horas)
            throws EmpregadoNaoExisteException, DataInvalidaException {
        Empregado e = EmpregadoService.getEmpregadoPorId(emp, empregados);
        historico.salvarEstado(empregados, dadosSindicais);
        HoristaService.lancaCartao(e, data, horas);
    }

    public String getHorasNormaisTrabalhadas (String emp, String dataInicial, String dataFinal)
            throws EmpregadoNaoExisteException, DataInvalidaException {
        Empregado e = EmpregadoService.getEmpregadoPorId(emp, empregados);
        return HoristaService.getHorasNormaisTrabalhadas(e, dataInicial, dataFinal);
    }

    public String getHorasExtrasTrabalhadas (String emp, String dataInicial, String dataFinal) 
            throws EmpregadoNaoExisteException, DataInvalidaException {
        Empregado e = EmpregadoService.getEmpregadoPorId(emp, empregados);
        return HoristaService.getHorasExtrasTrabalhadas(e, dataInicial, dataFinal);
    }

    public void lancaVenda (String emp, String data, String valor)
            throws EmpregadoNaoExisteException, DataInvalidaException {
        Empregado e = EmpregadoService.getEmpregadoPorId(emp, empregados);
        if (!(e instanceof Comissionado)) {
            throw new TipoEmpregadoException("Empregado nao eh comissionado.");
        }
        Comissionado c = (Comissionado) e;
        LocalDate d = ConversorUtils.stringToDate(data, "data");
        double v = Double.parseDouble(valor.replace(",", "."));
        if (v <= 0) throw new ValorNaoNuloException();
        historico.salvarEstado(empregados, dadosSindicais);
        c.setVendas(d, v);
    }

    public String getVendasRealizadas (String emp, String dataInicial, String dataFinal) 
            throws EmpregadoNaoExisteException, DataInvalidaException {
        Empregado e = EmpregadoService.getEmpregadoPorId(emp, empregados);
        return ComissionadoService.getVendasRealizadas(e, dataInicial, dataFinal);
    }

    public void alteraEmpregado (String emp, String atributo, String valor)
            throws EmpregadoNaoExisteException {
            
        Empregado e = EmpregadoService.getEmpregadoPorId(emp, empregados);
        historico.salvarEstado(this.empregados, this.dadosSindicais);
        Empregado novo = EmpregadoService.alteraEmpregado(emp, atributo, valor, e, empregados);
        if (atributo.equals("tipo")) {
            empregados.remove(e);
            empregados.add(novo);
        } else if (atributo.equals("agendaPagamento")) {
            EmpregadoService.mudaFolhaEmpregado(e, valor, folha);
        }
        e = novo;
    }

    public void alteraEmpregado (String emp, String atributo, String valor, 
    String comissao) 
        throws EmpregadoNaoExisteException {
            Empregado e = EmpregadoService.getEmpregadoPorId(emp, empregados);
            historico.salvarEstado(this.empregados, this.dadosSindicais);
            if (atributo.equals("tipo")) {
                if (valor.equals("comissionado")) {
                    Comissionado c = (Comissionado) EmpregadoService.alteraEmpregado(
                        emp, atributo, valor, comissao, e
                    );
                    empregados.remove(e);
                    empregados.add(c);
                } else if (valor.equals("horista")) {
                    Horista h = (Horista) EmpregadoService.alteraEmpregado(
                        emp, atributo, valor, comissao, e
                    );
                    empregados.remove(e);
                    empregados.add(h);
                }
            }
        }

    public void alteraEmpregado (String emp, String atributo, String valor, 
    String idSindicato, String taxaSindical) 
    throws EmpregadoNaoExisteException {
        Empregado e = EmpregadoService.getEmpregadoPorId(emp, empregados);
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
                historico.salvarEstado(empregados, dadosSindicais);
                e.setSindicalizado(v, idSindicato, t);
                if (v) {
                    dadosSindicais.put(String.valueOf(e.getId()), e.getIdSindicato());
                } else {
                    dadosSindicais.remove(String.valueOf(e.getId()));
                }
            } catch (NumberFormatException ex) {
                throw new ComissaoException("Taxa sindical deve ser numerica.");
            }
        }
    }

    public void alteraEmpregado (String emp, String atributo, String valor1, 
    String banco, String agencia, String contaCorrente) 
        throws EmpregadoNaoExisteException {
            Empregado e = EmpregadoService.getEmpregadoPorId(emp, empregados);
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
                    historico.salvarEstado(empregados, dadosSindicais);
                    e.setMetodoPagamento(valor1, banco, agencia, contaCorrente);
                } 
            } 
        }

    public void lancaTaxaServico (String membro, String data, String valor)
        throws EmpregadoNaoExisteException, DataInvalidaException, 
        MembroNaoExisteException {
            SindicatoService.validarMembro(membro);
            Empregado e = SindicatoService.getEmpSindicato(membro, this.dadosSindicais, this.empregados);
            historico.salvarEstado(empregados, dadosSindicais);
            SindicatoService.lancaTaxaServico(e, data, valor, this.dadosSindicais, this.empregados);
        }

    public String getTaxasServico (String emp, String dataInicial, String dataFinal)
        throws EmpregadoNaoExisteException, DataInvalidaException {
            Empregado e = EmpregadoService.getEmpregadoPorId(emp, empregados);
            return SindicatoService.getTaxasServico(e, dataInicial, dataFinal);
    }

    public String totalFolha (String data)
        throws DataInvalidaException {
        double total = 0, totalAs = 0, totalCom = 0, totalHr = 0;
        LocalDate d = ConversorUtils.stringToDate(data, "data");
        for (Empregado e : empregados) {
            boolean recebeu = false;
            if (e instanceof Assalariado) {
                Assalariado a = (Assalariado) e;
                if (EmpregadoService.recebeHoje(d, e)) {
                    recebeu = true;
                    totalAs += a.getSalario();
                }
            } else if (e instanceof Comissionado) {
                Comissionado c = (Comissionado) e;
                if (EmpregadoService.recebeHoje(d, e)) {
                    recebeu = true;
                    totalCom += c.getSalario(d);
                }
            } else if (e instanceof Horista) {
                Horista h = (Horista) e;
                if (EmpregadoService.recebeHoje(d, e)) {
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
        porTipo.put("assalariado", ConversorUtils.converteSalario(totalAs));
        porTipo.put("comissionado", ConversorUtils.converteSalario(totalCom));
        porTipo.put("horista", ConversorUtils.converteSalario(totalHr));
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
            LocalDate d = ConversorUtils.stringToDate(data, "data");
            historico.salvarEstado(empregados, dadosSindicais);
            
            writer.write("FOLHA DE PAGAMENTO DO DIA " + d);
            writer.newLine();
            FolhaUtils.printaHoristas(writer, d);

            double totalBruto = 0, totalDescontos = 0, totalLiquido = 0;
            double hnTotal = 0, hxTotal = 0;
            List<String> idsDoDia = folha.get(data);

            //HORISTAS
            if (idsDoDia != null) {
                List<Horista> horistasDoDia = new ArrayList<>();
                for (String ids : idsDoDia) {
                    Empregado e = EmpregadoService.getEmpregadoPorId(ids, empregados);
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
                    String metodo = FolhaUtils.formataMetodoPagamento(h);

                    if (bruto <= 0) {
                        hnSemanal = 0;
                        hxSemanal = 0;
                    }

                    FolhaUtils.printaValorHoristas(
                        h.getNome(), 
                        hnSemanal, 
                        hxSemanal, 
                        bruto, 
                        descontos, 
                        liquido, 
                        metodo, 
                        writer
                    );

                    hnTotal += hnSemanal;
                    hxTotal += hxSemanal;
                    totalBruto += bruto;
                    totalDescontos += descontos;
                    totalLiquido += liquido;
                    
                    if (bruto > 0) h.setUltimoPagamento(d);
                }

                total += totalBruto;

                FolhaUtils.printaTotalHoristas(
                    hnTotal, 
                    hxTotal, 
                    totalBruto, 
                    totalDescontos, 
                    totalLiquido, 
                    writer
                );
                
                FolhaUtils.printaAssalariados(writer, d);
            
            //ASSALARIADOS
            if (idsDoDia != null) {
                totalBruto = 0;
                totalDescontos = 0;
                totalLiquido = 0;

                List<Assalariado> AssalariadosDoDia = new ArrayList<>();
                for (String ids : idsDoDia) {
                    Empregado e = EmpregadoService.getEmpregadoPorId(ids, empregados);
                    if (e instanceof Assalariado) {
                        AssalariadosDoDia.add((Assalariado) e);
                    }
                }
                AssalariadosDoDia.sort(Comparator.comparing(Assalariado::getNome));

                for (Assalariado a : AssalariadosDoDia) {

                    double bruto = a.getSalario();
                    double liquido = a.getSalarioLiquido(d);
                    double descontos = a.getDescontos(d);
                    String metodo = FolhaUtils.formataMetodoPagamento(a);

                    a.setUltimoPagamento(d);

                    totalBruto += bruto;
                    totalDescontos += descontos;
                    totalLiquido += liquido;

                    FolhaUtils.printaValorAssalariados(
                        a.getNome(), 
                        bruto, 
                        liquido, 
                        descontos,
                        metodo, 
                        writer
                    );
                }

                total += totalBruto;

                FolhaUtils.printaTotalAssalariados(
                    totalBruto, 
                    totalDescontos, 
                    totalLiquido, 
                    writer
                );
            }
            
            FolhaUtils.printaComissionados(writer, d);

            //COMISSIONADOS
            if (idsDoDia != null) {
                totalBruto = 0;
                totalDescontos = 0;
                totalLiquido = 0;
                double totalFixo = 0, totalVendas = 0, totalComissao = 0;
                List<Comissionado> comissionadosDoDia = new ArrayList<>();
                for (String ids : idsDoDia) {
                    Empregado e = EmpregadoService.getEmpregadoPorId(ids, empregados);
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
                    String metodo = FolhaUtils.formataMetodoPagamento(c);

                    totalBruto += bruto;
                    totalDescontos += descontos;
                    totalLiquido += liquido;
                    totalFixo += fixo;
                    totalVendas += vendas;
                    totalComissao += comissao;

                    FolhaUtils.printaValorComissionados(
                        c.getNome(), 
                        fixo, 
                        vendas, 
                        comissao,
                        bruto, 
                        descontos, 
                        liquido, 
                        metodo,
                        writer
                    );

                    c.setUltimoPagamento(d);
                }

                total += totalBruto;

                FolhaUtils.printaTotalComissionados(
                    totalFixo, 
                    totalVendas, 
                    totalComissao,
                    totalBruto, 
                    totalDescontos, 
                    totalLiquido, 
                    writer
                );
                
                String strTotal = ConversorUtils.converteSalario(total);
                writer.write("TOTAL FOLHA: " + strTotal);
            }
            }
        }
    }

    public String getNumeroDeEmpregados () {
        int qtd = empregados.size();
        return String.valueOf(qtd);
    }

    private void restaurarEstado (Historico.Memento memento) {
        this.empregados.clear();
        this.dadosSindicais.clear();
        this.empregados.addAll(memento.getEmpregados());
        this.dadosSindicais.putAll(memento.getDadosSindicais());
    }

    public void undo () {
        if (!status) {
            throw new RuntimeException("Nao pode dar comandos depois de encerrarSistema.");
        }
        try {
            Historico.Memento estadoAtual = new Historico.Memento(
                new ArrayList<>(this.empregados), 
                new TreeMap<>(this.dadosSindicais)
            );
            Historico.Memento estadoAnterior = historico.undo(estadoAtual);
            restaurarEstado(estadoAnterior);
        } catch (IllegalStateException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void redo () {
        if (!status) {
            throw new RuntimeException("Nao pode dar comandos depois de encerrarSistema.");
        }
        try {
            Historico.Memento estadoAtual = new Historico.Memento(
                new ArrayList<>(this.empregados), 
                new TreeMap<>(this.dadosSindicais)
            );
            Historico.Memento estadoFuturo = historico.redo(estadoAtual);
            restaurarEstado(estadoFuturo);
        } catch (IllegalStateException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void criarAgendaDePagamentos (String descricao) {
        AgendaUtils.verificaAgenda(descricao, tiposAgenda);
        
    }


    public void encerrarSistema () {
        try {
            FileOutputStream f = new FileOutputStream("empregados.xml");
            XMLEncoder encoder = new XMLEncoder(f);
            encoder.writeObject(empregados);
            encoder.close();
            f.close();
            this.status = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
