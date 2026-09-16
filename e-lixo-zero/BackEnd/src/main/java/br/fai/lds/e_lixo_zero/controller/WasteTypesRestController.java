package br.fai.lds.e_lixo_zero.controller;

import br.fai.lds.e_lixo_zero.domain.UserModel;
import br.fai.lds.e_lixo_zero.domain.WasteTypeModel;
import br.fai.lds.e_lixo_zero.exceptions.ResourceNotFoundException;
import br.fai.lds.e_lixo_zero.exceptions.UnauthorizedException;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.service.user.UserService;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.service.wastetype.WasteTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/waste-types")
public class WasteTypesRestController {

    @Autowired
    private WasteTypeService wasteTypeService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<WasteTypeModel>> getAll() {
        return ResponseEntity.ok(wasteTypeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WasteTypeModel> getById(@PathVariable final int id) {
        final WasteTypeModel waste = wasteTypeService.findById(id);
        if (waste == null) {
            throw new ResourceNotFoundException("Waste type not found");
        }
        return ResponseEntity.ok(waste);
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody final WasteTypeModel waste, final HttpServletRequest request) {
        getAdminUser(request);
        final int id = wasteTypeService.create(waste);
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
    public ResponseEntity<Void> update(@PathVariable final int id, @RequestBody final WasteTypeModel waste, final HttpServletRequest request) {
        getAdminUser(request);
        final boolean updated = wasteTypeService.update(id, waste);
        return updated ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final int id, final HttpServletRequest request) {
        getAdminUser(request);
        wasteTypeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private UserModel getAdminUser(final HttpServletRequest request) {
        final String email = (String) request.getAttribute("email");
        if (email == null || email.isBlank()) {
            throw new UnauthorizedException("User not authenticated");
        }
        final UserModel user = userService.findByEmail(email);
        if (user == null) {
            throw new UnauthorizedException("User not found");
        }
        if (!"ADMIN".equals(user.getUserType())) {
            throw new UnauthorizedException("Admin access required");
        }
        return user;
    }
}
