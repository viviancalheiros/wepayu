package br.ufal.ic.p2.wepayu.Exception.Horas;

public class HoraNulaException extends RuntimeException {
    public HoraNulaException () {
        super("Horas devem ser positivas.");
    }
}
