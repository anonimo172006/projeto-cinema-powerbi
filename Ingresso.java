package venda_de_ingressos_de_cinema;


public class Ingresso {

    private int idIngresso;
    private int idFilme;
    private String horario;
    private double preco;

    public Ingresso(int idIngresso, int idFilme, String horario, double preco) {
        this.idIngresso = idIngresso;
        this.idFilme = idFilme;
        this.horario = horario;
        this.preco = preco;
    }

    public String linhaTXT() {
        return idIngresso + "\t" + idFilme + "\t" + horario + "\t" + preco + "\n";
    }

}
