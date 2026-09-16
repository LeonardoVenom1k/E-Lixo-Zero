package br.fai.lds.e_lixo_zero.ports_and_adapters.adapter.service.user;

import br.fai.lds.e_lixo_zero.domain.UserModel;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.dao.user.UserDao;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceAdapter implements UserService {

    @Autowired
    private UserDao userDao;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public int create(final UserModel userModel) {
        if (userModel == null) {
            return 0;
        }
        if (isInvalidString(userModel.getFullName())) {
            return 0;
        }
        if (isInvalidEmail(userModel.getEmail())) {
            return 0;
        }
        if (isInvalidString(userModel.getPassword())) {
            return 0;
        }
        if (findByEmail(userModel.getEmail()) != null) {
            return 0;
        }

        setDefaults(userModel);
        userModel.setPassword(passwordEncoder.encode(userModel.getPassword()));
        return userDao.add(userModel);
    }

    private void setDefaults(final UserModel userModel) {
        if (isInvalidString(userModel.getCity())) {
            userModel.setCity("Santa Rita do Sapucaí");
        }
        if (isInvalidString(userModel.getState())) {
            userModel.setState("MG");
        }
        if (isInvalidString(userModel.getUserType())) {
            userModel.setUserType("CITIZEN");
        }
        userModel.setActive(true);
    }

    @Override
    public void delete(final int id) {
        if (id <= 0) {
            return;
        }
        userDao.remove(id);
    }

    @Override
    public boolean update(final int id, final UserModel userModel) {
        final UserModel stored = findById(id);
        if (stored == null || userModel == null) {
            return false;
        }
        userDao.updateInformation(id, userModel);
        return true;
    }

    @Override
    public UserModel findById(final int id) {
        if (id <= 0) {
            return null;
        }
        return userDao.readById(id);
    }

    @Override
    public List<UserModel> findAll() {
        return userDao.readAll();
    }

    @Override
    public UserModel findByEmail(final String email) {
        if (isInvalidEmail(email)) {
            return null;
        }
        return userDao.readByEmail(email);
    }

    private boolean isInvalidEmail(final String email) {
        return email == null || email.isBlank() || !email.contains("@");
    }

    private boolean isInvalidString(final String value) {
        return value == null || value.isBlank();
    }
}
