package br.fai.lds.e_lixo_zero.controller;

import br.fai.lds.e_lixo_zero.domain.NotificacaoModel;
import br.fai.lds.e_lixo_zero.domain.SolicitacaoColetaModel;
import br.fai.lds.e_lixo_zero.domain.TipoResiduoModel;
import br.fai.lds.e_lixo_zero.domain.UserModel;
import br.fai.lds.e_lixo_zero.dto.ColetaRequestDto;
import br.fai.lds.e_lixo_zero.dto.ColetaResponseDto;
import br.fai.lds.e_lixo_zero.exceptions.BadRequestException;
import br.fai.lds.e_lixo_zero.exceptions.ResourceNotFoundException;
import br.fai.lds.e_lixo_zero.exceptions.UnauthorizedException;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.service.coleta.SolicitacaoColetaService;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.service.notificacao.NotificacaoService;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.service.residuo.TipoResiduoService;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.service.usuario.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/coletas")
public class ColetasRestController {

    @Autowired
    private SolicitacaoColetaService solicitacaoColetaService;

    @Autowired
    private TipoResiduoService tipoResiduoService;

    @Autowired
    private NotificacaoService notificacaoService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<ColetaResponseDto>> getAll(final HttpServletRequest request) {
        final UserModel usuario = getUsuario(request);
        final List<SolicitacaoColetaModel> coletas = solicitacaoColetaService.findByUsuarioId(usuario.getId());
        return ResponseEntity.ok(toResponseList(coletas));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ColetaResponseDto> getById(@PathVariable final int id) {
        final SolicitacaoColetaModel coleta = solicitacaoColetaService.findById(id);
        if (coleta == null) {
            throw new ResourceNotFoundException("Coleta não encontrada");
        }
        final ColetaResponseDto response = toResponse(coleta);
        if (response == null) {
            throw new ResourceNotFoundException("Coleta não encontrada");
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ColetaResponseDto> create(@RequestBody final ColetaRequestDto request, final HttpServletRequest httpRequest) {
        final UserModel usuario = getUsuario(httpRequest);
        final TipoResiduoModel residuo = tipoResiduoService.findByNome(request.getResiduo());
        if (residuo == null) {
            throw new BadRequestException("Tipo de resíduo não encontrado");
        }

        final SolicitacaoColetaModel coleta = new SolicitacaoColetaModel();
        coleta.setUsuarioId(usuario.getId());
        coleta.setTipoResiduoId(residuo.getId());
        coleta.setLogradouro(request.getLogradouro());
        coleta.setNumero(request.getNumero());
        coleta.setBairro(request.getBairro());
        coleta.setCidade(request.getCidade());
        coleta.setEstado("MG");
        coleta.setQuantidadeEstimada(String.valueOf(request.getQuantidade()));
        coleta.setDataDesejada(request.getData());
        coleta.setStatus(request.getStatus());
        coleta.setObservacoes(request.getPeriodo());

        final int id = solicitacaoColetaService.create(coleta);
        if (id == 0) {
            throw new BadRequestException("Erro ao criar coleta");
        }

        final SolicitacaoColetaModel saved = solicitacaoColetaService.findById(id);
        final ColetaResponseDto response = toResponse(saved);
        if (response == null) {
            throw new BadRequestException("Erro ao retornar coleta");
        }

        final NotificacaoModel notificacao = new NotificacaoModel();
        notificacao.setUsuarioId(usuario.getId());
        notificacao.setTitulo("Coleta confirmada");
        final String dataFormatada = formatarData(request.getData());
        notificacao.setMensagem(String.format("Sua coleta de %s foi agendada para %s no período da %s.", residuo.getNome(), dataFormatada, request.getPeriodo().toLowerCase()));
        notificacao.setTipoNotificacao("INFORMATIVA");
        notificacao.setLida(false);
        notificacaoService.create(notificacao);

        final URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable final int id, @RequestBody final ColetaRequestDto request) {
        final boolean updated = solicitacaoColetaService.updateStatus(id, request.getStatus());
        return updated ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final int id) {
        solicitacaoColetaService.delete(id);
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

    private List<ColetaResponseDto> toResponseList(final List<SolicitacaoColetaModel> coletas) {
        final List<ColetaResponseDto> response = new ArrayList<>();
        for (final SolicitacaoColetaModel coleta : coletas) {
            final ColetaResponseDto dto = toResponse(coleta);
            if (dto != null) {
                response.add(dto);
            }
        }
        return response;
    }

    private ColetaResponseDto toResponse(final SolicitacaoColetaModel coleta) {
        if (coleta == null) {
            return null;
        }
        final TipoResiduoModel residuo = tipoResiduoService.findById(coleta.getTipoResiduoId());
        final ColetaResponseDto dto = new ColetaResponseDto();
        dto.setId(coleta.getId());
        dto.setResiduo(residuo != null ? residuo.getNome() : "");
        dto.setQuantidade(parseQuantidade(coleta.getQuantidadeEstimada()));
        dto.setLogradouro(coleta.getLogradouro());
        dto.setNumero(coleta.getNumero());
        dto.setBairro(coleta.getBairro());
        dto.setCidade(coleta.getCidade());
        dto.setData(coleta.getDataDesejada());
        dto.setPeriodo(coleta.getObservacoes());
        dto.setStatus(coleta.getStatus());
        return dto;
    }

    private int parseQuantidade(final String quantidade) {
        if (quantidade == null || quantidade.isBlank()) {
            return 0;
        }
        try {
            return Integer.parseInt(quantidade.trim());
        } catch (final NumberFormatException e) {
            return 0;
        }
    }

    private String formatarData(final String data) {
        if (data == null || data.isBlank()) {
            return "";
        }
        try {
            final LocalDate date = LocalDate.parse(data);
            return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (final Exception e) {
            return data;
        }
    }
}
