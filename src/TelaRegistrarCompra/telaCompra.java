package TelaRegistrarCompra;

import Rounded.RoundedPanel;
import TelaRegistrarCompraConcluida.telaCompraConcluida;
import UsuarioLogado.SessaoUsuario;
import dao.FornecedorDAO;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import dao.ProdutoDAO;
import javax.swing.SpinnerNumberModel;
import java.util.Map;
import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

/**
 *
 * @author Usuário
 */
public class telaCompra extends javax.swing.JFrame {

    private final FornecedorDAO fornecedorDao = new FornecedorDAO();
    private final ProdutoDAO produtoDao = new ProdutoDAO();

    // NOVO: Ferramenta para formatar o valor como R$00,00
    private static final DecimalFormat df;

    // NOVO: Configura o formatador para o padrão brasileiro (R$ e vírgula)
    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("pt", "BR"));
        symbols.setCurrencySymbol("R$");
        symbols.setMonetaryDecimalSeparator(',');
        symbols.setGroupingSeparator('.');
        df = new DecimalFormat("¤#,##0.00", symbols);
    }

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(telaCompra.class.getName());

    /**
     * Creates new form telaCompra
     */
    public telaCompra() {
        initComponents();
        this.setExtendedState(MAXIMIZED_BOTH);
        carregarProdutosNoCombo();
        getRootPane().setDefaultButton(btnAddCompra);
        configurarListeners();
        atualizarCustoTotal();
        txtcustoTotal.setEditable(false);
        atualizarFornecedor();
        configurarAtalhoEsc();
        SpinnerNumberModel modelo = new SpinnerNumberModel(0, 0, 1000, 1);
        txtqtdComprada.setModel(modelo);

        jTextField2.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                // Verifica se a tecla apertada foi o ENTER
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    buscarProdutoPeloIdDigitado();
                    evt.consume();
                }
            }
        });
    }

    private void carregarProdutosNoCombo() {
        // 1. Limpa os itens atuais para não duplicar
        txtprodutoComprado.removeAllItems();

        // 2. Busca a lista atualizada do DAO (certifique-se que o método retorna List<String>)
        java.util.List<String> produtos = produtoDao.buscarNomesProdutos();

        // 3. Adiciona cada produto encontrado ao ComboBox
        for (String p : produtos) {
            txtprodutoComprado.addItem(p);
        }
    }

    private void configurarAtalhoEsc() {
        // Pega o InputMap da janela
        InputMap inputMap = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = this.getRootPane().getActionMap();

        // Define a tecla ESCAPE
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "fecharTela");

        // Define a ação que será executada
        actionMap.put("fecharTela", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Fecha apenas esta janela
            }
        });
    }

    private void configurarListeners() {
        // Se o produto selecionado mudar
        txtprodutoComprado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                atualizarCustoTotal();
                atualizarFornecedor();
            }
        });

        // Se a quantidade no Spinner mudar
        txtqtdComprada.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                atualizarCustoTotal();
            }
        });
    }

    private void atualizarFornecedor() {
        // Pega o produto que está selecionado na ComboBox
        String produtoSelecionado = (String) txtprodutoComprado.getSelectedItem();

// Na tela de compra, o nome selecionado já é o nome completo que está no DB.
        String nomeProdutoBanco = produtoSelecionado;

// Chama o método no FornecedorDAO
        String fornecedor = fornecedorDao.buscarNomeFornecedor(nomeProdutoBanco);

        // 3. Atualiza o campo de texto do fornecedor (que é o jTextField1)
        jTextField1.setText(fornecedor);

        // O campo do fornecedor DEVE ser apenas para exibição
        jTextField1.setEditable(false);
    }

// NOVO MÉTODO: O coração do cálculo
    private void atualizarCustoTotal() {
        String produtoSelecionado = (String) txtprodutoComprado.getSelectedItem();

        // Se nenhum produto estiver selecionado, limpa os campos e sai
        if (produtoSelecionado == null) {
            txtcustoTotal.setText(df.format(0.0));

            // 🚨 NOVO: Limpa o campo do ID (Ajuste jTextField2 para sua variável de ID, se for diferente)
            // Certifique-se de que jTextField2 esteja declarado na tela de compra!
            jTextField2.setText("");

            return;
        }

        // 🚨 NOVO: 1. Busca o ID pelo nome e atualiza o campo de ID
        int idProduto = produtoDao.buscarIdProdutoPorNome(produtoSelecionado);
        if (idProduto > 0) {
            // Converte o ID inteiro para String e exibe no campo de ID
            jTextField2.setText(String.valueOf(idProduto));
        } else {
            jTextField2.setText("ID Inválido"); // Avisa se não encontrar o ID
        }

        // 2. Pega a quantidade (Lógica existente)
        int quantidade = (int) txtqtdComprada.getValue();

        if (quantidade < 0) {
            quantidade = 0;
            txtqtdComprada.setValue(0);
        }

        // 3. Busca o preço no DB (Lógica existente)
        double precoUnitario = produtoDao.buscarPrecoProduto(produtoSelecionado);

        // 4. Calcula o total (Lógica existente)
        double custoTotal = precoUnitario * quantidade;

        // 5. Atualiza o campo Custo Total (Lógica existente)
        txtcustoTotal.setText(df.format(custoTotal));
    }

    private void buscarProdutoPeloIdDigitado() {
// 1. Pega o texto do campo de ID (que é o txtNomeProduto)
        String idText = jTextField2.getText().trim();

        // Limpa o campo do fornecedor e reseta o custo/seleção antes de buscar
        jTextField1.setText("");
        txtprodutoComprado.setSelectedItem(null);
        atualizarCustoTotal();

        if (idText.isEmpty()) {
            jTextField2.setText("");
            return;
        }

        try {
            int idProduto = Integer.parseInt(idText);

            // 2. Busca os dados no DAO
            Map<String, Object> dados = produtoDao.buscarProdutoPorId(idProduto);

            if (dados != null && !dados.isEmpty()) {
                String nomeProduto = (String) dados.get("nome");
                String nomeFornecedor = (String) dados.get("fornecedor");

                // 3. ATUALIZA A TELA: Seleciona o produto encontrado no JComboBox (txtprodutoComprado)
                txtprodutoComprado.setSelectedItem(nomeProduto);

                // 4. Garante que o nome do fornecedor é exibido (é o jTextField1)
                jTextField1.setText(nomeFornecedor);
                jTextField1.setEditable(false);

                // LINHA REMOVIDA: Era a linha que mudava o texto do campo de ID para o nome do produto.
                // txtNomeProduto.setText(nomeProduto); // <-- ESTA LINHA FOI REMOVIDA
                // O campo txtNomeProduto agora manterá o ID digitado (idText) ou o último valor que tinha.
            } else {
                // Produto não encontrado
                jTextField2.setText("ID Inválido ou Inativo");

                JOptionPane.showMessageDialog(this,
                        "Produto com ID " + idProduto + " não encontrado ou inativo.",
                        "Erro de Produto", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            // Se o que foi digitado não é um número
            jTextField2.setText("ID Inválido (Deve ser um número)");
        } catch (Exception e) {
            System.err.println("ERRO ao buscar produto por ID: " + e.getMessage());
            jTextField2.setText("Erro interno");
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new RoundedPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtprodutoComprado = new javax.swing.JComboBox<>();
        txtqtdComprada = new javax.swing.JSpinner();
        txtcustoTotal = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        btnAddCompra = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 243, 215));
        jPanel1.setMaximumSize(new java.awt.Dimension(1500, 808));
        jPanel1.setPreferredSize(new java.awt.Dimension(1500, 808));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 48)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(138, 31, 31));
        jLabel1.setText("REGISTRO DE COMPRA");

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setMaximumSize(new java.awt.Dimension(1500, 808));

        jLabel6.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel6.setText("Id Produto Comprado:");

        jLabel7.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel7.setText("Custo Total (R$):");

        jLabel8.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel8.setText("Quantidade Comprada: ");

        txtprodutoComprado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] {
            "Amstel Latão",
            "Brahma Latão",
            "Heineken Latão",
            "Coca Cola 2L Sabor Orig.",
            "Coca Cola 2L zero açúcar.",
            "Guaraná Lata",
            "Guaraná 2L Sabor Orig.",
            "Guaraná 2L zero açúcar",
            "Sacolé",}));
txtprodutoComprado.setToolTipText("Selecione o produto comprado.");
txtprodutoComprado.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
        txtprodutoCompradoActionPerformed(evt);
    }
    });

    txtqtdComprada.setToolTipText("Selecione a quantidade comprada.");

    txtcustoTotal.setText("R$00,00");
    txtcustoTotal.setToolTipText("Custo total é gerado automaticamente.");
    txtcustoTotal.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            txtcustoTotalActionPerformed(evt);
        }
    });

    jLabel9.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
    jLabel9.setText("Fornecedor:");

    jTextField1.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jTextField1ActionPerformed(evt);
        }
    });

    jLabel10.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
    jLabel10.setText("Produto Comprado:");

    jTextField2.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jTextField2ActionPerformed(evt);
        }
    });

    javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(
        jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel2Layout.createSequentialGroup()
            .addGap(66, 66, 66)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel7)
                        .addComponent(jLabel9)
                        .addComponent(jLabel10))
                    .addGap(64, 64, 64)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 367, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtcustoTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 367, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtprodutoComprado, javax.swing.GroupLayout.PREFERRED_SIZE, 367, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel8)
                        .addComponent(jLabel6))
                    .addGap(29, 29, 29)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 367, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, Short.MAX_VALUE))
                        .addComponent(txtqtdComprada))))
            .addContainerGap(52, Short.MAX_VALUE))
    );
    jPanel2Layout.setVerticalGroup(
        jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel2Layout.createSequentialGroup()
            .addGap(25, 25, 25)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                .addComponent(txtprodutoComprado, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel10))
            .addGap(45, 45, 45)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel6)
                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGap(35, 35, 35)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel8)
                .addComponent(txtqtdComprada, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGap(36, 36, 36)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel7)
                .addComponent(txtcustoTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGap(48, 48, 48)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel9)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addContainerGap(54, Short.MAX_VALUE))
    );

    btnAddCompra.setBackground(new java.awt.Color(51, 153, 0));
    btnAddCompra.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
    btnAddCompra.setForeground(new java.awt.Color(255, 255, 255));
    btnAddCompra.setText("Registrar Compra ");
    btnAddCompra.setToolTipText("Registre a compra.");
    btnAddCompra.setPreferredSize(new java.awt.Dimension(219, 71));
    btnAddCompra.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnAddCompraActionPerformed(evt);
        }
    });

    jButton3.setBackground(new java.awt.Color(138, 31, 31));
    jButton3.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
    jButton3.setForeground(new java.awt.Color(255, 255, 255));
    jButton3.setText("Voltar");
    jButton3.setToolTipText("Volte para o menu principal");
    jButton3.setPreferredSize(new java.awt.Dimension(219, 71));
    jButton3.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButton3ActionPerformed(evt);
        }
    });

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
        jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
            .addGap(0, 483, Short.MAX_VALUE)
            .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(44, 44, 44)
            .addComponent(btnAddCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(488, Short.MAX_VALUE))
        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel1)
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    jPanel1Layout.setVerticalGroup(
        jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel1Layout.createSequentialGroup()
            .addContainerGap(82, Short.MAX_VALUE)
            .addComponent(jLabel1)
            .addGap(39, 39, 39)
            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 95, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(btnAddCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addContainerGap(34, Short.MAX_VALUE))
    );

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    );
    layout.setVerticalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 843, Short.MAX_VALUE)
    );

    pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtcustoTotalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtcustoTotalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtcustoTotalActionPerformed

    private void btnAddCompraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddCompraActionPerformed

        String produtoComprado = txtprodutoComprado.getSelectedItem().toString();

        int quantidade = 0;
        double custoTotal = 0.0;
        String custoFormatado = "";

        // Tenta obter a quantidade (int) e o Custo Total (double)
        try {
            quantidade = (int) txtqtdComprada.getValue();
            if (quantidade <= 0) {
                javax.swing.JOptionPane.showMessageDialog(null,
                        "Insira a quantidade comprada para continuar",
                        "Erro de Quantidade",
                        javax.swing.JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Conversão do custo total formatado (R$XX.XXX,XX) para double
            String custoStr = txtcustoTotal.getText();
            custoFormatado = custoStr; // Mantém a string formatada para a mensagem de sucesso

            // Remove o símbolo da moeda (R$) e os separadores de milhar (.), depois troca a vírgula (,) por ponto (.)
            String valorLimpo = custoStr.replaceAll("R\\$", "").replaceAll("\\.", "").replace(",", ".").trim();
            custoTotal = Double.parseDouble(valorLimpo);

        } catch (NumberFormatException | NullPointerException e) {
            logger.log(java.util.logging.Level.SEVERE, "Erro ao converter valores de Quantidade ou Custo Total.", e);
            javax.swing.JOptionPane.showMessageDialog(null, "Erro na leitura dos campos. Verifique a Quantidade e o Custo Total.", "Erro", javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. PREPARAR NOME DO PRODUTO PARA O BANCO DE DADOS
        String nomeProdutoBanco = produtoComprado
                .replace("Caixa ", "")
                .replace("Fardo ", "")
                .trim();

        boolean sucessoAtualizacao = produtoDao.adicionarEstoque(nomeProdutoBanco, quantidade);

        // 4. EXIBIR RESULTADO E CONCLUIR
        if (sucessoAtualizacao) {

            // 🚨 NOVO: Registra a movimentação no extrato com o usuário logado
            int idProd = produtoDao.buscarIdProdutoPorNome(nomeProdutoBanco);
            produtoDao.registrarNoExtrato(idProd, "ENTRADA", quantidade, "Compra de Mercadoria", SessaoUsuario.getUsuarioLogado());

            // Se o estoque foi atualizado com sucesso, FECHA a tela atual
            this.dispose();

            telaCompraConcluida tConcluida = new telaCompraConcluida();
            tConcluida.setVisible(true);

            logger.info("Compra registrada e extrato atualizado por: " + SessaoUsuario.getUsuarioLogado());

        } else {
            // Se houve erro na atualização do estoque
            javax.swing.JOptionPane.showMessageDialog(
                    null,
                    "ERRO: Não foi possível atualizar o estoque do produto '" + nomeProdutoBanco + "'.\nVerifique a conexão e se o produto existe no banco de dados.",
                    "Erro de Conexão/Estoque",
                    javax.swing.JOptionPane.ERROR_MESSAGE
            );
        }

    }//GEN-LAST:event_btnAddCompraActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void txtprodutoCompradoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtprodutoCompradoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtprodutoCompradoActionPerformed

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new telaCompra().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddCompra;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField txtcustoTotal;
    private javax.swing.JComboBox<String> txtprodutoComprado;
    private javax.swing.JSpinner txtqtdComprada;
    // End of variables declaration//GEN-END:variables
}
