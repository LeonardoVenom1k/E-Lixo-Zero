package br.fai.lds.e_lixo_zero.controller;

import br.fai.lds.e_lixo_zero.domain.NotificationModel;
import br.fai.lds.e_lixo_zero.domain.PickupRequestModel;
import br.fai.lds.e_lixo_zero.domain.WasteTypeModel;
import br.fai.lds.e_lixo_zero.domain.UserModel;
import br.fai.lds.e_lixo_zero.dto.PickupRequestDto;
import br.fai.lds.e_lixo_zero.dto.PickupResponseDto;
import br.fai.lds.e_lixo_zero.exceptions.BadRequestException;
import br.fai.lds.e_lixo_zero.exceptions.ResourceNotFoundException;
import br.fai.lds.e_lixo_zero.exceptions.UnauthorizedException;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.service.pickup.PickupRequestService;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.service.notification.NotificationService;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.service.wastetype.WasteTypeService;
import br.fai.lds.e_lixo_zero.ports_and_adapters.port.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/pickups")
public class PickupsRestController {

    @Autowired
    private PickupRequestService pickupRequestService;

    @Autowired
    private WasteTypeService wasteTypeService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<PickupResponseDto>> getAll(final HttpServletRequest request) {
        final UserModel user = getUser(request);
        final List<PickupRequestModel> pickups = pickupRequestService.findByUserId(user.getId());
        return ResponseEntity.ok(toResponseList(pickups));
    }

    @GetMapping("/all")
    public ResponseEntity<List<PickupResponseDto>> getAllForAdmin(final HttpServletRequest request) {
        getAdminUser(request);
        return ResponseEntity.ok(toResponseListWithOwner(pickupRequestService.findAll()));
    }

    @GetMapping("/collector")
    public ResponseEntity<List<PickupResponseDto>> getForCollector(final HttpServletRequest request) {
        final UserModel collector = getCollectorUser(request);
        return ResponseEntity.ok(toResponseListWithOwner(pickupRequestService.findForCollector(collector.getId())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PickupResponseDto> getById(@PathVariable final int id) {
        final PickupRequestModel pickup = pickupRequestService.findById(id);
        if (pickup == null) {
            throw new ResourceNotFoundException("Pickup not found");
        }
        final PickupResponseDto response = toResponse(pickup);
        if (response == null) {
            throw new ResourceNotFoundException("Pickup not found");
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<PickupResponseDto> create(@RequestBody final PickupRequestDto request, final HttpServletRequest httpRequest) {
        final UserModel user = getUser(httpRequest);
        final WasteTypeModel waste = wasteTypeService.findByName(request.getWaste());
        if (waste == null) {
            throw new BadRequestException("Waste type not found");
        }

        if (request.getDate() == null || request.getDate().isBlank()) {
            throw new BadRequestException("Please provide the desired pickup date");
        }
        try {
            final LocalDate desiredDate = LocalDate.parse(request.getDate());
            if (desiredDate.isBefore(LocalDate.now())) {
                throw new BadRequestException("The desired date cannot be in the past");
            }
        } catch (final DateTimeParseException e) {
            throw new BadRequestException("Invalid desired date");
        }

        final PickupRequestModel pickup = new PickupRequestModel();
        pickup.setUserId(user.getId());
        pickup.setWasteTypeId(waste.getId());
        pickup.setStreet(request.getStreet());
        pickup.setNumber(request.getNumber());
        pickup.setNeighborhood(request.getNeighborhood());
        pickup.setCity(request.getCity());
        pickup.setState("MG");
        pickup.setEstimatedQuantity(String.valueOf(request.getQuantity()));
        pickup.setDesiredDate(request.getDate());
        pickup.setStatus(request.getStatus());
        pickup.setNotes(request.getPeriod());

        final int id = pickupRequestService.create(pickup);
        if (id == 0) {
            throw new BadRequestException("Error creating pickup");
        }

        final PickupRequestModel saved = pickupRequestService.findById(id);
        final PickupResponseDto response = toResponse(saved);
        if (response == null) {
            throw new BadRequestException("Error returning pickup");
        }

        final NotificationModel notification = new NotificationModel();
        notification.setUserId(user.getId());
        notification.setTitle("Coleta agendada");
        final String formattedDate = formatDate(request.getDate());
        notification.setMessage(String.format("Sua coleta de %s foi agendada para %s no período da %s.", waste.getName(), formattedDate, request.getPeriod().toLowerCase()));
        notification.setNotificationType("INFO");
        notification.setRead(false);
        notificationService.create(notification);

        final URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable final int id, @RequestBody final PickupRequestDto request, final HttpServletRequest httpRequest) {
        final UserModel user = getUser(httpRequest);
        final boolean updated;
        if ("ADMIN".equals(user.getUserType())) {
            updated = pickupRequestService.updateStatus(id, request.getStatus());
        } else if ("COLLECTOR".equals(user.getUserType())) {
            updated = pickupRequestService.updateStatusByCollector(id, request.getStatus(), user.getId());
        } else {
            throw new UnauthorizedException("Admin or collector access required");
        }
        if (!updated) {
            return ResponseEntity.badRequest().build();
        }
        createStatusNotification(id, request.getStatus());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final int id) {
        pickupRequestService.delete(id);
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

    private UserModel getCollectorUser(final HttpServletRequest request) {
        final UserModel user = getUser(request);
        if (!"COLLECTOR".equals(user.getUserType())) {
            throw new UnauthorizedException("Collector access required");
        }
        return user;
    }

    private List<PickupResponseDto> toResponseListWithOwner(final List<PickupRequestModel> pickups) {
        final List<PickupResponseDto> response = new ArrayList<>();
        for (final PickupRequestModel pickup : pickups) {
            final PickupResponseDto dto = toResponse(pickup);
            if (dto != null) {
                final UserModel owner = userService.findById(pickup.getUserId());
                dto.setUserName(owner != null ? owner.getFullName() : "");
                response.add(dto);
            }
        }
        return response;
    }

    private void createStatusNotification(final int pickupId, final String status) {
        final PickupRequestModel pickup = pickupRequestService.findById(pickupId);
        if (pickup == null) {
            return;
        }
        final WasteTypeModel waste = wasteTypeService.findById(pickup.getWasteTypeId());
        final String wasteName = waste != null ? waste.getName() : "resíduos";
        final NotificationModel notification = new NotificationModel();
        notification.setUserId(pickup.getUserId());
        notification.setTitle("Status da coleta atualizado");
        notification.setMessage(String.format("O status da sua coleta de %s foi atualizado para %s.", wasteName, statusLabel(status)));
        notification.setNotificationType("INFO");
        notification.setRead(false);
        notificationService.create(notification);
    }

    private String statusLabel(final String status) {
        if (status == null) {
            return "";
        }
        switch (status) {
            case "PENDING":
                return "Pendente";
            case "Scheduled":
                return "Agendada";
            case "In Progress":
                return "Em andamento";
            case "Completed":
                return "Concluída";
            case "Cancelled":
            case "Canceled":
                return "Cancelada";
            default:
                return status;
        }
    }

    private List<PickupResponseDto> toResponseList(final List<PickupRequestModel> pickups) {
        final List<PickupResponseDto> response = new ArrayList<>();
        for (final PickupRequestModel pickup : pickups) {
            final PickupResponseDto dto = toResponse(pickup);
            if (dto != null) {
                response.add(dto);
            }
        }
        return response;
    }

    private PickupResponseDto toResponse(final PickupRequestModel pickup) {
        if (pickup == null) {
            return null;
        }
        final WasteTypeModel waste = wasteTypeService.findById(pickup.getWasteTypeId());
        final PickupResponseDto dto = new PickupResponseDto();
        dto.setId(pickup.getId());
        dto.setCollectorId(pickup.getCollectorId());
        dto.setWaste(waste != null ? waste.getName() : "");
        dto.setQuantity(parseQuantity(pickup.getEstimatedQuantity()));
        dto.setStreet(pickup.getStreet());
        dto.setNumber(pickup.getNumber());
        dto.setNeighborhood(pickup.getNeighborhood());
        dto.setCity(pickup.getCity());
        dto.setDate(pickup.getDesiredDate());
        dto.setPeriod(pickup.getNotes());
        dto.setStatus(pickup.getStatus());
        return dto;
    }

    private int parseQuantity(final String quantity) {
        if (quantity == null || quantity.isBlank()) {
            return 0;
        }
        try {
            return Integer.parseInt(quantity.trim());
        } catch (final NumberFormatException e) {
            return 0;
        }
    }

    private String formatDate(final String date) {
        if (date == null || date.isBlank()) {
            return "";
        }
        try {
            final LocalDate parsedDate = LocalDate.parse(date);
            return parsedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (final Exception e) {
            return date;
        }
    }
}
