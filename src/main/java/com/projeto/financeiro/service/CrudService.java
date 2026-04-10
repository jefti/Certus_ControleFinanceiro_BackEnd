package com.projeto.financeiro.service;

import java.util.List;

public interface CrudService<Entity, Request, Response> {

    Response criar(Request dto);

    List<Response> listarTodos();


    Response buscarPorId(long id);

    Response atualizar(long id, Request dto);

    void inativar(long id);

}
