package ut.edu.evcs.project_java.domain.station;

import jakarta.persistence.*;

@Entity
@Table(name = "charging_points")
public class ChargingPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String code;
    private double maxPowerKW;
    private boolean online;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id")
    private Station station;

    public ChargingPoint() {
    }

    public ChargingPoint(String id, String code, double maxPowerKW, boolean online, Station station) {
        this.id = id;
        this.code = code;
        this.maxPowerKW = maxPowerKW;
        this.online = online;
        this.station = station;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getMaxPowerKW() {
        return this.maxPowerKW;
    }

    public void setMaxPowerKW(double maxPowerKW) {
        this.maxPowerKW = maxPowerKW;
    }

    public boolean isOnline() {
        return this.online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public Station getStation() {
        return this.station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    @Override
    public String toString() {
        return "ChargingPoint{" +
                "id='" + id + '\'' +
                ", code='" + code + '\'' +
                ", maxPowerKW=" + maxPowerKW +
                ", online=" + online +
                '}';
    }
}
