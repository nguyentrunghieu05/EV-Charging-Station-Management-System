package ut.edu.evcs.project_java.domain.tariff;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tariffs")
public class TariffPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;
    private String mode; // PER_KWH, PER_MINUTE, HYBRID, SUBSCRIPTION
    @Column(name = "price_per_kwh", nullable = false)
    private BigDecimal pricePerKWh = BigDecimal.ZERO;

    @Column(name = "price_per_minute", nullable = false)
    private BigDecimal pricePerMinute = BigDecimal.ZERO;

    @Column(name = "idle_fee_per_minute", nullable = false)
    private BigDecimal idleFeePerMinute = BigDecimal.ZERO;
    private String timeOfUseRules;
    private boolean active;

    public TariffPlan() {
    }

    public TariffPlan(String id, String name, String mode, BigDecimal pricePerKWh, BigDecimal pricePerMinute,
                      BigDecimal idleFeePerMinute, String timeOfUseRules, boolean active) {
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

    public BigDecimal getPricePerKWh() {
        return this.pricePerKWh;
    }

    public void setPricePerKWh(BigDecimal pricePerKWh) {
        this.pricePerKWh = pricePerKWh;
    }

    public BigDecimal getPricePerMinute() {
        return this.pricePerMinute;
    }

    public void setPricePerMinute(BigDecimal pricePerMinute) {
        this.pricePerMinute = pricePerMinute;
    }

    public BigDecimal getIdleFeePerMinute() {
        return this.idleFeePerMinute;
    }

    public void setIdleFeePerMinute(BigDecimal idleFeePerMinute) {
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

