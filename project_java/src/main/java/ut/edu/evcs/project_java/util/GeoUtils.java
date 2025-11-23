package ut.edu.evcs.project_java.util;

public final class GeoUtils {

    private GeoUtils() {
        throw new AssertionError("Utility class");
    }

    private static final double EARTH_RADIUS_KM = 6371.0;

    public static double calculateDistance(double lat1, double lng1, double lat2, double lng2) {

        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                        * Math.sin(dLng / 2) * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    public static double roundDistance(double distance) {
        return Math.round(distance * 100.0) / 100.0;
    }

    public static boolean isWithinRadius(double centerLat, double centerLng,
            double pointLat, double pointLng,
            double radiusKm) {
        double distance = calculateDistance(centerLat, centerLng, pointLat, pointLng);
        return distance <= radiusKm;
    }

    public static double[] getBoundingBox(double centerLat, double centerLng, double radiusKm) {
        double latDegreePerKm = 1.0 / 111.0;
        double lngDegreePerKm = 1.0 / (111.0 * Math.cos(Math.toRadians(centerLat)));

        double latDelta = radiusKm * latDegreePerKm;
        double lngDelta = radiusKm * lngDegreePerKm;

        return new double[] {
                centerLat - latDelta,
                centerLat + latDelta,
                centerLng - lngDelta,
                centerLng + lngDelta
        };
    }

    public static String formatDistance(double distanceKm) {
        if (distanceKm < 1.0) {
            int meters = (int) (distanceKm * 1000);
            return meters + " m";
        } else {
            return String.format("%.1f km", distanceKm);
        }
    }
}