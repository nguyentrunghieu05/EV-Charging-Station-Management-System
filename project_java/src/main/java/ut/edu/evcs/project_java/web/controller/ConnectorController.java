package ut.edu.evcs.project_java.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ut.edu.evcs.project_java.domain.station.Connector;
import ut.edu.evcs.project_java.service.ConnectorService;

import java.util.List;
import java.util.Map;

@Tag(name = "Connectors", description = "Quản lý cổng sạc")
@RestController
@RequestMapping("/api/connectors")
@SecurityRequirement(name = "bearerAuth")
public class ConnectorController {

    private final ConnectorService service;

    public ConnectorController(ConnectorService service) {
        this.service = service;
    }

    @Operation(summary = "Kiểm tra connector có available không")
    @GetMapping("/{id}/available")
    @PreAuthorize("hasAnyRole('EV_DRIVER', 'CS_STAFF', 'ADMIN')")
    public Map<String, Object> checkAvailable(@PathVariable String id) {
        return Map.of(
            "connectorId", id,
            "available", service.isAvailable(id)
        );
    }

    @Operation(summary = "Đánh dấu connector đang bận (occupy)")
    @PostMapping("/{id}/occupy")
    @PreAuthorize("hasAnyRole('CS_STAFF', 'ADMIN')")
    public Map<String, String> occupy(@PathVariable String id) {
        service.occupy(id);
        return Map.of(
            "status", "success",
            "message", "Connector occupied",
            "connectorId", id
        );
    }

    @Operation(summary = "Giải phóng connector (release)")
    @PostMapping("/{id}/release")
    @PreAuthorize("hasAnyRole('CS_STAFF', 'ADMIN')")
    public Map<String, String> release(@PathVariable String id) {
        service.release(id);
        return Map.of(
            "status", "success",
            "message", "Connector released",
            "connectorId", id
        );
    }

    @Operation(summary = "Lấy danh sách connector theo charging point")
    @GetMapping("/point/{pointId}")
    @PreAuthorize("hasAnyRole('EV_DRIVER', 'CS_STAFF', 'ADMIN')")
    public List<Connector> getByPointId(@PathVariable String pointId) {
        return service.getByPointId(pointId);
    }

    @Operation(summary = "Lấy danh sách connector available")
    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('EV_DRIVER', 'CS_STAFF', 'ADMIN')")
    public List<Connector> getAvailableConnectors() {
        return service.getAvailableConnectors();
    }

    @Operation(summary = "Lấy chi tiết connector")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('EV_DRIVER', 'CS_STAFF', 'ADMIN')")
    public Connector getById(@PathVariable String id) {
        return service.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("Connector not found: " + id));
    }

    @Operation(summary = "Tạo connector mới")
    @PostMapping
    @PreAuthorize("hasAnyRole('CS_STAFF', 'ADMIN')")
    public Connector create(@RequestBody Connector connector) {
        return service.create(connector);
    }

    @Operation(summary = "Cập nhật connector")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('CS_STAFF', 'ADMIN')")
    public Connector update(@PathVariable String id, @RequestBody Connector updates) {
        return service.update(id, updates);
    }

    @Operation(summary = "Xoá connector")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}