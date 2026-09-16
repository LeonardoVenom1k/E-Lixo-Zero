package br.fai.lds.e_lixo_zero.controller;

import br.fai.lds.e_lixo_zero.domain.NotificationModel;
import br.fai.lds.e_lixo_zero.domain.UserModel;
import br.fai.lds.e_lixo_zero.dto.LoginRequestDto;
import br.fai.lds.e_lixo_zero.dto.LoginResponseDto;
import br.fai.lds.e_lixo_zero.exceptions.UnauthorizedException;
import br.fai.lds.e_lixo_zero.exceptions.ResourceNotFoundException;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.service.notification.NotificationService;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.service.user.UserService;
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
@RequestMapping("/api/users")
public class UsersRestController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private NotificationService notificationService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @GetMapping
    public ResponseEntity<List<UserModel>> getAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserModel> getById(@PathVariable final int id) {
        final UserModel user = userService.findById(id);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody final UserModel userModel) {
        final int id = userService.create(userModel);
        if (id == 0) {
            return ResponseEntity.badRequest().build();
        }
        createWelcomeNotification(id, userModel);
        final URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable final int id, @RequestBody final UserModel userModel) {
        final boolean updated = userService.update(id, userModel);
        return updated ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final int id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody final LoginRequestDto loginRequest) {
        final UserModel user = userService.findByEmail(loginRequest.getEmail());
        if (user == null || !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        final String token = jwtTokenService.generateToken(user.getEmail());
        final LoginResponseDto response = toLoginResponse(user, token);
        return ResponseEntity.ok(response);
    }

    private void createWelcomeNotification(final int userId, final UserModel user) {
        final NotificationModel welcome = new NotificationModel();
        welcome.setUserId(userId);
        welcome.setTitle("Bem-vindo(a) ao E-Lixo Zero!");
        final String firstName = user.getFullName() != null ? user.getFullName().trim().split(" ")[0] : "";
        welcome.setMessage(String.format("Olá, %s! Encontre pontos de coleta em %s e agende coletas residenciais.", firstName, user.getCity()));
        welcome.setNotificationType("INFO");
        welcome.setRead(false);
        notificationService.create(welcome);
    }

    private LoginResponseDto toLoginResponse(final UserModel user, final String token) {
        final LoginResponseDto response = new LoginResponseDto();
        response.setId(user.getId());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setCpf(user.getCpf());
        response.setPhone(user.getPhone());
        response.setStreet(user.getStreet());
        response.setNumber(user.getNumber());
        response.setNeighborhood(user.getNeighborhood());
        response.setCity(user.getCity());
        response.setState(user.getState());
        response.setUserType(user.getUserType());
        response.setToken(token);
        return response;
    }
}
