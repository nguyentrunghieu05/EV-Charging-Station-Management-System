package ut.edu.evcs.project_java.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ut.edu.evcs.project_java.domain.station.Station;
import ut.edu.evcs.project_java.domain.station.StationStatus;
import ut.edu.evcs.project_java.repo.ConnectorRepository;
import ut.edu.evcs.project_java.repo.StationRepository;

@Service
public class StationService {
    
    private final StationRepository repo;
    private final ConnectorRepository connectorRepo;
    
    public StationService(StationRepository repo, ConnectorRepository connectorRepo) {
        this.repo = repo;
        this.connectorRepo = connectorRepo;
    }
    
    public List<Station> list() {
        return repo.findAll();
    }
    
    public Optional<Station> getById(String id) {
        return repo.findById(id);
    }

    
    /**
     * Tìm trạm sạc trong bán kính radiusKm từ vị trí (lat, lng)
     */
    public List<Station> findNearby(double lat, double lng, double radiusKm, int limit) {
        List<Station> all = repo.findAll();
        
        return all.stream()
                .filter(s -> StationStatus.ONLINE.equals(s.getStatus()))
                .map(station -> new StationWithDistance(station, calculateDistance(lat, lng, station.getLat(), station.getLng())))
                .filter(swd -> swd.distance <= radiusKm)
                .sorted(Comparator.comparingDouble(swd -> swd.distance))
                .limit(limit)
                .map(swd -> swd.station)
                .toList();
    }

    /**
     * Tính khoảng cách giữa 2 điểm (Haversine formula)
     */
    private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        final double EARTH_RADIUS_KM = 6371.0;
        
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS_KM * c;
    }

    /**
     * Lấy danh sách connector available của trạm
     */
    @Transactional(readOnly = true)
    public List<ut.edu.evcs.project_java.web.dto.ConnectorInfoDTO> getAvailableConnectors(String stationId) {
        return connectorRepo.findAll().stream()
                .filter(c -> c.getChargingPoint() != null 
                        && c.getChargingPoint().getStation() != null
                        && c.getChargingPoint().getStation().getId().equals(stationId))
                .filter(c -> !c.isOccupied())
                .map(c -> new ut.edu.evcs.project_java.web.dto.ConnectorInfoDTO(
                    c.getId(),
                    c.getType(),
                    c.getMaxCurrentA(),
                    c.getVoltageV(),
                    c.isOccupied()
                ))
                .toList();
    }

    private static class StationWithDistance {
        final Station station;
        final double distance;
        
        StationWithDistance(Station station, double distance) {
            this.station = station;
            this.distance = distance;
        }
    }
}