package ut.edu.evcs.project_java.service;

import org.springframework.stereotype.Service;
import ut.edu.evcs.project_java.domain.notification.NotificationType;
import ut.edu.evcs.project_java.domain.vehicle.Vehicle;
import ut.edu.evcs.project_java.repo.VehicleRepository;
import ut.edu.evcs.project_java.service.NotificationService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
     * @param driverId ID của tài xế (Long)
     */
    // ⭐ ĐÃ SỬA: Thay đổi tham số từ String sang Long
    public List<Vehicle> getByDriverId(Long driverId) {
        String driverIdStr = String.valueOf(driverId);
        
        // Giả định rằng Driver ID trong Vehicle là String (do cơ sở dữ liệu/domain)
        return repo.findAll().stream()
                .filter(v -> v.getDriverId() != null && v.getDriverId().equals(driverIdStr))
                .collect(Collectors.toList());
    }

    /**
     * Tạo xe mới cho driver. Lấy driverId từ token người dùng.
     * @param vehicle Dữ liệu xe mới
     * @param userId ID người dùng đang đăng nhập (là Driver ID)
     */
    // ⭐ ĐÃ SỬA: Thêm tham số Long userId
    public Vehicle create(Vehicle vehicle, Long userId) {
        String driverIdStr = String.valueOf(userId);
        
        // 1. Gán Driver ID từ token cho Vehicle
        vehicle.setDriverId(driverIdStr);
        
        // 2. Kiểm tra tính hợp lệ cơ bản
        // driverId đã được đảm bảo từ token
        if (vehicle.getPlateNo() == null || vehicle.getPlateNo().isBlank()) {
            throw new IllegalArgumentException("Biển số xe (plateNo) là bắt buộc.");
        }
        
        // 3. Kiểm tra trùng biển số
        Optional<Vehicle> existing = repo.findAll().stream()
                .filter(v -> v.getPlateNo().equalsIgnoreCase(vehicle.getPlateNo()))
                .findFirst();
        
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Biển số xe đã tồn tại: " + vehicle.getPlateNo());
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
     * @param id ID của xe (Long)
     */
    // ⭐ ĐÃ SỬA: Thay đổi tham số id từ String sang Long
    public Vehicle update(Long id, Vehicle updates) {
        // Do VehicleRepository thường dùng String/UUID cho ID, ta cần chuyển Long sang String
        String idStr = String.valueOf(id); 

        Vehicle existing = repo.findById(idStr)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy xe với ID: " + idStr));
        
        // Không cho đổi driver
        if (updates.getBrand() != null) existing.setBrand(updates.getBrand());
        if (updates.getModel() != null) existing.setModel(updates.getModel());
        // Chỉ cập nhật nếu giá trị hợp lệ
        if (updates.getBatteryCapacityKWh() > 0) existing.setBatteryCapacityKWh(updates.getBatteryCapacityKWh());
        if (updates.getConnectorSupported() != null) existing.setConnectorSupported(updates.getConnectorSupported());
        
        // Nếu đổi biển số, check trùng
        if (updates.getPlateNo() != null && !updates.getPlateNo().equals(existing.getPlateNo())) {
            Optional<Vehicle> duplicate = repo.findAll().stream()
                    // Dùng idStr để loại trừ chính bản thân nó khi kiểm tra trùng
                    .filter(v -> !v.getId().equals(idStr) && v.getPlateNo().equalsIgnoreCase(updates.getPlateNo())) 
                    .findFirst();
            if (duplicate.isPresent()) {
                throw new IllegalArgumentException("Biển số xe đã tồn tại: " + updates.getPlateNo());
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
     * @param id ID của xe (Long)
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
    // ⭐ ĐÃ SỬA: Thay đổi tham số id từ String sang Long
    public void delete(Long id) {
        String idStr = String.valueOf(id);
        
        if (!repo.existsById(idStr)) {
            throw new IllegalArgumentException("Không tìm thấy xe với ID: " + idStr);
        }
        repo.deleteById(idStr);
    }

    /**
     * Lấy chi tiết 1 xe
     * @param id ID của xe (Long)
     */
    // ⭐ ĐÃ SỬA: Thay đổi tham số id từ String sang Long
    public Optional<Vehicle> getById(Long id) {
        String idStr = String.valueOf(id);
        return repo.findById(idStr);
    }
}