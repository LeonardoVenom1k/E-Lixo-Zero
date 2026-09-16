package br.fai.lds.e_lixo_zero.ports_and_adapters.port.dao.crud;

import java.util.List;

public interface ReadDao<T> {
    T readById(final int id);

    List<T> readAll();
}
