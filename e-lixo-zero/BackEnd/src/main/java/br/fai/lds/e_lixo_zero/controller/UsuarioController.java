package br.fai.lds.e_lixo_zero.controller;

import br.fai.lds.e_lixo_zero.domain.UserModel;
import br.fai.lds.e_lixo_zero.dto.LoginRequestDto;
import br.fai.lds.e_lixo_zero.dto.LoginResponseDto;
import br.fai.lds.e_lixo_zero.exceptions.UnauthorizedException;
import br.fai.lds.e_lixo_zero.exceptions.ResourceNotFoundException;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.service.usuario.UsuarioService;
import br.fai.lds.e_lixo_zero.security.JwtTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtTokenService jwtTokenService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @GetMapping
    public ResponseEntity<List<UserModel>> getAll() {
        return ResponseEntity.ok(usuarioService.findALl());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserModel> getById(@PathVariable final int id) {
        final UserModel usuario = usuarioService.findById(id);
        if (usuario == null) {
            throw new ResourceNotFoundException("Usuário não encontrado");
        }
        return ResponseEntity.ok(usuario);
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody final UserModel userModel) {
        final int id = usuarioService.create(userModel);
        if (id == 0) {
            return ResponseEntity.badRequest().build();
        }
        final URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable final int id, @RequestBody final UserModel userModel) {
        final boolean updated = usuarioService.update(id, userModel);
        return updated ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final int id) {
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody final LoginRequestDto loginRequest) {
        final UserModel usuario = usuarioService.findByEmail(loginRequest.getEmail());
        if (usuario == null || !passwordEncoder.matches(loginRequest.getSenha(), usuario.getSenha())) {
            throw new UnauthorizedException("E-mail ou senha inválidos");
        }

        final String token = jwtTokenService.generateToken(usuario.getEmail());
        final LoginResponseDto response = toLoginResponse(usuario, token);
        return ResponseEntity.ok(response);
    }

    private LoginResponseDto toLoginResponse(final UserModel usuario, final String token) {
        final LoginResponseDto response = new LoginResponseDto();
        response.setId(usuario.getId());
        response.setNomeCompleto(usuario.getNomeCompleto());
        response.setEmail(usuario.getEmail());
        response.setCpf(usuario.getCpf());
        response.setTelefone(usuario.getTelefone());
        response.setLogradouro(usuario.getLogradouro());
        response.setNumero(usuario.getNumero());
        response.setBairro(usuario.getBairro());
        response.setCidade(usuario.getCidade());
        response.setEstado(usuario.getEstado());
        response.setTipoUsuario(usuario.getTipoUsuario());
        response.setToken(token);
        return response;
    }
}
