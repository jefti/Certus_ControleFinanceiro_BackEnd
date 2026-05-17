package com.projeto.financeiro.service;


public interface EmailProvider {

    void enviarCodigoRecuperacao(String emailDestino, String codigo);
}
