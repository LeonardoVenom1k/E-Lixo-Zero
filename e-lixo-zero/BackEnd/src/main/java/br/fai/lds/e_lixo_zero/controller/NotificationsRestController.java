package br.fai.lds.e_lixo_zero.controller;

import br.fai.lds.e_lixo_zero.domain.NotificationModel;
import br.fai.lds.e_lixo_zero.domain.UserModel;
import br.fai.lds.e_lixo_zero.exceptions.ResourceNotFoundException;
import br.fai.lds.e_lixo_zero.exceptions.UnauthorizedException;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.service.notification.NotificationService;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/notifications")
public class NotificationsRestController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<NotificationModel>> getAll(final HttpServletRequest request) {
        final UserModel user = getUser(request);
        return ResponseEntity.ok(notificationService.findByUserId(user.getId()));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationModel>> getByUsuario(@PathVariable final int userId) {
        return ResponseEntity.ok(notificationService.findByUserId(userId));
    }

    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<NotificationModel>> getNaoLidasByUsuario(@PathVariable final int userId) {
        return ResponseEntity.ok(notificationService.findUnreadByUserId(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationModel> getById(@PathVariable final int id) {
        final NotificationModel notification = notificationService.findById(id);
        if (notification == null) {
            throw new ResourceNotFoundException("Notification not found");
        }
        return ResponseEntity.ok(notification);
    }

    @PostMapping
    public ResponseEntity<NotificationModel> create(@RequestBody final NotificationModel notification, final HttpServletRequest request) {
        getAdminUser(request);
        final int id = notificationService.create(notification);
        if (id == 0) {
            throw new ResourceNotFoundException("Error creating notification");
        }
        return ResponseEntity.ok(notificationService.findById(id));
    }

    @PostMapping("/broadcast")
    public ResponseEntity<Integer> broadcast(@RequestBody final NotificationModel notification, final HttpServletRequest request) {
        getAdminUser(request);
        int sent = 0;
        for (final UserModel user : userService.findAll()) {
            if (!user.isActive()) {
                continue;
            }
            final NotificationModel copy = new NotificationModel();
            copy.setUserId(user.getId());
            copy.setTitle(notification.getTitle());
            copy.setMessage(notification.getMessage());
            copy.setNotificationType(notification.getNotificationType());
            copy.setRead(false);
            if (notificationService.create(copy) > 0) {
                sent++;
            }
        }
        return ResponseEntity.ok(sent);
    }

    @PutMapping("/{id}/mark-read")
    public ResponseEntity<Void> markAsRead(@PathVariable final int id) {
        final boolean updated = notificationService.markAsRead(id);
        return updated ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final int id) {
        notificationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private UserModel getUser(final HttpServletRequest request) {
        final String email = (String) request.getAttribute("email");
        if (email == null || email.isBlank()) {
            throw new UnauthorizedException("User not authenticated");
        }
        final UserModel user = userService.findByEmail(email);
        if (user == null) {
            throw new UnauthorizedException("User not found");
        }
        return user;
    }

    private UserModel getAdminUser(final HttpServletRequest request) {
        final UserModel user = getUser(request);
        if (!"ADMIN".equals(user.getUserType())) {
            throw new UnauthorizedException("Admin access required");
        }
        return user;
    }
}
