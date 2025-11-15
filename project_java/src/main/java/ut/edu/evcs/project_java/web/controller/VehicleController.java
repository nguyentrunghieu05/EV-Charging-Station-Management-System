package ut.edu.evcs.project_java.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ut.edu.evcs.project_java.domain.vehicle.Vehicle;
import ut.edu.evcs.project_java.service.VehicleService;

import java.util.List;
import java.util.Map;
import java.util.Optional; // Cần thiết nếu dùng Optional

// ❗ LƯU Ý QUAN TRỌNG: Bạn cần tạo interface này hoặc thay thế nó
// bằng class chi tiết người dùng thực tế của bạn (ví dụ: UserDetailsImpl)
interface UserDetailsWithId {
    Long getId();
}

@Tag(name = "Vehicles", description = "Quản lý xe của tài xế")
@RestController
@RequestMapping("/api/vehicles")
@SecurityRequirement(name = "bearerAuth")
public class VehicleController {

    private final VehicleService service;

    public VehicleController(VehicleService service) {
        this.service = service;
    }

    // ⭐ GIẢI QUYẾT LỖI CustomUserDetails: 
    // Trích xuất ID từ Principal (Giả định rằng bạn có một cơ chế để lấy ID Long)
    private Long extractUserId(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return null;
        }
        
        try {
            // Thay thế 'UserDetailsWithId' bằng class UserDetails thực tế của bạn
            // VÍ DỤ: Nếu bạn sử dụng UserDetails của Spring Security và lưu ID trong đó:
            if (authentication.getPrincipal() instanceof UserDetailsWithId) { 
                return ((UserDetailsWithId) authentication.getPrincipal()).getId();
            }
            
            // HOẶC nếu bạn dùng JWT và lưu ID trong Name/Principal
            String principalName = authentication.getName();
            return Long.valueOf(principalName);
            
        } catch (NumberFormatException e) {
            System.err.println("Principal name is not a valid Long ID: " + authentication.getName());
            return null;
        } catch (Exception e) {
            System.err.println("Error extracting user ID: " + e.getMessage());
            return null;
        }
    }

    @Operation(summary = "Lấy danh sách xe của driver")
    @GetMapping("/driver/{driverId}")
    @PreAuthorize("hasAnyRole('EV_DRIVER', 'ADMIN')")
    public List<Vehicle> getByDriverId(@PathVariable Long driverId) { 
        return service.getByDriverId(driverId);
    }

    @Operation(summary = "Lấy chi tiết 1 xe")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('EV_DRIVER', 'ADMIN')")
    public Vehicle getById(@PathVariable Long id) { 
        // Đảm bảo Service trả về Optional
        return service.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found: " + id));
    }

    @Operation(summary = "Tạo xe mới")
    @PostMapping
    @PreAuthorize("hasAnyRole('EV_DRIVER', 'ADMIN')")
    public ResponseEntity<?> create(@RequestBody Vehicle vehicle, Authentication authentication) {
        
        Long userId = extractUserId(authentication);
        if (userId == null) {
            return new ResponseEntity<>(
                Map.of("message", "Phiên đăng nhập không hợp lệ hoặc thiếu thông tin người dùng."), 
                HttpStatus.UNAUTHORIZED // 401
            );
        }

        try {
            // ⭐ Đã sửa lỗi: service.create(Vehicle, Long)
            Vehicle createdVehicle = service.create(vehicle, userId); 
            
            return new ResponseEntity<>(createdVehicle, HttpStatus.CREATED); 
        } catch (IllegalArgumentException e) {
            // Trả về JSON hợp lệ cho lỗi nghiệp vụ (ví dụ: Biển số trùng)
            return new ResponseEntity<>(
                Map.of("message", e.getMessage()), 
                HttpStatus.BAD_REQUEST // 400
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                Map.of("message", "Lỗi server không xác định khi tạo xe: " + e.getMessage()), 
                HttpStatus.INTERNAL_SERVER_ERROR // 500
            );
        }
    }

    @Operation(summary = "Cập nhật thông tin xe")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('EV_DRIVER', 'ADMIN')")
    public Vehicle update(@PathVariable Long id, @RequestBody Vehicle updates) {
        return service.update(id, updates);
    }

    @Operation(summary = "Xoá xe")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('EV_DRIVER', 'ADMIN')")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}