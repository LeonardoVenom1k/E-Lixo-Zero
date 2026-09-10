package br.fai.lds.e_lixo_zero.ports_and_adapters.port.service.crud;

import java.util.List;

public interface FindService<T> {

    T findById(final int id);

    List<T> findAll();
}
