package venda_de_ingressos_de_cinema;
public class Filme {

    private int id;
    private String nome;
    private String genero;
    private int duracao;

    public Filme(int id, String nome, String genero, int duracao) {
        this.id = id;
        this.nome = nome;
        this.genero = genero;
        this.duracao = duracao;
    }

    public String linhaTXT() {
        return id + "\t" + nome + "\t" + genero + "\t" + duracao + "\n";
    }

}
