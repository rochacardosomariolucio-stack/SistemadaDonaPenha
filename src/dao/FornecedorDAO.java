package dao;

import conexao.Conexao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FornecedorDAO {

    public boolean adicionarFornecedor(String nome, String whatsapp) {
        // O ID (idFornecedor) é geralmente gerado automaticamente (AUTO_INCREMENT)
        String sql = "INSERT INTO fornecedor (Nome, Whatsapp, Ativo) VALUES (?, ?, 1)";
        Connection conn = null;
        PreparedStatement pstm = null;

        try {
            conn = Conexao.conectar();
            pstm = conn.prepareStatement(sql);

            // 1. Parâmetro Nome
            pstm.setString(1, nome.trim());
            // 2. Parâmetro Whatsapp (assumindo que esta coluna exista)
            pstm.setString(2, whatsapp.trim());

            int linhasAfetadas = pstm.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            // Idealmente, você deve tratar erros de violação de chave (nome duplicado)
            System.err.println("ERRO ao adicionar fornecedor: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            // Bloco finally para garantir o fechamento dos recursos
            try {
                if (pstm != null) {
                    pstm.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Erro ao fechar recursos: " + e.getMessage());
            }
        }
    }

    public boolean atualizarNomeFornecedor(int idFornecedor, String novoNome) {
        String sql = "UPDATE fornecedor SET Nome = ? WHERE idFornecedor = ?";
        Connection conn = null;
        PreparedStatement pstm = null;

        try {
            conn = Conexao.conectar();
            pstm = conn.prepareStatement(sql);
            pstm.setString(1, novoNome.trim()); // O novo nome
            pstm.setInt(2, idFornecedor); // Filtra pelo ID

            return pstm.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("ERRO ao atualizar o nome do fornecedor: " + e.getMessage());
            return false;
        } finally {
            try {
                if (pstm != null) {
                    pstm.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                /* ... */ }
        }
    }

    public String buscarNomeAtualPorId(int idFornecedor) {
        String sql = "SELECT Nome FROM fornecedor WHERE idFornecedor = ? AND Ativo = 1";
        String nomeAtual = null;
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;

        try {
            conn = Conexao.conectar();
            pstm = conn.prepareStatement(sql);
            pstm.setInt(1, idFornecedor);
            rs = pstm.executeQuery();

            if (rs.next()) {
                nomeAtual = rs.getString("Nome");
            }

        } catch (SQLException e) {
            System.err.println("ERRO ao buscar nome por ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
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
                System.err.println("Erro ao fechar recursos: " + e.getMessage());
            }
        }
        return nomeAtual;
    }

    // Dentro da classe ProdutoDAO
    public String buscarNomeFornecedor(String nomeProduto) {
        String sql = "SELECT f.Nome FROM fornecedor f JOIN estoque e ON e.idFornecedor = f.idFornecedor WHERE e.produto = ?";
        String nomeFornecedor = "Não Encontrado"; // Valor padrão se não achar

        try (java.sql.Connection conn = Conexao.conectar(); // Use sua classe/método de conexão real
                 java.sql.PreparedStatement pstm = conn.prepareStatement(sql)) {

            pstm.setString(1, nomeProduto);

            try (java.sql.ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    nomeFornecedor = rs.getString("Nome");
                }
            }

        } catch (java.sql.SQLException e) {
            // Trate o erro de SQL, por exemplo:
            System.err.println("Erro ao buscar fornecedor: " + e.getMessage());
        }

        return nomeFornecedor;
    }

    public int buscarIdPorNome(String nomeFornecedor) {
        String sql = "SELECT idFornecedor FROM fornecedor WHERE Nome = ?";
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        int id = -1; // -1 indica que não foi encontrado

        try {
            conn = Conexao.conectar();
            pstm = conn.prepareStatement(sql);
            pstm.setString(1, nomeFornecedor);
            rs = pstm.executeQuery();

            if (rs.next()) {
                id = rs.getInt("idFornecedor");
            }
        } catch (Exception e) {
            System.err.println("ERRO ao buscar ID do fornecedor: " + e.getMessage());
        } finally {
            // Fechar recursos
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
                // ...
            }
        }
        return id;
    }

    public boolean existeFornecedor(String nome) {
        // SQL para contar quantas linhas existem com aquele nome E QUE ESTÃO ATIVAS
        String sql = "SELECT count(*) FROM fornecedor WHERE Nome = ? AND Ativo = 1";
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;

        try {
            conn = Conexao.conectar();
            pstm = conn.prepareStatement(sql);
            pstm.setString(1, nome);
            rs = pstm.executeQuery();

            if (rs.next()) {
                // Retorna true se a contagem for maior que 0 (ou seja, se o fornecedor ativo existe)
                return rs.getInt(1) > 0;
            }

        } catch (Exception e) { // <-- Mantenha apenas este bloco catch
            // Captura e trata qualquer erro (SQL, Conexão, etc.)
            System.err.println("ERRO ao verificar a existência do fornecedor: " + e.getMessage());
            e.printStackTrace(); // É bom imprimir o stack trace
            return false; // Retorna false em caso de erro

        } finally {
            // Fechar recursos: Este bloco SEMPRE será executado, garantindo o fechamento.
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
                System.err.println("Erro ao fechar recursos: " + e.getMessage());
            }
        }
        return false; // Retorno padrão se nada for encontrado (caso o rs.next() não execute)
    }

    /**
     * Tenta excluir um fornecedor da tabela 'fornecedor' usando o nome.
     *
     * @param nomeFornecedor O nome do fornecedor a ser excluído.
     * @return true se a exclusão foi bem-sucedida (pelo menos 1 linha afetada),
     * false caso contrário.
     */
    public boolean inativarFornecedorEProdutos(int idFornecedor) {
        // Comando SQL de Inativação (UPDATE)
        String sqlFornecedor = "UPDATE fornecedor SET Ativo = 0 WHERE idFornecedor = ?";
        String sqlEstoque = "UPDATE estoque SET Ativo = 0 WHERE idFornecedor = ?";
        Connection conn = null;

        try {
            conn = Conexao.conectar();
            conn.setAutoCommit(false); // Inicia uma transação segura

            // 1. Inativa o Fornecedor
            try (PreparedStatement pstmFornecedor = conn.prepareStatement(sqlFornecedor)) {
                pstmFornecedor.setInt(1, idFornecedor);
                pstmFornecedor.executeUpdate();
            }

            // 2. Inativa os Produtos Vinculados
            try (PreparedStatement pstmEstoque = conn.prepareStatement(sqlEstoque)) {
                pstmEstoque.setInt(1, idFornecedor);
                pstmEstoque.executeUpdate();
            }

            conn.commit(); // Confirma as duas operações
            return true;

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("ERRO ao fazer rollback: " + ex.getMessage());
                }
            }
            System.err.println("ERRO ao inativar fornecedor e produtos: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("ERRO ao fechar conexão: " + e.getMessage());
            }
        }
    }

    public Map<Integer, String> buscarFornecedoresAtivos() {
        String sql = "SELECT idFornecedor, Nome FROM fornecedor WHERE Ativo = 1 ORDER BY Nome";
        Map<Integer, String> fornecedoresAtivos = new HashMap<>();

        try (Connection conn = Conexao.conectar(); PreparedStatement pstm = conn.prepareStatement(sql); ResultSet rs = pstm.executeQuery()) {

            while (rs.next()) {
                // Mapeia o ID e o Nome
                fornecedoresAtivos.put(rs.getInt("idFornecedor"), rs.getString("Nome"));
            }
        } catch (SQLException e) {
            System.err.println("ERRO ao buscar fornecedores ativos: " + e.getMessage());
        }
        return fornecedoresAtivos;
    }

    public boolean reativarFornecedorEProdutos(int idFornecedor) {
        // SQL para definir Ativo = 1 (Reativar)
        String sqlFornecedor = "UPDATE fornecedor SET Ativo = 1 WHERE idFornecedor = ?";
        String sqlEstoque = "UPDATE estoque SET Ativo = 1 WHERE idFornecedor = ?";
        Connection conn = null;

        try {
            conn = Conexao.conectar();
            conn.setAutoCommit(false); // Inicia transação

            // 1. Reativa o Fornecedor
            try (PreparedStatement pstmFornecedor = conn.prepareStatement(sqlFornecedor)) {
                pstmFornecedor.setInt(1, idFornecedor);
                pstmFornecedor.executeUpdate();
            }

            // 2. Reativa os Produtos Vinculados
            try (PreparedStatement pstmEstoque = conn.prepareStatement(sqlEstoque)) {
                pstmEstoque.setInt(1, idFornecedor);
                pstmEstoque.executeUpdate();
            }

            conn.commit(); // Confirma as duas operações
            return true;

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Desfaz se houver erro
                } catch (SQLException ex) {
                    /* Tratamento de erro */ }
            }
            System.err.println("ERRO ao reativar fornecedor e produtos: " + e.getMessage());
            return false;
        } finally {
            // Fechar conexão
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                /* Tratamento de erro */ }
        }
    }

    public Map<Integer, String> buscarFornecedoresInativos() {
        String sql = "SELECT idFornecedor, Nome FROM fornecedor WHERE Ativo = 0 ORDER BY Nome";
        Map<Integer, String> fornecedoresInativos = new HashMap<>();

        try (Connection conn = Conexao.conectar(); PreparedStatement pstm = conn.prepareStatement(sql); ResultSet rs = pstm.executeQuery()) {

            while (rs.next()) {
                // Mapeia o ID e o Nome
                fornecedoresInativos.put(rs.getInt("idFornecedor"), rs.getString("Nome"));
            }
        } catch (SQLException e) {
            System.err.println("ERRO ao buscar fornecedores inativos: " + e.getMessage());
        }
        return fornecedoresInativos;
    }

    public List<String> buscarNomesFornecedores() {
        String sql = "SELECT Nome FROM fornecedor ORDER BY Nome";
        List<String> nomes = new ArrayList<>();

        try (Connection conn = Conexao.conectar(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                nomes.add(rs.getString("Nome"));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar nomes de fornecedores: " + e.getMessage());
        }
        return nomes;
    }

    // Lista fornecedores ativos para preencher a JTable
    public java.util.List<Object[]> listarFornecedoresCompleto() {
        java.util.List<Object[]> lista = new java.util.ArrayList<>();

        // MUDANÇA: Adicionado 'ORDER BY idFornecedor' para garantir a sequência numérica
        String sql = "SELECT idFornecedor, Nome, Telefone, Cidade FROM fornecedor WHERE Ativo = 1 ORDER BY idFornecedor";

        try (Connection conn = Conexao.conectar(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getInt("idFornecedor"),
                    rs.getString("Nome"),
                    rs.getString("Telefone"),
                    rs.getString("Cidade")
                });
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar fornecedores: " + e.getMessage());
        }
        return lista;
    }

    // Lista fornecedores que estão na lixeira (Ativo = 0)
    public java.util.List<Object[]> listarFornecedoresInativos() {
        java.util.List<Object[]> lista = new java.util.ArrayList<>();
        String sql = "SELECT idFornecedor, Nome, Telefone, Cidade FROM fornecedor WHERE Ativo = 0 ORDER BY Nome";

        try (Connection conn = Conexao.conectar(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(new Object[]{rs.getInt("idFornecedor"), rs.getString("Nome"), rs.getString("Telefone"), rs.getString("Cidade")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

// Método para restaurar o fornecedor
    public boolean reativarFornecedor(int id) {
        String sql = "UPDATE fornecedor SET Ativo = 1 WHERE idFornecedor = ?";
        try (Connection conn = Conexao.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    // Insere um novo fornecedor no banco de dados
    public boolean cadastrarFornecedor(String nome, String tel, String cep, String bairro, String rua, String uf, String cidade) {
        String sql = "INSERT INTO fornecedor (Nome, Telefone, CEP, Bairro, Rua, UF, Cidade, Ativo) VALUES (?, ?, ?, ?, ?, ?, ?, 1)";
        try (Connection conn = Conexao.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nome);
            stmt.setString(2, tel);
            stmt.setString(3, cep);
            stmt.setString(4, bairro);
            stmt.setString(5, rua);
            stmt.setString(6, uf);
            stmt.setString(7, cidade);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao cadastrar fornecedor: " + e.getMessage());
            return false;
        }
    }

    public boolean atualizarNomeF(int id, String nome) {
        String sql = "UPDATE fornecedor SET Nome = ? WHERE idFornecedor = ?";
        return executarUpdate(sql, nome, id);
    }

    public boolean atualizarTelefoneF(int id, String tel) {
        String sql = "UPDATE fornecedor SET Telefone = ? WHERE idFornecedor = ?";
        return executarUpdate(sql, tel, id);
    }

    public boolean atualizarCidadeF(int id, String cid) {
        String sql = "UPDATE fornecedor SET Cidade = ? WHERE idFornecedor = ?";
        return executarUpdate(sql, cid, id);
    }

// Método genérico para evitar repetição no DAO
    private boolean executarUpdate(String sql, String valor, int id) {
        try (java.sql.Connection conn = conexao.Conexao.conectar(); java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, valor);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean inativarFornecedor(int id) {
        String sql = "UPDATE fornecedor SET Ativo = 0 WHERE idFornecedor = ?";
        try (java.sql.Connection conn = conexao.Conexao.conectar(); java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (java.sql.SQLException e) {
            return false;
        }
    }
}
