package br.ufal.ic.p2.wepayu.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import br.ufal.ic.p2.wepayu.models.Empregado;

public class Historico implements Serializable {
    private Stack<Memento> undo = new Stack<>();
    private Stack<Memento> redo = new Stack<>();

    public static class Memento implements Serializable {
        private final ArrayList<Empregado> empregados;
        private final Map<String, String> dadosSindicais;

        public Memento(ArrayList<Empregado> empregados, Map<String, String> dadosSindicais) {
            this.empregados = new ArrayList<>();
            for (Empregado original : empregados) {
                try {
                    Empregado clone = (Empregado) original.clone();
                    this.empregados.add(clone);
                } catch (CloneNotSupportedException e) {
                    throw new RuntimeException("Falha na clonagem.", e);
                }
            }
            this.dadosSindicais = (new TreeMap<>(dadosSindicais));
        }

        public ArrayList<Empregado> getEmpregados () {
            return empregados;
        }

        public Map<String, String> getDadosSindicais () {
            return dadosSindicais;
        }
    }

    public void salvarEstado (ArrayList<Empregado> empregados, Map<String, String> dadosSindicais) {
        Memento memento = new Memento(empregados, dadosSindicais);
        undo.push(memento);
        redo.clear();
    }

    public Memento undo (Memento estadoAtual) {
        if (undo.isEmpty()) {
            throw new IllegalStateException("Nao ha comando a desfazer.");
        }
        redo.push(estadoAtual);
        return undo.pop();
    }

    public Memento redo (Memento estadoAtual) {
        if (redo.isEmpty()) {
            throw new IllegalStateException("Nao ha comando a refazer");
        }
        undo.push(estadoAtual);
        return redo.pop();
    }
}
