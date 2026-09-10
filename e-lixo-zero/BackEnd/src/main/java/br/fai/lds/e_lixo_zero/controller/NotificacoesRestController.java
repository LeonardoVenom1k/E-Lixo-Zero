package br.fai.lds.e_lixo_zero.controller;

import br.fai.lds.e_lixo_zero.domain.NotificacaoModel;
import br.fai.lds.e_lixo_zero.domain.UserModel;
import br.fai.lds.e_lixo_zero.exceptions.ResourceNotFoundException;
import br.fai.lds.e_lixo_zero.exceptions.UnauthorizedException;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.service.notificacao.NotificacaoService;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.service.usuario.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/notificacoes")
public class NotificacoesRestController {

    @Autowired
    private NotificacaoService notificacaoService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<NotificacaoModel>> getAll(final HttpServletRequest request) {
        final UserModel usuario = getUsuario(request);
        return ResponseEntity.ok(notificacaoService.findByUsuarioId(usuario.getId()));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<NotificacaoModel>> getByUsuario(@PathVariable final int usuarioId) {
        return ResponseEntity.ok(notificacaoService.findByUsuarioId(usuarioId));
    }

    @GetMapping("/usuario/{usuarioId}/nao-lidas")
    public ResponseEntity<List<NotificacaoModel>> getNaoLidasByUsuario(@PathVariable final int usuarioId) {
        return ResponseEntity.ok(notificacaoService.findNaoLidasByUsuarioId(usuarioId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificacaoModel> getById(@PathVariable final int id) {
        final NotificacaoModel notificacao = notificacaoService.findById(id);
        if (notificacao == null) {
            throw new ResourceNotFoundException("Notificação não encontrada");
        }
        return ResponseEntity.ok(notificacao);
    }

    @PostMapping
    public ResponseEntity<NotificacaoModel> create(@RequestBody final NotificacaoModel notificacao) {
        final int id = notificacaoService.create(notificacao);
        if (id == 0) {
            throw new ResourceNotFoundException("Erro ao criar notificação");
        }
        return ResponseEntity.ok(notificacaoService.findById(id));
    }

    @PutMapping("/{id}/marcar-lida")
    public ResponseEntity<Void> marcarComoLida(@PathVariable final int id) {
        final boolean updated = notificacaoService.marcarComoLida(id);
        return updated ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final int id) {
        notificacaoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private UserModel getUsuario(final HttpServletRequest request) {
        final String email = (String) request.getAttribute("email");
        if (email == null || email.isBlank()) {
            throw new UnauthorizedException("Usuário não autenticado");
        }
        final UserModel usuario = usuarioService.findByEmail(email);
        if (usuario == null) {
            throw new UnauthorizedException("Usuário não encontrado");
        }
        return usuario;
    }
}
