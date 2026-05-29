package Rounded;

import javax.swing.Icon;
import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicTextFieldUI;

public class RoundedTextField extends JTextField {

    private Shape shape;
    private int cornerRadius = 20; // Raio dos cantos. Ajuste este valor.

    public RoundedTextField(int size) {
        super(size);
        setOpaque(false);
        
        setSelectionColor(new Color(0, 120, 215)); // Define a cor do texto quando está selecionado (geralmente branco para contraste)
        setSelectedTextColor(Color.WHITE);// Torna o fundo transparente
        
        // Define o UI (User Interface) para desenhar o texto corretamente
        this.setUI(new BasicTextFieldUI()); 
        
        // Define uma borda vazia para que o texto não toque nos cantos
        Border empty = new Border() {
            @Override
            public void paintBorder(java.awt.Component c, Graphics g, int x, int y, int width, int height) {}

            @Override
            public Insets getBorderInsets(java.awt.Component c) {
                // Adiciona espaço interno para o texto (padding)
                return new Insets(5, cornerRadius / 2, 5, cornerRadius / 2);
            }

            @Override
            public boolean isBorderOpaque() {
                return false;
            }
        };
        setBorder(empty);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Cor de fundo do campo (a parte branca interna)
        g2.setColor(getBackground()); 
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
        
        // Chama o método da super classe para desenhar o texto e o caret
        super.paintComponent(g); 
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Cor da borda
        g2.setColor(getForeground()); // Usando foreground para a cor da borda
        g2.setStroke(new BasicStroke(1)); // Espessura da borda
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
    }
    
    // Sobrescreve a forma para que os eventos de clique e foco funcionem
    @Override
    public boolean contains(int x, int y) {
        if (shape == null || !shape.getBounds().equals(getBounds())) {
            shape = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        }
        return shape.contains(x, y);
    }
}