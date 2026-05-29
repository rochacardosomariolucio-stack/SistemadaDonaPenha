package dao;

// Imports necessários:
import conexao.Conexao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProdutoDAO {

    //Classe POJO estática para transporte de dados básicos do produto.
    public static class Produto {

        private String nome;
        private int quantidade;
        private double preco;

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public int getQuantidade() {
            return quantidade;
        }

        public void setQuantidade(int quantidade) {
            this.quantidade = quantidade;
        }

        public double getPreco() {
            return preco;
        }

        public void setPreco(double preco) {
            this.preco = preco;
        }
    }

    // ========================================================================
    // SEÇÃO DE CONSULTAS E BUSCAS
    // ========================================================================
    // Busca nome, preço e fornecedor pelo ID para preencher a tela de vendas
    
    // TELA VENDA E TELA COMPRA
    public Map<String, Object> buscarProdutoPorId(int idProduto) {
        String sql = "SELECT e.produto, e.preco, f.Nome AS NomeFornecedor "
                + "FROM estoque e JOIN fornecedor f ON e.idFornecedor = f.idFornecedor "
                + "WHERE e.idEstoque = ? AND e.Ativo = 1";
        Map<String, Object> dadosProduto = new HashMap<>();
        try (Connection conn = Conexao.conectar(); PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setInt(1, idProduto);
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    dadosProduto.put("nome", rs.getString("produto"));
                    dadosProduto.put("fornecedor", rs.getString("NomeFornecedor"));
                    dadosProduto.put("preco", rs.getDouble("preco"));
                }
            }
        } catch (SQLException e) {
            System.err.println("ERRO ao buscar produto por ID: " + e.getMessage());
        }
        return dadosProduto;
    }
    
    //TELA VENDA E TELA COMPRA
    public List<String> buscarNomesProdutos() {
        String sql = "SELECT produto FROM estoque WHERE Ativo = 1 ORDER BY produto";
        List<String> produtos = new ArrayList<>();
        try (Connection conn = Conexao.conectar(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                produtos.add(rs.getString("produto"));
            }
        } catch (SQLException e) {
            System.err.println("ERRO: Falha ao buscar nomes de produtos. " + e.getMessage());
            e.printStackTrace();
        }
        return produtos;
    }

    // TELA VENDA E TELA COMPRA
    public double buscarPrecoProduto(String nomeProduto) {
        String sql = "SELECT preco FROM estoque WHERE produto = ?";
        double preco = 0.0;

        try (Connection conn = Conexao.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nomeProduto);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    preco = rs.getDouble("preco");
                }
            }
        } catch (SQLException e) {
            System.err.println("ERRO: Falha ao buscar o preço do produto: " + nomeProduto);
            e.printStackTrace();
        }
        return preco;
    }

    // Consulta a quantidade total disponível em estoque pelo nome
    public int buscarQuantidade(String nomeProduto) {
        String sql = "SELECT quantidade FROM Estoque WHERE produto = ?";
        int quantidade = 0;
        try (Connection conn = Conexao.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nomeProduto);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    quantidade = rs.getInt("quantidade");
                }
            }
        } catch (SQLException e) {
            System.err.println("ERRO: Falha ao buscar a quantidade de " + nomeProduto);
            e.printStackTrace();
            quantidade = 0;
        }
        return quantidade;
    }

    // Busca o saldo atual pelo ID para processamento do extrato financeiro
    public int buscarSaldoAtual(int idProd) {
        String sql = "SELECT quantidade FROM estoque WHERE idEstoque = ?";
        try (Connection conn = Conexao.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idProd);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("quantidade");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ========================================================================
    // ESTOQUE E MOVIMENTAÇÃO
    // ========================================================================
    // Altera a quantidade física de um produto no banco de dados
    public boolean atualizarQuantidade(String nomeProduto, int novaQuantidade) {
        // Comado SQL: Atualizar a coluna 'quantidade' na tabela 'Estoque' 
        // onde a coluna 'produto' corresponde ao nome.
        String sql = "UPDATE Estoque SET quantidade = ? WHERE produto = ?";

        try (Connection conn = Conexao.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, novaQuantidade);
            stmt.setString(2, nomeProduto);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0; // Retorna true se atualizou pelo menos uma linha

        } catch (SQLException e) {
            System.err.println("ERRO: Falha ao atualizar a quantidade de " + nomeProduto);
            e.printStackTrace();
            return false;
        }
    }

    // Subtrai itens do estoque após a confirmação de uma venda
    public boolean descontarEstoque(String nomeProduto, int quantidadeVendida) {
        int estoqueAtual = buscarQuantidade(nomeProduto);
        int novaQuantidade = estoqueAtual - quantidadeVendida;
        if (novaQuantidade < 0) {
            System.err.println("Erro: Tentativa de descontar mais do que o estoque (" + estoqueAtual + ") permite.");
            return false;
        }
        return atualizarQuantidade(nomeProduto, novaQuantidade);
    }

    // Grava uma linha no histórico detalhando entradas ou saídas e o saldo final
// Adicione "String vendedor" ao final dos parênteses
    public void registrarNoExtrato(int idProd, String tipo, int qtdMovimentada, String motivo, String vendedor) {
        String sql = "INSERT INTO movimentacao_estoque (idProduto, tipo, quantidade, motivo, saldo_momento, vendedor) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idProd);
            stmt.setString(2, tipo);
            stmt.setInt(3, qtdMovimentada);
            stmt.setString(4, motivo);
            stmt.setInt(5, buscarSaldoAtual(idProd));
            stmt.setString(6, vendedor); // Agora a variável existe e o erro sumirá
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ========================================================================
    // GESTÃO DE PRODUTOS
    // ========================================================================
    // Insere um novo produto no sistema vinculando-o a um fornecedor da lista
    public boolean cadastrarNovoProduto(String nome, double preco, int qtd, int idFornecedor) {
        String sql = "INSERT INTO estoque (produto, preco, quantidade, idFornecedor, Ativo) VALUES (?, ?, ?, ?, 1)";
        try (java.sql.Connection conn = conexao.Conexao.conectar(); java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nome);
            stmt.setDouble(2, preco);
            stmt.setInt(3, qtd);
            stmt.setInt(4, idFornecedor);
            return stmt.executeUpdate() > 0;
        } catch (java.sql.SQLException e) {
            return false;
        }
    }

    // Desativa o produto (exclusão lógica) para que ele não apareça mais nas vendas
    public boolean inativarProduto(int id) {
        String sql = "UPDATE estoque SET Ativo = 0 WHERE idEstoque = ?";
        try (Connection conn = Conexao.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    // Ativa novamente um produto que estava na lixeira
    public boolean reativarProduto(int id) {
        String sql = "UPDATE estoque SET Ativo = 1 WHERE idEstoque = ?";
        try (Connection conn = Conexao.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    // ========================================================================
    // RELATÓRIOS (LISTAGEM PARA TABELAS)
    // ========================================================================
    // Lista todos os produtos ativos organizados numericamente pelo ID
    public List<Object[]> listarProdutosCompleto() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT idEstoque, produto, preco, quantidade FROM estoque WHERE Ativo = 1 ORDER BY idEstoque";
        try (Connection conn = Conexao.conectar(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(new Object[]{rs.getInt("idEstoque"), rs.getString("produto"), rs.getDouble("preco"), rs.getInt("quantidade")});
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar produtos: " + e.getMessage());
        }
        return lista;
    }

    // Lista apenas os produtos que foram inativados
    public List<Object[]> listarProdutosInativos() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT idEstoque, produto, preco, quantidade FROM estoque WHERE Ativo = 0 ORDER BY produto";
        try (Connection conn = Conexao.conectar(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(new Object[]{rs.getInt("idEstoque"), rs.getString("produto"), rs.getDouble("preco"), rs.getInt("quantidade")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // No ProdutoDAO.java
    public java.util.List<Object[]> listarMovimentacaoExtrato() {
        java.util.List<Object[]> lista = new java.util.ArrayList<>();
        String sql = "SELECT p.produto, m.tipo, m.quantidade, m.data_hora, m.saldo_momento, m.vendedor " // BUSCA O VENDEDOR
                + "FROM movimentacao_estoque m JOIN estoque p ON m.idProduto = p.idEstoque "
                + "ORDER BY m.data_hora DESC";
        try (java.sql.Connection conn = conexao.Conexao.conectar(); java.sql.PreparedStatement stmt = conn.prepareStatement(sql); java.sql.ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getString("produto"),
                    rs.getString("tipo"),
                    rs.getInt("quantidade"),
                    rs.getTimestamp("data_hora"),
                    rs.getInt("saldo_momento"),
                    rs.getString("vendedor") // Adiciona ao array para a tabela
                });
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean limparHistoricoMovimentacao() {
        String sql = "DELETE FROM movimentacao_estoque";
        try (java.sql.Connection conn = conexao.Conexao.conectar(); java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
            return stmt.executeUpdate() > 0;
        } catch (java.sql.SQLException e) {
            return false;
        }
    }

    // ========================================================================
    // GESTÃO DE ESTOQUE 
    // ========================================================================
    // Busca um objeto Produto completo através do ID
    public int buscarIdProdutoPorNome(String nomeProduto) {
        // Busca o ID (idEstoque) do produto que está ativo
        String sql = "SELECT idEstoque FROM estoque WHERE produto = ? AND Ativo = 1";
        int idProduto = -1; // Valor padrão para indicar que não foi encontrado

        try (Connection conn = Conexao.conectar(); PreparedStatement pstm = conn.prepareStatement(sql)) {

            pstm.setString(1, nomeProduto);

            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    idProduto = rs.getInt("idEstoque");
                }
            }
        } catch (SQLException e) {
            System.err.println("ERRO ao buscar ID do produto por nome: " + e.getMessage());
        }
        return idProduto;
    }

    // Busca um objeto Produto completo através do ID
    public Produto buscarPorId(int idEstoque) {
        String sql = "SELECT produto, quantidade, preco FROM estoque WHERE idEstoque = ? AND Ativo = 1";
        try (Connection conn = Conexao.conectar(); // Use seu método de conexão real
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idEstoque);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Produto p = new Produto();
                p.setNome(rs.getString("produto"));
                p.setQuantidade(rs.getInt("quantidade"));
                p.setPreco(rs.getDouble("preco")); // <--- ESSA LINHA BUSCA O PREÇO
                return p;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ========================================================================
    // HISTÓRICO DE VENDAS
    // ========================================================================
    // Salva o registro financeiro da venda no histórico geral
    public boolean salvarVendaNoHistorico(String produto, int qtd, double valor, String pagto, String vendedor) {
        String sql = "INSERT INTO vendas (produto, quantidade, valor_total, forma_pagamento, data_venda, vendedor) VALUES (?, ?, ?, ?, NOW(), ?)";
        try (Connection conn = Conexao.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, produto);
            stmt.setInt(2, qtd);
            stmt.setDouble(3, valor);
            stmt.setString(4, pagto);
            stmt.setString(5, vendedor); // <-- O quinto parâmetro
            stmt.execute();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean adicionarEstoque(String nomeProduto, int quantidadeAdicionada) {
        int estoqueAtual = buscarQuantidade(nomeProduto);
        int novaQuantidade = estoqueAtual + quantidadeAdicionada;
        if (estoqueAtual == 0 && quantidadeAdicionada > 0) {
        }
        return atualizarQuantidade(nomeProduto, novaQuantidade);
    }

    public java.util.List<Object[]> listarVendas() {
        java.util.List<Object[]> lista = new java.util.ArrayList<>();
        // 🚨 SQL ATUALIZADO: Buscar todos os campos (incluindo vendedor)
        String sql = "SELECT * FROM vendas ORDER BY data_venda DESC";

        try (java.sql.Connection conn = Conexao.conectar(); java.sql.PreparedStatement stmt = conn.prepareStatement(sql); java.sql.ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getInt("id"),
                    rs.getString("produto"),
                    rs.getString("data_venda"),
                    rs.getInt("quantidade"),
                    rs.getDouble("valor_total"),
                    rs.getString("forma_pagamento"),
                    rs.getString("vendedor")
                });
            }
        } catch (java.sql.SQLException e) {
            System.out.println("Erro ao listar vendas: " + e.getMessage());
        }
        return lista;
    }

    public boolean limparHistoricoVendas() {
        String sql = "DELETE FROM vendas"; // Apaga todas as linhas da tabela

        try (java.sql.Connection conn = Conexao.conectar(); java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.executeUpdate();
            return true;
        } catch (java.sql.SQLException e) {
            System.out.println("Erro ao limpar histórico: " + e.getMessage());
            return false;
        }
    }

    public boolean atualizarNome(int id, String novoNome) {
        String sql = "UPDATE estoque SET produto = ? WHERE idEstoque = ?";
        try (Connection conn = Conexao.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, novoNome);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

// Método para atualizar apenas o Preço do Produto
    public boolean atualizarPreco(int id, double novoPreco) {
        String sql = "UPDATE estoque SET preco = ? WHERE idEstoque = ?";
        try (Connection conn = Conexao.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, novoPreco);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean atualizarQuantidadePorId(int id, int novaQtd) {
        String sql = "UPDATE estoque SET quantidade = ? WHERE idEstoque = ?";
        try (Connection conn = Conexao.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, novaQtd);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public int buscarIdPeloNome(String nomeFornecedor) {
        String sql = "SELECT idFornecedor FROM fornecedor WHERE Nome = ?";
        int id = -1;

        try (Connection conn = Conexao.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nomeFornecedor);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    id = rs.getInt("idFornecedor");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar ID do fornecedor: " + e.getMessage());
        }
        return id;
    }

    // Busca produtos filtrados por um fornecedor específico
    public List<Object[]> listarProdutosPorFornecedor(int idF) {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT idEstoque, produto, preco, quantidade FROM estoque WHERE idFornecedor = ? AND Ativo = 1";

        try (Connection conn = Conexao.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idF);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Object[]{rs.getInt("idEstoque"), rs.getString("produto"), rs.getDouble("preco"), rs.getInt("quantidade")});
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

}
