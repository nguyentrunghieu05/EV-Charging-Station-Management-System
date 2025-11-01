package ut.edu.evcs.project_java.service;

import org.springframework.stereotype.Service;
import ut.edu.evcs.project_java.domain.vehicle.Vehicle;
import ut.edu.evcs.project_java.repo.VehicleRepository;

import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {
    private final VehicleRepository repo;

    public VehicleService(VehicleRepository repo) {
        this.repo = repo;
    }

    /**
     * Lấy danh sách xe của driver
     */
    public List<Vehicle> getByDriverId(String driverId) {
        return repo.findAll().stream()
                .filter(v -> v.getDriverId().equals(driverId))
                .toList();
    }

    /**
     * Tạo xe mới cho driver
     */
    public Vehicle create(Vehicle vehicle) {
        if (vehicle.getDriverId() == null || vehicle.getDriverId().isBlank()) {
            throw new IllegalArgumentException("driverId is required");
        }
        if (vehicle.getPlateNo() == null || vehicle.getPlateNo().isBlank()) {
            throw new IllegalArgumentException("plateNo is required");
        }
        // Kiểm tra trùng biển số
        Optional<Vehicle> existing = repo.findAll().stream()
                .filter(v -> v.getPlateNo().equalsIgnoreCase(vehicle.getPlateNo()))
                .findFirst();
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Plate number already exists: " + vehicle.getPlateNo());
        }
        return repo.save(vehicle);
    }

    /**
     * Cập nhật thông tin xe
     */
    public Vehicle update(String id, Vehicle updates) {
        Vehicle existing = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found: " + id));
        
        // Không cho đổi driver
        if (updates.getBrand() != null) existing.setBrand(updates.getBrand());
        if (updates.getModel() != null) existing.setModel(updates.getModel());
        if (updates.getBatteryCapacityKWh() > 0) existing.setBatteryCapacityKWh(updates.getBatteryCapacityKWh());
        if (updates.getConnectorSupported() != null) existing.setConnectorSupported(updates.getConnectorSupported());
        
        // Nếu đổi biển số, check trùng
        if (updates.getPlateNo() != null && !updates.getPlateNo().equals(existing.getPlateNo())) {
            Optional<Vehicle> duplicate = repo.findAll().stream()
                    .filter(v -> !v.getId().equals(id) && v.getPlateNo().equalsIgnoreCase(updates.getPlateNo()))
                    .findFirst();
            if (duplicate.isPresent()) {
                throw new IllegalArgumentException("Plate number already exists: " + updates.getPlateNo());
            }
            existing.setPlateNo(updates.getPlateNo());
        }
        
        return repo.save(existing);
    }

    /**
     * Xoá xe
     */
    public void delete(String id) {
        if (!repo.existsById(id)) {
            throw new IllegalArgumentException("Vehicle not found: " + id);
        }
        repo.deleteById(id);
    }

    /**
     * Lấy chi tiết 1 xe
     */
    public Optional<Vehicle> getById(String id) {
        return repo.findById(id);
    }
}