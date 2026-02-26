package venda_de_ingressos_de_cinema;

import java.sql.Connection;

public class TesteConexao {
    public static void main(String[] args) {
        Connection c = Conexao.conectar();
        if (c != null) {
            System.out.println("Conectado ao MySQL com sucesso 🚀");
        } else {
            System.out.println("Falha na conexão");
        }
    }
}
