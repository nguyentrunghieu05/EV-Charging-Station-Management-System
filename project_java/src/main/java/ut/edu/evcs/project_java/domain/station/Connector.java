package ut.edu.evcs.project_java.domain.station;

import jakarta.persistence.*;

@Entity
@Table(name = "connectors")
public class Connector {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String type; // CCS / CHAdeMO / AC
    private double maxCurrentA;
    private double voltageV;
    private boolean occupied;
    private String qrCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "point_id")
    private ChargingPoint chargingPoint;

    public Connector() {
    }

    public Connector(String id, String type, double maxCurrentA, double voltageV, boolean occupied, String qrCode, ChargingPoint chargingPoint) {
        this.id = id;
        this.type = type;
        this.maxCurrentA = maxCurrentA;
        this.voltageV = voltageV;
        this.occupied = occupied;
        this.qrCode = qrCode;
        this.chargingPoint = chargingPoint;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getMaxCurrentA() {
        return this.maxCurrentA;
    }

    public void setMaxCurrentA(double maxCurrentA) {
        this.maxCurrentA = maxCurrentA;
    }

    public double getVoltageV() {
        return this.voltageV;
    }

    public void setVoltageV(double voltageV) {
        this.voltageV = voltageV;
    }

    public boolean isOccupied() {
        return this.occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    public String getQrCode() {
        return this.qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public ChargingPoint getChargingPoint() {
        return this.chargingPoint;
    }

    public void setChargingPoint(ChargingPoint chargingPoint) {
        this.chargingPoint = chargingPoint;
    }

    @Override
    public String toString() {
        return "Connector{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", maxCurrentA=" + maxCurrentA +
                ", voltageV=" + voltageV +
                ", occupied=" + occupied +
                ", qrCode='" + qrCode + '\'' +
                '}';
    }
}

