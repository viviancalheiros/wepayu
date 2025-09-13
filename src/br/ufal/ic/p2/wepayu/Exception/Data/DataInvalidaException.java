package br.ufal.ic.p2.wepayu.Exception.Data;

public class DataInvalidaException extends Exception {
    public DataInvalidaException (String data) {
        super(data.equals("data") ? "Data invalida." : "Data " + data + " invalida.");
    }
}
