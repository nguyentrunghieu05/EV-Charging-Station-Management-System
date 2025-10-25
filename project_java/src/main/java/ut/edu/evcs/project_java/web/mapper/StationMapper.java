package ut.edu.evcs.project_java.web.mapper;

import org.mapstruct.Mapper;
import ut.edu.evcs.project_java.domain.station.Station;
import ut.edu.evcs.project_java.web.dto.StationDTO;

@Mapper(componentModel = "spring")
public interface StationMapper {
    StationDTO toDto(Station s);
}
