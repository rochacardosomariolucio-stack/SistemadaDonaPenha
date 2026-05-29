/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package UsuarioLogado;

/**
 *
 * @author guilh
 */
public class SessaoUsuario {
    private static String nomeUsuarioLogado;

    // Métodos estáticos para acessar a variável
    public static void setUsuarioLogado(String nome) {
        nomeUsuarioLogado = nome;
    }

    public static String getUsuarioLogado() {
        return nomeUsuarioLogado;
    }
}
