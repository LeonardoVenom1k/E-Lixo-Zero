package br.fai.lds.e_lixo_zero.ports_and_adapters.adapter.dao.user;

import br.fai.lds.e_lixo_zero.domain.UserModel;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.dao.user.UserDao;

import java.util.ArrayList;
import java.util.List;

public class UserFakeDaoAdapter implements UserDao {

    private static final List<UserModel> USERS = new ArrayList<>();
    private static int nextId = 1;

    static {
        final UserModel user = new UserModel();
        user.setId(nextId++);
        user.setFullName("João Silva");
        user.setEmail("joao@gmail.com");
        user.setPassword("123456");
        user.setCpf("12345678900");
        user.setPhone("(35) 99999-0001");
        user.setStreet("Rua das Flores");
        user.setNumber("123");
        user.setNeighborhood("Centro");
        user.setCity("Santa Rita do Sapucaí");
        user.setState("MG");
        user.setUserType("CITIZEN");
        user.setActive(true);
        USERS.add(user);
    }

    @Override
    public int add(final UserModel entity) {
        if (entity == null) {
            return 0;
        }
        entity.setId(nextId++);
        USERS.add(entity);
        return entity.getId();
    }

    @Override
    public void remove(final int id) {
        USERS.removeIf(u -> u.getId() == id);
    }

    @Override
    public UserModel readById(final int id) {
        return USERS.stream()
                .filter(u -> u.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<UserModel> readAll() {
        return new ArrayList<>(USERS);
    }

    @Override
    public void updateInformation(final int id, final UserModel entity) {
        final UserModel stored = readById(id);
        if (stored == null) {
            return;
        }
        stored.setFullName(entity.getFullName());
        stored.setCpf(entity.getCpf());
        stored.setPhone(entity.getPhone());
        stored.setStreet(entity.getStreet());
        stored.setNumber(entity.getNumber());
        stored.setNeighborhood(entity.getNeighborhood());
        stored.setCity(entity.getCity());
        stored.setState(entity.getState());
        stored.setUserType(entity.getUserType());
        stored.setActive(entity.isActive());
    }

    @Override
    public UserModel readByEmail(final String email) {
        if (email == null || email.isBlank()) {
            return null;
        }
        return USERS.stream()
                .filter(u -> u.getEmail() != null && u.getEmail().equalsIgnoreCase(email.trim()))
                .findFirst()
                .orElse(null);
    }
}
