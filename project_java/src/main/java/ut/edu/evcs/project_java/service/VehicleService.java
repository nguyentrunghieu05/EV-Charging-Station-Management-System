package ut.edu.evcs.project_java.service;

import org.springframework.stereotype.Service;
import ut.edu.evcs.project_java.domain.notification.NotificationType;
import ut.edu.evcs.project_java.domain.vehicle.Vehicle;
import ut.edu.evcs.project_java.repo.VehicleRepository;
import ut.edu.evcs.project_java.service.NotificationService;

import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {
    private final VehicleRepository repo;
    private final NotificationService notificationService;

    public VehicleService(VehicleRepository repo, NotificationService notificationService) {
        this.repo = repo;
        this.notificationService = notificationService;
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
        Vehicle saved = repo.save(vehicle);
        String meta = String.format("{\"vehicleId\":\"%s\",\"plateNo\":\"%s\"}", saved.getId(), saved.getPlateNo());
        notificationService.createInAppNotification(
            saved.getDriverId(),
            "Đã thêm xe mới",
            "Xe " + (saved.getBrand()!=null?saved.getBrand():"") + " " + (saved.getModel()!=null?saved.getModel():"") + " - biển số " + saved.getPlateNo(),
            NotificationType.VEHICLE_CREATED,
            meta
        );
        return saved;
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
        
        Vehicle updated = repo.save(existing);
        String metaU = String.format("{\"vehicleId\":\"%s\",\"plateNo\":\"%s\"}", updated.getId(), updated.getPlateNo());
        notificationService.createInAppNotification(
            updated.getDriverId(),
            "Cập nhật thông tin xe",
            "Xe " + (updated.getBrand()!=null?updated.getBrand():"") + " " + (updated.getModel()!=null?updated.getModel():"") + " đã được cập nhật",
            NotificationType.VEHICLE_UPDATED,
            metaU
        );
        return updated;
    }

    /**
     * Xoá xe
     */
    public void delete(String id) {
        Vehicle existing = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found: " + id));
        repo.deleteById(id);
        String metaD = String.format("{\"vehicleId\":\"%s\",\"plateNo\":\"%s\"}", existing.getId(), existing.getPlateNo());
        notificationService.createInAppNotification(
            existing.getDriverId(),
            "Đã xoá xe",
            "Xe biển số " + existing.getPlateNo() + " đã được xoá",
            NotificationType.VEHICLE_DELETED,
            metaD
        );
    }

    /**
     * Lấy chi tiết 1 xe
     */
    public Optional<Vehicle> getById(String id) {
        return repo.findById(id);
    }
}