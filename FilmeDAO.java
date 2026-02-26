package venda_de_ingressos_de_cinema;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class FilmeDAO {
    public static void main(String[] args) {
    FilmeDAO.listarFilmes();
}


    public static void listarFilmes() {
        String sql = "SELECT id, nome, genero, duracao FROM filmes";

        try (
            Connection conn = Conexao.conectar();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
        ) {
            System.out.println("=== FILMES CADASTRADOS ===");

            while (rs.next()) {
                System.out.println(
                    "ID: " + rs.getInt("id") +
                    " | Nome: " + rs.getString("nome") +
                    " | Gênero: " + rs.getString("genero") +
                    " | Duração: " + rs.getInt("duracao")
                );
            }

        } catch (Exception e) {
            System.out.println("Erro ao listar filmes");
            e.printStackTrace();
        }
    }
}
