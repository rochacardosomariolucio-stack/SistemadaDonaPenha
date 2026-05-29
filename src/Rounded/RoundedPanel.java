package Rounded; 

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

public class RoundedPanel extends JPanel {
    private int cornerRadius = 30; 

    public RoundedPanel() {
        super();
        setOpaque(false); // ISTO É CRUCIAL!
    }

    @Override
    protected void paintComponent(Graphics g) {
        // ESSENCIAL: CHAMA O MÉTODO DA SUPER CLASSE PRIMEIRO
        // super.paintComponent(g); // Com setOpaque(false), pode-se remover, mas é boa prática manter ou omitir.

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // COR DO FUNDO
        // Se você quer o painel BRANCO (255, 255, 255), mantenha-o em getBackground()
        g2.setColor(getBackground()); 
        
        // Desenha o retângulo arredondado. O "-1" garante que a borda cabe dentro.
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
        
        // Opcional: Borda fina (remova se não quiser borda)
        //g2.setColor(getForeground()); 
        //g2.setStroke(new BasicStroke(1));
        //g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
        
        // É importante chamar o super.paintComponent(g) após desenhar, 
        // ou no início, se não tiver certeza. Vamos mantê-lo omitido se setOpaque(false)
        // está funcionando, para que o fundo padrão não seja desenhado.
    }
}