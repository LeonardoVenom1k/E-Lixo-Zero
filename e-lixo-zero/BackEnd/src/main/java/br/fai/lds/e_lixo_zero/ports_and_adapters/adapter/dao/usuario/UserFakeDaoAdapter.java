package br.fai.lds.e_lixo_zero.ports_and_adapters.adapter.dao.usuario;

import br.fai.lds.e_lixo_zero.domain.UserModel;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.dao.usuario.UserDao;

import java.util.ArrayList;
import java.util.List;

public class UserFakeDaoAdapter implements UserDao {

    private static final List<UserModel> USUARIOS = new ArrayList<>();
    private static int nextId = 1;

    static {
        final UserModel usuario = new UserModel();
        usuario.setId(nextId++);
        usuario.setNomeCompleto("João Silva");
        usuario.setEmail("joao@gmail.com");
        usuario.setSenha("123456");
        usuario.setCpf("12345678900");
        usuario.setTelefone("(35) 99999-0001");
        usuario.setLogradouro("Rua das Flores");
        usuario.setNumero("123");
        usuario.setBairro("Centro");
        usuario.setCidade("Santa Rita do Sapucaí");
        usuario.setEstado("MG");
        usuario.setTipoUsuario("CIDADAO");
        usuario.setAtivo(true);
        USUARIOS.add(usuario);
    }

    @Override
    public int add(final UserModel entity) {
        if (entity == null) {
            return 0;
        }
        entity.setId(nextId++);
        USUARIOS.add(entity);
        return entity.getId();
    }

    @Override
    public void remove(final int id) {
        USUARIOS.removeIf(u -> u.getId() == id);
    }

    @Override
    public UserModel readyById(final int id) {
        return USUARIOS.stream()
                .filter(u -> u.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<UserModel> readAll() {
        return new ArrayList<>(USUARIOS);
    }

    @Override
    public void updateInformation(final int id, final UserModel entity) {
        final UserModel stored = readyById(id);
        if (stored == null) {
            return;
        }
        stored.setNomeCompleto(entity.getNomeCompleto());
        stored.setCpf(entity.getCpf());
        stored.setTelefone(entity.getTelefone());
        stored.setLogradouro(entity.getLogradouro());
        stored.setNumero(entity.getNumero());
        stored.setBairro(entity.getBairro());
        stored.setCidade(entity.getCidade());
        stored.setEstado(entity.getEstado());
        stored.setTipoUsuario(entity.getTipoUsuario());
        stored.setAtivo(entity.isAtivo());
    }

    @Override
    public UserModel readByEmail(final String email) {
        if (email == null || email.isBlank()) {
            return null;
        }
        return USUARIOS.stream()
                .filter(u -> u.getEmail() != null && u.getEmail().equalsIgnoreCase(email.trim()))
                .findFirst()
                .orElse(null);
    }
}
