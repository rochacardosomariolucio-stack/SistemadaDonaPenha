package Main;

// 1. Importa a classe da tela de login
import TelaLogin.telalogin; // Ajuste o nome do pacote 'seuprojeto.telaLogin'
import TelaPrincipal.TelaPrincipal;

public class main {
    
    /**
     * O ponto de entrada principal do programa.
     * @param args argumentos de linha de comando
     */
    public static void main(String[] args) {
        // Cria uma nova instância da tela de login
        //telalogin login = new telalogin();
        // Torna a tela visível para o usuário
        //login.setVisible(true);
        // login.setLocationRelativeTo(null); 
        
        
        TelaPrincipal tprincipal = new TelaPrincipal();
        tprincipal.setVisible(true);
    }
}