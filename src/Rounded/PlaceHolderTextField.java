package Rounded; // Certifique-se que o pacote está correto

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.FontMetrics;

public class PlaceHolderTextField extends RoundedTextField { // Estende seu RoundedTextField
    
    private String placeholder = "";
    private Color placeholderColor = Color.GRAY; // Cor padrão do placeholder
    
    public PlaceHolderTextField(int columns) {
        super(columns);
    }

    public PlaceHolderTextField(String placeholder, int columns) {
        super(columns);
        this.placeholder = placeholder;
    }
    
    // Métodos para definir/obter o placeholder
    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint(); // Redesenha o componente para mostrar/esconder o placeholder
    }

    public Color getPlaceholderColor() {
        return placeholderColor;
    }

    public void setPlaceholderColor(Color placeholderColor) {
        this.placeholderColor = placeholderColor;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Desenha o fundo arredondado e a borda

        // Desenha o placeholder SOMENTE se o campo estiver vazio E não tiver foco
        if (placeholder != null && !placeholder.isEmpty() && getText().isEmpty()) { 
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(placeholderColor);
            
            FontMetrics metrics = g2.getFontMetrics(getFont());
            
            int extraPadding = 5; 
            int x = getInsets().left + extraPadding; 
            
            int y = (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();
            
            g2.drawString(placeholder, x, y);
        }
    }
}