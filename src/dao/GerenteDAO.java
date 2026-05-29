package dao;

import conexao.Conexao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GerenteDAO {

    public boolean verificarLogin(String usuario, String senha) {
        String sql = "SELECT nome FROM gerente WHERE nome = ? AND senha = ?";
        try (Connection conn = Conexao.conectar(); PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setString(1, usuario.trim());
            pstm.setString(2, senha.trim());
            try (ResultSet rs = pstm.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            return false;
        }
    }

    // MUDOU: Agora insere um novo, permitindo ter 2 ou mais na tabela
    public boolean cadastrarGerente(String nome, String senha) {
        String sql = "INSERT INTO gerente (nome, senha) VALUES (?, ?)";
        try (Connection conn = Conexao.conectar(); PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setString(1, nome);
            pstm.setString(2, senha);
            return pstm.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    // NOVO: Para você deletar um usuário específico pelo nome
    public boolean deletarGerente(String nome) {
        String sql = "DELETE FROM gerente WHERE nome = ?";
        try (Connection conn = Conexao.conectar(); PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setString(1, nome);
            return pstm.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    // MUDOU: Conta quantos tem pra você travar em 2 na tela
    public int contarUsuarios() {
        String sql = "SELECT COUNT(*) FROM gerente";
        try (Connection conn = Conexao.conectar(); PreparedStatement pstm = conn.prepareStatement(sql); ResultSet rs = pstm.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
        }
        return 0;
    }

    public boolean atualizarSenha(String nomeGerente, String novaSenha) {
        String sql = "UPDATE gerente SET senha = ? WHERE nome = ?";
        try (Connection conn = Conexao.conectar(); PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setString(1, novaSenha);
            pstm.setString(2, nomeGerente);
            return pstm.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean existeGerenteCadastrado() {
        // ⚠️ SQL: Conta quantas linhas existem na tabela 'gerente'.
        String sql = "SELECT COUNT(*) FROM gerente WHERE nome IS NOT NULL";

        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;

        try {
            conn = Conexao.conectar();
            if (conn == null) {
                return true; // Falha na conexão: Presume que já existe para bloquear criação
            }
            pstm = conn.prepareStatement(sql);
            rs = pstm.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1); // Pega o valor da primeira coluna (a contagem)
                return count > 0;         // true se houver 1 ou mais registros
            }
            return false;
        } catch (SQLException erro) {
            System.err.println("Erro ao verificar existência de gerente: " + erro.getMessage());
            return true; // Em caso de erro, é mais seguro bloquear o cadastro (retorna true)
        } finally {
            // Fechamento de recursos
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstm != null) {
                    pstm.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean deletarGerenteLogado(String nomeUsuario) {
        // ⚠️ Importante: O WHERE garante que só a conta logada será apagada
        String sql = "DELETE FROM gerente WHERE nome = ?";

        Connection conn = null;
        PreparedStatement pstm = null;

        try {
            conn = Conexao.conectar();
            pstm = conn.prepareStatement(sql);

            pstm.setString(1, nomeUsuario);

            // Retorna true se conseguiu apagar a linha específica
            return pstm.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao deletar usuário logado: " + e.getMessage());
            return false;
        } finally {
            // Fechamento de recursos padrão do seu código
            try {
                if (pstm != null) {
                    pstm.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public java.util.List<String> listarNomesUsuarios() {
        java.util.List<String> lista = new java.util.ArrayList<>();
        String sql = "SELECT nome FROM gerente ORDER BY nome ASC";
        try (Connection conn = Conexao.conectar(); PreparedStatement pstm = conn.prepareStatement(sql); ResultSet rs = pstm.executeQuery()) {
            while (rs.next()) {
                lista.add(rs.getString("nome"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}
