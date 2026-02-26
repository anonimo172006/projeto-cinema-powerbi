package venda_de_ingressos_de_cinema;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {

    private static final String URL =
        "jdbc:mysql://localhost:3306/cinema_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

    private static final String USUARIO = "root";
    private static final String SENHA = "17271524";

    public static Connection conectar() {
        try {
            return DriverManager.getConnection(URL, USUARIO, SENHA);
        } catch (SQLException e) {
            System.out.println("Erro ao conectar no banco:");
            e.printStackTrace();
            return null;
        }
    }
}
