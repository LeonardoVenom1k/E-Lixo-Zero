package br.fai.lds.e_lixo_zero.controller;

import br.fai.lds.e_lixo_zero.domain.TipoResiduoModel;
import br.fai.lds.e_lixo_zero.exceptions.ResourceNotFoundException;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.service.residuo.TipoResiduoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/residuos")
public class ResiduoController {

    @Autowired
    private TipoResiduoService tipoResiduoService;

    @GetMapping
    public ResponseEntity<List<TipoResiduoModel>> getAll() {
        return ResponseEntity.ok(tipoResiduoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoResiduoModel> getById(@PathVariable final int id) {
        final TipoResiduoModel residuo = tipoResiduoService.findById(id);
        if (residuo == null) {
            throw new ResourceNotFoundException("Resíduo não encontrado");
        }
        return ResponseEntity.ok(residuo);
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody final TipoResiduoModel residuo) {
        final int id = tipoResiduoService.create(residuo);
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
    public ResponseEntity<Void> update(@PathVariable final int id, @RequestBody final TipoResiduoModel residuo) {
        final boolean updated = tipoResiduoService.update(id, residuo);
        return updated ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final int id) {
        tipoResiduoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
