package ut.edu.evcs.project_java.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ut.edu.evcs.project_java.domain.vehicle.Vehicle;
import ut.edu.evcs.project_java.service.VehicleService;

import java.util.List;

@Tag(name = "Vehicles", description = "Quản lý xe của tài xế")
@RestController
@RequestMapping("/api/vehicles")
@SecurityRequirement(name = "bearerAuth")
public class VehicleController {

    private final VehicleService service;

    public VehicleController(VehicleService service) {
        this.service = service;
    }

    @Operation(summary = "Lấy danh sách xe của driver")
    @GetMapping("/driver/{driverId}")
    @PreAuthorize("hasAnyRole('EV_DRIVER', 'ADMIN')")
    public List<Vehicle> getByDriverId(@PathVariable String driverId) {
        return service.getByDriverId(driverId);
    }

    @Operation(summary = "Lấy chi tiết 1 xe")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('EV_DRIVER', 'ADMIN')")
    public Vehicle getById(@PathVariable String id) {
        return service.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found: " + id));
    }

    @Operation(summary = "Tạo xe mới")
    @PostMapping
    @PreAuthorize("hasAnyRole('EV_DRIVER', 'ADMIN')")
    public Vehicle create(@RequestBody Vehicle vehicle) {
        return service.create(vehicle);
    }

    @Operation(summary = "Cập nhật thông tin xe")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('EV_DRIVER', 'ADMIN')")
    public Vehicle update(@PathVariable String id, @RequestBody Vehicle updates) {
        return service.update(id, updates);
    }

    @Operation(summary = "Xoá xe")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('EV_DRIVER', 'ADMIN')")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}