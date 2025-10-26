package ut.edu.evcs.project_java.domain.tariff;

import jakarta.persistence.*;

@Entity
@Table(name = "tariffs")
public class TariffPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;
    private String mode; // PER_KWH, PER_MINUTE, HYBRID, SUBSCRIPTION
    private double pricePerKWh;
    private double pricePerMinute;
    private double idleFeePerMinute;
    private String timeOfUseRules;
    private boolean active;

    public TariffPlan() {
    }

    public TariffPlan(String id, String name, String mode, double pricePerKWh, double pricePerMinute,
                      double idleFeePerMinute, String timeOfUseRules, boolean active) {
        this.id = id;
        this.name = name;
        this.mode = mode;
        this.pricePerKWh = pricePerKWh;
        this.pricePerMinute = pricePerMinute;
        this.idleFeePerMinute = idleFeePerMinute;
        this.timeOfUseRules = timeOfUseRules;
        this.active = active;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMode() {
        return this.mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public double getPricePerKWh() {
        return this.pricePerKWh;
    }

    public void setPricePerKWh(double pricePerKWh) {
        this.pricePerKWh = pricePerKWh;
    }

    public double getPricePerMinute() {
        return this.pricePerMinute;
    }

    public void setPricePerMinute(double pricePerMinute) {
        this.pricePerMinute = pricePerMinute;
    }

    public double getIdleFeePerMinute() {
        return this.idleFeePerMinute;
    }

    public void setIdleFeePerMinute(double idleFeePerMinute) {
        this.idleFeePerMinute = idleFeePerMinute;
    }

    public String getTimeOfUseRules() {
        return this.timeOfUseRules;
    }

    public void setTimeOfUseRules(String timeOfUseRules) {
        this.timeOfUseRules = timeOfUseRules;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "TariffPlan{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", mode='" + mode + '\'' +
                ", pricePerKWh=" + pricePerKWh +
                ", pricePerMinute=" + pricePerMinute +
                ", idleFeePerMinute=" + idleFeePerMinute +
                ", timeOfUseRules='" + timeOfUseRules + '\'' +
                ", active=" + active +
                '}';
    }
}

