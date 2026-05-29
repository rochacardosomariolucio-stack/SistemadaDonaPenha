package TelaVenda;

import Rounded.RoundedPanel;
import TelaVendaConcluida.telaVendaConcluida;
import UsuarioLogado.SessaoUsuario;
import dao.ProdutoDAO;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.SpinnerNumberModel;
import dao.FornecedorDAO;
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
 * @author ALUNO
 */
public class telaVendas extends javax.swing.JFrame {

    private final ProdutoDAO produtoDao = new ProdutoDAO();
    private final DecimalFormat df = new DecimalFormat("R$#,##0.00");
    private final FornecedorDAO fornecedorDao = new FornecedorDAO();

    private void calcularCustoTotal() {
        try {
            // 1. Obter Produto Selecionado
            String produtoSelecionado = (String) txtprodutoVendido.getSelectedItem();

            // Se não houver produto selecionado, limpa os campos e sai
            if (produtoSelecionado == null || produtoSelecionado.trim().isEmpty()) {
                jTextField1.setText(""); // Limpa Fornecedor
                txtcustoTotal.setText("R$0,00");
                jTextField2.setText(""); // Limpa o ID
                return;
            }

            // NOVO: 1b. Buscar e Atualizar o ID no jTextField2
            int idProduto = produtoDao.buscarIdProdutoPorNome(produtoSelecionado);
            if (idProduto > 0) {
                jTextField2.setText(String.valueOf(idProduto)); // Converte o ID inteiro para String
            } else {
                jTextField2.setText("ID Inválido"); // Ou limpa, se preferir
            }

            // 2. Buscar Fornecedor (Lógica existente)
            String fornecedor = fornecedorDao.buscarNomeFornecedor(produtoSelecionado);

            if (fornecedor != null && !fornecedor.trim().isEmpty()) {
                jTextField1.setText(fornecedor);
            } else {
                jTextField1.setText("Não Encontrado");
            }
            jTextField1.setEditable(false);
            // -----------------------------------------------------------

            // 3. Obter Quantidade (Lógica existente)
            int quantidade = ((Integer) txtqtdVendida.getValue()).intValue();

            // 4. Obter Preço Unitário (Lógica existente)
            double precoUnitario = produtoDao.buscarPrecoProduto(produtoSelecionado);

            // 5. Calcular Total (Lógica existente)
            double custoTotal = 0.0;
            if (precoUnitario > 0.0 && quantidade > 0) {
                custoTotal = precoUnitario * quantidade;
            }

            // 6. Atualizar o campo Custo Total formatado
            txtcustoTotal.setText(df.format(custoTotal));

        } catch (Exception e) {
            // Em caso de erro (ex: valor inválido no JSpinner), zera os campos
            txtcustoTotal.setText("R$0,00");
            jTextField1.setText("Erro de Carga");
            jTextField2.setText("Erro");
            // e.printStackTrace(); // Descomente para debug
        }
    }

    /**
     * Creates new form Principal
     */
    public telaVendas() {

        initComponents();
        setExtendedState(MAXIMIZED_BOTH);
        txtcustoTotal.setEditable(false);
        txtdtVenda.setEditable(false);
        preencherComboBoxProdutos();
        calcularCustoTotal();
        configurarAtalhoEsc();

        this.getRootPane().setDefaultButton(jButton2);
        jTextField2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                // Verifica se a tecla apertada foi o ENTER
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    // Chama o método usando o texto do campo ID (jTextField2)
                    pesquisarProdutoPorId(jTextField2.getText().trim());
                    evt.consume(); // Consome o evento para evitar que o ENTER faça algo mais
                }
            }
        });

// Obtém a referência para o campo de texto interno do JComboBox
        javax.swing.JTextField editor = (javax.swing.JTextField) txtprodutoVendido.getEditor().getEditorComponent();

// Adiciona o KeyListener
        editor.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                // Verifica se a tecla apertada foi o ENTER
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    // ESTE MÉTODO É O QUE FAZ O PRODUTO "APARECER"
                    pesquisarProdutoPorId(editor.getText().trim());
                    evt.consume();
                }
            }
        });

        //Valor Inicial, Valor Minimo, Valor Maximo, Quanto muda a cada clique
        SpinnerNumberModel modelo = new SpinnerNumberModel(0, 0, 10000, 1);
        // Verifique se o nome do seu JSpinner realmente é 'spinnerQuantidade' na tela de Vendas
        txtqtdVendida.setModel(modelo);

        LocalDate hoje = LocalDate.now();
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dataFormatada = hoje.format(formatador);
        txtdtVenda.setText(dataFormatada);

        // CONFIGURAÇÃO DA MÁSCARA DA DATA (Seu código original)
        try {
            if (txtdtVenda.getFormatter() instanceof javax.swing.text.MaskFormatter) {
                javax.swing.text.MaskFormatter maskFormatter = (javax.swing.text.MaskFormatter) txtdtVenda.getFormatter();
                maskFormatter.setPlaceholderCharacter('_');
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ====================================================================
        // NOVO CÓDIGO PARA FUNCIONALIDADE DE CÁLCULO
        // 1. Ouvinte para o JComboBox (Produto Vendido)
        txtprodutoVendido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calcularCustoTotal();
            }
        });

        // 2. Ouvinte para o JSpinner (Quantidade Vendida)
        txtqtdVendida.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                calcularCustoTotal();
            }
        });

        // Inicializa o valor total no carregamento da tela
        calcularCustoTotal();
        // ====================================================================
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")

    private void pesquisarProdutoPorId(String idText) {
        if (idText.isEmpty()) {
            return;
        }

        try {
            // Tenta converter o texto digitado (o ID) para um número inteiro
            int idProduto = Integer.parseInt(idText);

            // 1. Chama o DAO para buscar o produto pelo ID
            Map<String, Object> dados = produtoDao.buscarProdutoPorId(idProduto);

            if (dados.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Produto com ID " + idProduto + " não encontrado ou inativo.",
                        "Erro de Produto", JOptionPane.ERROR_MESSAGE);

                // Se deu erro, limpa APENAS o campo de ID (jTextField2)
                jTextField2.setText("");

                // Opcional: Limpa também a seleção do produto e recalcula o custo (deve zerar)
                txtprodutoVendido.setSelectedItem(null);
                calcularCustoTotal();

            } else {
                // 2. Seleciona o item no JComboBox pelo NOME encontrado.
                String nomeProduto = (String) dados.get("nome");
                String nomeFornecedor = (String) dados.get("fornecedor");

                // Esta linha seleciona o item e faz o NOME do produto aparecer no JComboBox
                txtprodutoVendido.setSelectedItem(nomeProduto);

                // ⚠️ IMPORTANTE: A linha que forçava o ID de volta no ComboBox FOI REMOVIDA.
                // O campo de ID (jTextField2) VAI MANTER O ID digitado.
                // 3. Atualiza o Fornecedor e recalcula o total
                jTextField1.setText(nomeFornecedor); // Campo do Fornecedor

                // Chama seu método existente que atualiza o preço e o total
                calcularCustoTotal();
            }

        } catch (NumberFormatException e) {
            // Se o que foi digitado não é um número.
            // O campo ID (jTextField2) manterá o texto inválido para visualização.
            // Você pode limpar aqui se preferir: jTextField2.setText("");
        }
    }

    private void preencherComboBoxProdutos() {
        // Limpa os itens estáticos que o NetBeans pode ter gerado
        txtprodutoVendido.removeAllItems();

        // Busca a lista de nomes de produtos usando o DAO
        java.util.List<String> produtos = produtoDao.buscarNomesProdutos();

        // Adiciona os produtos buscados do DB ao ComboBox
        for (String produto : produtos) {
            txtprodutoVendido.addItem(produto);
        }
    }
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new RoundedPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtprodutoVendido = new javax.swing.JComboBox<>();
        txtqtdVendida = new javax.swing.JSpinner();
        txtcustoTotal = new javax.swing.JTextField();
        txtformaPagamento = new javax.swing.JComboBox<>();
        txtdtVenda = new javax.swing.JFormattedTextField();
        jLabel11 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        jLabel4.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        jLabel4.setText("Data de Compra:");

        jLabel3.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        jLabel3.setText("Custo Total (R$):");

        jLabel5.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        jLabel5.setText("Forma de Pagamento:");

        jLabel2.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        jLabel2.setText("Quantidade Comprada:");

        jLabel1.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        jLabel1.setText("Produto:");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(255, 229, 196));

        jPanel1.setBackground(new java.awt.Color(255, 243, 215));
        jPanel1.setMaximumSize(new java.awt.Dimension(1500, 808));
        jPanel1.setMinimumSize(new java.awt.Dimension(850, 650));
        jPanel1.setName("principalPanel"); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(1500, 808));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setMaximumSize(new java.awt.Dimension(1500, 808));

        jLabel6.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel6.setText("Produto Vendido:");

        jLabel7.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel7.setText("Custo Total (R$):");

        jLabel8.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel8.setText("Quantidade Vendida: ");

        jLabel9.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel9.setText("Data da Venda:");

        jLabel10.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel10.setText("Forma de Pagamento:");

        txtprodutoVendido.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] {
            "Amstel Latão", "Brahma Latão", "Heineken Latão", "Coca Cola Lata Sabor Orig.", "Coca Cola 2L Sabor Orig.", "Coca Cola 2L zero açúcar.", "Guaraná Lata", "Guaraná 2L Sabor Orig.", "Guaraná 2L Zero Açúcar", "Sacolé" }));
txtprodutoVendido.setToolTipText("Selecione o produto para vender.");
txtprodutoVendido.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
        txtprodutoVendidoActionPerformed(evt);
    }
    });

    txtqtdVendida.setToolTipText("Selecione a quantidade vendida.");

    txtcustoTotal.setText("R$00,00");
    txtcustoTotal.setToolTipText("Custo total gerado automaticamente.");
    txtcustoTotal.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            txtcustoTotalActionPerformed(evt);
        }
    });

    txtformaPagamento.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pix", "Dinheiro",}));
    txtformaPagamento.setToolTipText("Selecione a forma de pagamento.");

    try {
        txtdtVenda.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##/##/####")));
    } catch (java.text.ParseException ex) {
        ex.printStackTrace();
    }
    txtdtVenda.setToolTipText("A data é gerada automaticamente.");
    txtdtVenda.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            txtdtVendaActionPerformed(evt);
        }
    });

    jLabel11.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
    jLabel11.setText("Fornecedor:");

    jTextField1.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jTextField1ActionPerformed(evt);
        }
    });

    jLabel12.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
    jLabel12.setText("Id Produto Vendido:");

    jTextField2.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jTextField2ActionPerformed(evt);
        }
    });

    javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(
        jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
            .addContainerGap(102, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jLabel6)
                .addComponent(jLabel8)
                .addComponent(jLabel7)
                .addComponent(jLabel10)
                .addComponent(jLabel9)
                .addComponent(jLabel11)
                .addComponent(jLabel12))
            .addGap(29, 29, 29)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 367, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtqtdVendida)
                    .addComponent(txtcustoTotal)
                    .addComponent(txtformaPagamento, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtprodutoVendido, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtdtVenda)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 367, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGap(94, 94, 94))
    );
    jPanel2Layout.setVerticalGroup(
        jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel2Layout.createSequentialGroup()
            .addGap(40, 40, 40)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel6)
                .addComponent(txtprodutoVendido, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGap(30, 30, 30)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel12)
                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel8)
                .addComponent(txtqtdVendida, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGap(48, 48, 48)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel7)
                .addComponent(txtcustoTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGap(54, 54, 54)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel10)
                .addComponent(txtformaPagamento, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGap(48, 48, 48)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel9)
                .addComponent(txtdtVenda, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGap(44, 44, 44)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel11)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGap(28, 28, 28))
    );

    jLabel13.setFont(new java.awt.Font("Arial", 1, 48)); // NOI18N
    jLabel13.setForeground(new java.awt.Color(138, 31, 31));
    jLabel13.setText("Registro de Vendas");

    jButton1.setBackground(new java.awt.Color(123, 19, 26));
    jButton1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
    jButton1.setForeground(new java.awt.Color(255, 255, 255));
    jButton1.setText("Cancelar");
    jButton1.setToolTipText("Cancele a venda.");
    jButton1.setMaximumSize(new java.awt.Dimension(182, 49));
    jButton1.setMinimumSize(new java.awt.Dimension(182, 49));
    jButton1.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButton1ActionPerformed(evt);
        }
    });

    jButton2.setBackground(new java.awt.Color(51, 153, 0));
    jButton2.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
    jButton2.setForeground(new java.awt.Color(255, 255, 255));
    jButton2.setText("Registrar Venda");
    jButton2.setToolTipText("Registre a venda.");
    jButton2.setMaximumSize(new java.awt.Dimension(182, 49));
    jButton2.setMinimumSize(new java.awt.Dimension(182, 49));
    jButton2.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButton2ActionPerformed(evt);
        }
    });

    jButton3.setBackground(new java.awt.Color(51, 102, 255));
    jButton3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
    jButton3.setForeground(new java.awt.Color(255, 255, 255));
    jButton3.setText("RELATORIO DE VENDAS");
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
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(169, 169, 169)
            .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(0, 0, Short.MAX_VALUE))
        .addGroup(jPanel1Layout.createSequentialGroup()
            .addGap(0, 359, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(51, 51, 51)
            .addComponent(jButton3)
            .addContainerGap(64, Short.MAX_VALUE))
        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
            .addGap(0, 0, Short.MAX_VALUE)
            .addComponent(jLabel13)
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    jPanel1Layout.setVerticalGroup(
        jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel1Layout.createSequentialGroup()
            .addContainerGap(25, Short.MAX_VALUE)
            .addComponent(jLabel13)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(12, 12, 12)
                    .addComponent(jButton3)))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addContainerGap(37, Short.MAX_VALUE))
    );

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    );
    layout.setVerticalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 853, Short.MAX_VALUE)
    );

    jPanel1.getAccessibleContext().setAccessibleName("");

    pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtcustoTotalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtcustoTotalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtcustoTotalActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        int quantidadeVendida = ((Integer) txtqtdVendida.getValue()).intValue();

        if (quantidadeVendida <= 0) {
            javax.swing.JOptionPane.showMessageDialog(this, "Insira a quantidade vendida para continuar", "Erro de Quantidade", javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 1. Obter os dados da venda para salvar
        String produtoVendido = (String) txtprodutoVendido.getSelectedItem();
        String custoTotalStr = txtcustoTotal.getText().replace("R$", "").replace(".", "").replace(",", ".");
        double valorTotal = Double.parseDouble(custoTotalStr);
        String formaPagamento = (String) txtformaPagamento.getSelectedItem();
        String dataVenda = txtdtVenda.getText();

        // 🚨 NOVO: Pega o nome de quem logou para registrar no histórico
        String vendedorAtual = SessaoUsuario.getUsuarioLogado();

        // 🚨 ATUALIZADO: Passando o 'vendedorAtual' como o 5º parâmetro
        boolean vendaSalva = produtoDao.salvarVendaNoHistorico(
                produtoVendido, // 1. String
                quantidadeVendida, // 2. int
                valorTotal, // 3. double
                formaPagamento, // 4. String
                vendedorAtual // 5. String (O novo parâmetro)
        );

        if (!vendaSalva) {
            javax.swing.JOptionPane.showMessageDialog(this, "Erro ao salvar histórico de venda no banco!", "Erro de DB", javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 3. DESCONTAR O ESTOQUE 
        boolean estoqueAtualizado = produtoDao.descontarEstoque(produtoVendido, quantidadeVendida);

        if (estoqueAtualizado) {
            int idProd = produtoDao.buscarIdProdutoPorNome(produtoVendido);
            produtoDao.registrarNoExtrato(idProd, "SAÍDA", quantidadeVendida, "Venda Realizada", vendedorAtual);
            this.dispose();
            telaVendaConcluida concluida = new telaVendaConcluida(dataVenda, txtcustoTotal.getText(), formaPagamento);
            concluida.setVisible(true);
        } else {
            javax.swing.JOptionPane.showMessageDialog(null, "Erro ao atualizar estoque!", "Erro", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void txtdtVendaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtdtVendaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtdtVendaActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void txtprodutoVendidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtprodutoVendidoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtprodutoVendidoActionPerformed

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        relatorioDeVendas rvenda = new relatorioDeVendas();
        rvenda.setVisible(true);
    }//GEN-LAST:event_jButton3ActionPerformed

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
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(telaVendas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(telaVendas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(telaVendas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(telaVendas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new telaVendas().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField txtcustoTotal;
    private javax.swing.JFormattedTextField txtdtVenda;
    private javax.swing.JComboBox<String> txtformaPagamento;
    private javax.swing.JComboBox<String> txtprodutoVendido;
    private javax.swing.JSpinner txtqtdVendida;
    // End of variables declaration//GEN-END:variables
}
