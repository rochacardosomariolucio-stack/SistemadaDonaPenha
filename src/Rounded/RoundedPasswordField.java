package Rounded;

import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JPasswordField;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicPasswordFieldUI;

public class RoundedPasswordField extends JPasswordField {

    private Shape shape;
    private int cornerRadius = 20; // Raio dos cantos. Ajuste este valor.

    public RoundedPasswordField(int size) {
        super(size);
        setOpaque(false); // Torna o fundo transparente
        
        // Define o UI para desenhar o texto (e os pontos) corretamente
        this.setUI(new BasicPasswordFieldUI()); 
        
        setSelectionColor(new Color(0, 120, 215)); 
        setSelectedTextColor(Color.GRAY);
        
        // Define uma borda vazia para que os pontos não toquem nos cantos
        Border empty = new Border() {
            @Override
            public void paintBorder(java.awt.Component c, Graphics g, int x, int y, int width, int height) {}

            @Override
            public Insets getBorderInsets(java.awt.Component c) {
                // Adiciona espaço interno (padding)
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

        // Cor de fundo do campo (a parte interna)
        g2.setColor(getBackground()); 
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
        
        // Chama o método da super classe para desenhar os caracteres ocultos
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
    
    // Sobrescreve a forma para que os eventos funcionem
    @Override
    public boolean contains(int x, int y) {
        if (shape == null || !shape.getBounds().equals(getBounds())) {
            shape = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        }
        return shape.contains(x, y);
    }
}