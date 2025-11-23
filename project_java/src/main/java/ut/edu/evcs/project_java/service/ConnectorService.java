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

    public boolean isAvailable(String connectorId) {
        return repo.findById(connectorId)
                .map(c -> !c.isOccupied())
                .orElse(false);
    }

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

    @Transactional
    public void release(String connectorId) {
        Connector c = repo.findById(connectorId)
                .orElseThrow(() -> new IllegalArgumentException("Connector not found: " + connectorId));

        c.setOccupied(false);
        repo.save(c);
    }

    public List<Connector> getByPointId(String pointId) {
        return repo.findAll().stream()
                .filter(c -> c.getChargingPoint() != null && c.getChargingPoint().getId().equals(pointId))
                .toList();
    }

    public List<Connector> getAvailableConnectors() {
        return repo.findAll().stream()
                .filter(c -> !c.isOccupied())
                .toList();
    }

    public Optional<Connector> getById(String id) {
        return repo.findById(id);
    }

    public Connector create(Connector connector) {
        if (connector.getType() == null || connector.getType().isBlank()) {
            throw new IllegalArgumentException("Connector type is required");
        }
        if (connector.getChargingPoint() == null) {
            throw new IllegalArgumentException("ChargingPoint is required");
        }
        return repo.save(connector);
    }

    public Connector update(String id, Connector updates) {
        Connector existing = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Connector not found: " + id));

        if (updates.getType() != null)
            existing.setType(updates.getType());
        if (updates.getMaxCurrentA() > 0)
            existing.setMaxCurrentA(updates.getMaxCurrentA());
        if (updates.getVoltageV() > 0)
            existing.setVoltageV(updates.getVoltageV());
        if (updates.getQrCode() != null)
            existing.setQrCode(updates.getQrCode());

        return repo.save(existing);
    }

    public void delete(String id) {
        if (!repo.existsById(id)) {
            throw new IllegalArgumentException("Connector not found: " + id);
        }
        repo.deleteById(id);
    }
}