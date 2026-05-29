    package conexao;

// Imports necessários para trabalhar com conexão SQL
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {

    // Método principal que tenta estabelecer e retornar a conexão
    public static Connection conectar() {
        Connection conexao = null;
        try {
            // 1. REGISTRAR O DRIVER
            // informa ao Java qual classe ele deve usar para se comunicar com o MySQL.
            Class.forName("com.mysql.jdbc.Driver");
            
            // 2. ESTABELECER A CONEXÃO
            // Usa as configurações (URL, Usuário, Senha) para abrir a conexão real.
            conexao = DriverManager.getConnection("jdbc:mysql://localhost:3306/dpsystembd?serverTimezone=America/Sao_Paulo", "root", "root");
            
            System.out.println("Conexão estabelecida com sucesso!");
            return conexao;

        } catch (ClassNotFoundException e) {
            System.err.println("ERRO: Driver MySQL não encontrado. Verifique se o .jar foi adicionado.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("ERRO: Falha ao conectar ao banco de dados! Senha ou MySQL desligado.");
            e.printStackTrace();
        }
        return null;
    }

    // Método main para testar a conexão (você pode apagar depois de funcionar)
    public static void main(String[] args) {
        System.out.println("Iniciando teste de conexão...");
        Connection conn = conectar();
        
        // Se a conexão foi bem-sucedida, tentamos fechar.
        if (conn != null) {
            try {
                conn.close();
                System.out.println("Conexão de teste fechada.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}