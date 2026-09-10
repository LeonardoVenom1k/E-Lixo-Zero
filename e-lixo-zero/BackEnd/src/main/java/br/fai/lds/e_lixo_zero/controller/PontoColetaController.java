package br.fai.lds.e_lixo_zero.controller;

import br.fai.lds.e_lixo_zero.domain.PontoColetaModel;
import br.fai.lds.e_lixo_zero.exceptions.ResourceNotFoundException;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.service.ponto.PontoColetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/pontos-coleta")
public class PontoColetaController {

    @Autowired
    private PontoColetaService pontoColetaService;

    @GetMapping
    public ResponseEntity<List<PontoColetaModel>> getAll() {
        return ResponseEntity.ok(pontoColetaService.findAll());
    }

    @GetMapping("/cidade/{cidade}")
    public ResponseEntity<List<PontoColetaModel>> getByCidade(@PathVariable final String cidade) {
        return ResponseEntity.ok(pontoColetaService.findByCidade(cidade));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PontoColetaModel> getById(@PathVariable final int id) {
        final PontoColetaModel ponto = pontoColetaService.findById(id);
        if (ponto == null) {
            throw new ResourceNotFoundException("Ponto de coleta não encontrado");
        }
        return ResponseEntity.ok(ponto);
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody final PontoColetaModel ponto) {
        final int id = pontoColetaService.create(ponto);
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
    public ResponseEntity<Void> update(@PathVariable final int id, @RequestBody final PontoColetaModel ponto) {
        final boolean updated = pontoColetaService.update(id, ponto);
        return updated ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final int id) {
        pontoColetaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
