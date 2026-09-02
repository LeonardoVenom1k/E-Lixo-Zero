package br.fai.lds.e_lixo_zero.ports_and_adapters.adapter.service.usuario;

import br.fai.lds.e_lixo_zero.domain.UserModel;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.dao.usuario.UserDao;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.service.usuario.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioServiceAdapter implements UsuarioService {

    @Autowired
    private UserDao userDao;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public int create(final UserModel userModel) {
        if (userModel == null) {
            return 0;
        }
        if (isInvalidString(userModel.getNomeCompleto())) {
            return 0;
        }
        if (isInvalidEmail(userModel.getEmail())) {
            return 0;
        }
        if (isInvalidString(userModel.getSenha())) {
            return 0;
        }
        if (findByEmail(userModel.getEmail()) != null) {
            return 0;
        }

        setDefaults(userModel);
        userModel.setSenha(passwordEncoder.encode(userModel.getSenha()));
        return userDao.add(userModel);
    }

    private void setDefaults(final UserModel userModel) {
        if (isInvalidString(userModel.getCidade())) {
            userModel.setCidade("Santa Rita do Sapucaí");
        }
        if (isInvalidString(userModel.getEstado())) {
            userModel.setEstado("MG");
        }
        if (isInvalidString(userModel.getTipoUsuario())) {
            userModel.setTipoUsuario("CIDADAO");
        }
        userModel.setAtivo(true);
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
        return userDao.readyById(id);
    }

    @Override
    public List<UserModel> findALl() {
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
