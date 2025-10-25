package ut.edu.evcs.project_java.service;

import org.springframework.stereotype.Service;
import ut.edu.evcs.project_java.domain.station.Station;
import ut.edu.evcs.project_java.repo.StationRepository;

import java.util.List;

@Service
public class StationService {
    private final StationRepository repo;
    public StationService(StationRepository repo) {
        this.repo = repo;
    }
    public List<Station> list() {
        return repo.findAll();
    }
}