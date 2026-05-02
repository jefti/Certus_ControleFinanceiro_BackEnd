package com.projeto.financeiro.service;

/*
 * Criada para tornar explícito o contrato de escopo por usuário,
 * corrigindo a violação de LSP que existia quando {@code TituloService}
 * e {@code CentroDeCustoService} implementavam {@code CrudService} diretamente
 * mas retornavam dados filtrados, enquanto {@code UsuarioService} retornava todos.
 */
public interface UserScopedCrudService<Request, Response> extends CrudService<Request, Response> {
}
