/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Rounded;

/**
 *
 * @author guilh
 */
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JButton;
import javax.swing.border.EmptyBorder;

public class RoundedButton extends JButton {
    private int arc = 30; // Raio para arredondar as bordas

    public RoundedButton(String text, int arcRadius) {
        super(text);
        this.arc = arcRadius;
        // Torna a área de conteúdo (dentro do botão) transparente para que o fundo customizado seja visto
        setContentAreaFilled(false);
        // Remove a borda padrão para evitar conflitos
        setBorder(new EmptyBorder(10, 20, 10, 20)); 
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Define a cor de fundo (a cor que você definiu no setBackground)
        g2.setColor(getBackground());
        // Desenha o retângulo arredondado
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

        // Chama o paintComponent padrão para desenhar o texto e o ícone
        super.paintComponent(g);

        g2.dispose();
    }
}