package ut.edu.evcs.project_java.domain.station;

public enum StationStatus {
    ONLINE,
    OFFLINE,
    MAINTENANCE;

    public boolean isAvailableForCharging() {
        return this == ONLINE;
    }

    public static StationStatus fromStringSafe(String value) {
        if (value == null) {
            return null;
        }
        try {
            return StationStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
