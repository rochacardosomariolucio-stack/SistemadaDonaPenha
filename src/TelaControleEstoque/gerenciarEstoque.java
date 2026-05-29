package TelaControleEstoque;

import dao.ProdutoDAO;

/**
 *
 * @author gui
 */
public class gerenciarEstoque extends javax.swing.JFrame {

    // --- ATRIBUTOS E LOGS ---
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(gerenciarEstoque.class.getName());
    private int idSelecionado;
    private String nomeAtual;

    /**
     * CONSTRUTOR: Configura a tela com os dados do produto clicado
     */
    public gerenciarEstoque(int id, String nome) {
        this.idSelecionado = id;
        this.nomeAtual = nome;

        initComponents(); // Carrega o desenho da tela (Swing)
        setLocationRelativeTo(null); // Centraliza a tela
        // Atualiza o título visual com o nome do produto selecionado
        jLabel1.setText("GERENCIAR: " + nome.toUpperCase());
    }

// --- LÓGICA DE EVENTOS (BOTÕES) ---
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 243, 215));
        jPanel1.setPreferredSize(new java.awt.Dimension(1529, 873));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 48)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(124, 0, 0));
        jLabel1.setText("GERENCIAR ESTOQUE");

        jButton1.setBackground(new java.awt.Color(153, 102, 255));
        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("EDITAR NOME");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(102, 102, 255));
        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("EDITAR PREÇO");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(255, 153, 102));
        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("EDITAR QUANTIDADE");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton5.setBackground(new java.awt.Color(0, 51, 0));
        jButton5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton5.setForeground(new java.awt.Color(255, 255, 255));
        jButton5.setText("VOLTAR");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton4.setBackground(new java.awt.Color(51, 0, 0));
        jButton4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton4.setForeground(new java.awt.Color(255, 255, 255));
        jButton4.setText("INATIVAR PRODUTO");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(342, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(0, 307, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton2)
                .addGap(36, 36, 36)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(35, 35, 35)
                .addComponent(jButton1)
                .addGap(241, 241, 241))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(479, 479, 479))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel1)
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3)
                    .addComponent(jButton2)
                    .addComponent(jButton1))
                .addGap(18, 18, 18)
                .addComponent(jButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 55, Short.MAX_VALUE)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 1141, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    /**
     * Botão 2: EDITAR PREÇO
     */
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        String input = javax.swing.JOptionPane.showInputDialog(this, "Digite o novo preço para " + nomeAtual + ":");

        if (input != null && !input.isEmpty()) {
            try {
                double novoPreco = Double.parseDouble(input.replace(",", ".")); // Converte vírgula em ponto para evitar erro de decimal
                ProdutoDAO dao = new ProdutoDAO();
                if (dao.atualizarPreco(idSelecionado, novoPreco)) {
                    javax.swing.JOptionPane.showMessageDialog(this, "Preço atualizado!");
                }
            } catch (NumberFormatException e) {
                javax.swing.JOptionPane.showMessageDialog(this, "Erro: Digite um valor numérico válido.");
            }
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    
    /**
     * Botão 3: EDITAR QUANTIDADE (Correção de estoque)
     */
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        String input = javax.swing.JOptionPane.showInputDialog(this, "Nova quantidade física no estoque:");

        if (input != null) {
            try {
                int novaQtd = Integer.parseInt(input);
                ProdutoDAO dao = new ProdutoDAO();
                // idSelecionado garante que estamos mexendo no item selecionado na tabela
                if (dao.atualizarQuantidadePorId(idSelecionado, novaQtd)) {
                    javax.swing.JOptionPane.showMessageDialog(this, "Estoque de " + nomeAtual + " corrigido!");
                }
            } catch (NumberFormatException e) {
                javax.swing.JOptionPane.showMessageDialog(this, "Erro: Digite um número inteiro.");
            }
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    
    /**
     * Botão 1: EDITAR NOME
     */
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
// Abre o campo de texto já com o nome que veio do banco
        String novoNome = javax.swing.JOptionPane.showInputDialog(this, "Editar nome do produto:", nomeAtual);

        if (novoNome != null && !novoNome.trim().isEmpty()) {
            ProdutoDAO dao = new ProdutoDAO();
            //idSelecionado é o ID REAL que veio da sua jTable1
            if (dao.atualizarNome(idSelecionado, novoNome)) {
                javax.swing.JOptionPane.showMessageDialog(this, "Nome atualizado com sucesso!");
                this.nomeAtual = novoNome; // Atualiza a variável da tela
                jLabel1.setText("GERENCIAR: " + novoNome.toUpperCase());
            }
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    
    /**
     * Botão 5: VOLTAR (Fecha apenas a tela atual)
     */
    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        dispose();
    }//GEN-LAST:event_jButton5ActionPerformed

    
    /**
     * Botão 4: INATIVAR PRODUTO (Exclusão lógica)
     */
    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        int resposta = javax.swing.JOptionPane.showConfirmDialog(this,
                "Deseja realmente inativar o produto: " + nomeAtual + "?",
                "Confirmação", javax.swing.JOptionPane.YES_NO_OPTION);

        if (resposta == javax.swing.JOptionPane.YES_OPTION) {
            if (new ProdutoDAO().inativarProduto(idSelecionado)) {
                javax.swing.JOptionPane.showMessageDialog(this, "Produto inativado!");
                dispose(); // Fecha a tela de gerenciar
            }
        }
    }//GEN-LAST:event_jButton4ActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new gerenciarEstoque(1, "Produto Teste").setVisible(true));
    }
// --- MÉTODOS GERADOS AUTOMATICAMENTE PELO NETBEANS ---
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
