package ut.edu.evcs.project_java.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ut.edu.evcs.project_java.service.StationService;
import ut.edu.evcs.project_java.web.dto.StationDTO;
import ut.edu.evcs.project_java.web.mapper.StationMapper;

import java.util.List;

@Tag(name = "Stations", description = "Quản lý trạm sạc")
@RestController
@RequestMapping("/api/stations")
@SecurityRequirement(name = "bearerAuth")
public class StationController {
    
    private final StationService service;
    private final StationMapper mapper;
    
    public StationController(StationService service, StationMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Operation(summary = "Lấy danh sách tất cả trạm sạc")
    @GetMapping
    @PreAuthorize("hasAnyRole('EV_DRIVER', 'CS_STAFF', 'ADMIN')")
    public List<StationDTO> list() {
        return service.list().stream().map(mapper::toDto).toList();
    }

    @Operation(
        summary = "Tìm trạm sạc gần vị trí hiện tại",
        description = "Sử dụng công thức Haversine để tính khoảng cách"
    )
    @GetMapping("/nearby")
    @PreAuthorize("hasAnyRole('EV_DRIVER', 'CS_STAFF', 'ADMIN')")
    public List<StationDTO> findNearby(
            @Parameter(description = "Vĩ độ (latitude)", example = "10.762622")
            @RequestParam double lat,
            
            @Parameter(description = "Kinh độ (longitude)", example = "106.660172")
            @RequestParam double lng,
            
            @Parameter(description = "Bán kính tìm kiếm (km)", example = "5")
            @RequestParam(defaultValue = "5") double radiusKm,
            
            @Parameter(description = "Số lượng kết quả tối đa", example = "10")
            @RequestParam(defaultValue = "10") int limit
    ) {
        return service.findNearby(lat, lng, radiusKm, limit)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Operation(summary = "Lấy chi tiết trạm sạc")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('EV_DRIVER', 'CS_STAFF', 'ADMIN')")
    public StationDTO getById(@PathVariable String id) {
        return service.getById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Station not found: " + id));
    }

    @Operation(summary = "Lấy danh sách connector của trạm")
    @GetMapping("/{id}/connectors")
    @PreAuthorize("hasAnyRole('EV_DRIVER', 'CS_STAFF', 'ADMIN')")
    public List<ut.edu.evcs.project_java.web.dto.ConnectorInfoDTO> getConnectors(@PathVariable String id) {
        return service.getAvailableConnectors(id);
    }
}