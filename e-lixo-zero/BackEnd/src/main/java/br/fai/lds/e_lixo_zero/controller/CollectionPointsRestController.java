package br.fai.lds.e_lixo_zero.controller;

import br.fai.lds.e_lixo_zero.domain.CollectionPointModel;
import br.fai.lds.e_lixo_zero.exceptions.ResourceNotFoundException;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.service.collectionpoint.CollectionPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/collection-points")
public class CollectionPointsRestController {

    @Autowired
    private CollectionPointService collectionPointService;

    @GetMapping
    public ResponseEntity<List<CollectionPointModel>> getAll() {
        return ResponseEntity.ok(collectionPointService.findAll());
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<List<CollectionPointModel>> getByCity(@PathVariable final String city) {
        return ResponseEntity.ok(collectionPointService.findByCity(city));
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<CollectionPointModel>> getNearby(
            @RequestParam final double lat,
            @RequestParam final double lng) {
        return ResponseEntity.ok(collectionPointService.findNearby(lat, lng));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CollectionPointModel> getById(@PathVariable final int id) {
        final CollectionPointModel point = collectionPointService.findById(id);
        if (point == null) {
            throw new ResourceNotFoundException("Collection point not found");
        }
        return ResponseEntity.ok(point);
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody final CollectionPointModel point) {
        final int id = collectionPointService.create(point);
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
    public ResponseEntity<Void> update(@PathVariable final int id, @RequestBody final CollectionPointModel point) {
        final boolean updated = collectionPointService.update(id, point);
        return updated ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final int id) {
        collectionPointService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
