package ut.edu.evcs.project_java.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ut.edu.evcs.project_java.domain.station.Connector;
import ut.edu.evcs.project_java.repo.ConnectorRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ConnectorService {
    private final ConnectorRepository repo;

    public ConnectorService(ConnectorRepository repo) {
        this.repo = repo;
    }

    /**
     * Kiểm tra connector có available không (không bị occupied)
     */
    public boolean isAvailable(String connectorId) {
        return repo.findById(connectorId)
                .map(c -> !c.isOccupied())
                .orElse(false);
    }

    /**
     * Đánh dấu connector là đang bận (occupy)
     */
    @Transactional
    public void occupy(String connectorId) {
        Connector c = repo.findById(connectorId)
                .orElseThrow(() -> new IllegalArgumentException("Connector not found: " + connectorId));
        
        if (c.isOccupied()) {
            throw new IllegalStateException("Connector is already occupied: " + connectorId);
        }
        
        c.setOccupied(true);
        repo.save(c);
    }

    /**
     * Giải phóng connector (release)
     */
    @Transactional
    public void release(String connectorId) {
        Connector c = repo.findById(connectorId)
                .orElseThrow(() -> new IllegalArgumentException("Connector not found: " + connectorId));
        
        c.setOccupied(false);
        repo.save(c);
    }

    /**
     * Lấy danh sách connector của 1 charging point
     */
    public List<Connector> getByPointId(String pointId) {
        return repo.findAll().stream()
                .filter(c -> c.getChargingPoint() != null && c.getChargingPoint().getId().equals(pointId))
                .toList();
    }

    /**
     * Lấy danh sách connector available
     */
    public List<Connector> getAvailableConnectors() {
        return repo.findAll().stream()
                .filter(c -> !c.isOccupied())
                .toList();
    }

    /**
     * Lấy chi tiết connector
     */
    public Optional<Connector> getById(String id) {
        return repo.findById(id);
    }

    /**
     * Tạo connector mới
     */
    public Connector create(Connector connector) {
        if (connector.getType() == null || connector.getType().isBlank()) {
            throw new IllegalArgumentException("Connector type is required");
        }
        if (connector.getChargingPoint() == null) {
            throw new IllegalArgumentException("ChargingPoint is required");
        }
        return repo.save(connector);
    }

    /**
     * Cập nhật thông tin connector
     */
    public Connector update(String id, Connector updates) {
        Connector existing = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Connector not found: " + id));
        
        if (updates.getType() != null) existing.setType(updates.getType());
        if (updates.getMaxCurrentA() > 0) existing.setMaxCurrentA(updates.getMaxCurrentA());
        if (updates.getVoltageV() > 0) existing.setVoltageV(updates.getVoltageV());
        if (updates.getQrCode() != null) existing.setQrCode(updates.getQrCode());
        
        return repo.save(existing);
    }

    /**
     * Xoá connector
     */
    public void delete(String id) {
        if (!repo.existsById(id)) {
            throw new IllegalArgumentException("Connector not found: " + id);
        }
        repo.deleteById(id);
    }
}