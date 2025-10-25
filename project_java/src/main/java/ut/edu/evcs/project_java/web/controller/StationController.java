package ut.edu.evcs.project_java.web.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ut.edu.evcs.project_java.service.StationService;
import ut.edu.evcs.project_java.web.dto.StationDTO;
import ut.edu.evcs.project_java.web.mapper.StationMapper;

import java.util.List;

@RestController
@RequestMapping("/api/stations")
public class StationController {
    private final StationService service;
    private final StationMapper mapper;
    public StationController(StationService service, StationMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public List<StationDTO> list() {
        return service.list().stream().map(mapper::toDto).toList();
    }
}
