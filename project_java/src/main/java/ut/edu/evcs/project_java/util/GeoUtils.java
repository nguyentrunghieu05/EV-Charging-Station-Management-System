package ut.edu.evcs.project_java.util;

/**
 * Utilities cho tính toán địa lý
 */
public final class GeoUtils {
    
    private GeoUtils() {
        throw new AssertionError("Utility class");
    }
    
    /** Bán kính Trái Đất (km) */
    private static final double EARTH_RADIUS_KM = 6371.0;
    
    /**
     * Tính khoảng cách giữa 2 điểm sử dụng công thức Haversine
     * 
     * @param lat1 Vĩ độ điểm 1
     * @param lng1 Kinh độ điểm 1
     * @param lat2 Vĩ độ điểm 2
     * @param lng2 Kinh độ điểm 2
     * @return Khoảng cách (km)
     */
    public static double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        // Chuyển độ sang radian
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        
        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);
        
        // Công thức Haversine
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS_KM * c;
    }
    
    /**
     * Làm tròn khoảng cách về 2 chữ số thập phân
     */
    public static double roundDistance(double distance) {
        return Math.round(distance * 100.0) / 100.0;
    }
    
    /**
     * Kiểm tra điểm có nằm trong bán kính không
     * 
     * @param centerLat Vĩ độ tâm
     * @param centerLng Kinh độ tâm
     * @param pointLat Vĩ độ điểm cần check
     * @param pointLng Kinh độ điểm cần check
     * @param radiusKm Bán kính (km)
     * @return true nếu điểm nằm trong bán kính
     */
    public static boolean isWithinRadius(double centerLat, double centerLng, 
                                        double pointLat, double pointLng, 
                                        double radiusKm) {
        double distance = calculateDistance(centerLat, centerLng, pointLat, pointLng);
        return distance <= radiusKm;
    }
    
    /**
     * Tính bounding box (hình chữ nhật) để giới hạn tìm kiếm
     * Trả về [minLat, maxLat, minLng, maxLng]
     * 
     * @param centerLat Vĩ độ tâm
     * @param centerLng Kinh độ tâm
     * @param radiusKm Bán kính (km)
     * @return double[] {minLat, maxLat, minLng, maxLng}
     */
    public static double[] getBoundingBox(double centerLat, double centerLng, double radiusKm) {
        // Độ lat/lng tương ứng với 1km (xấp xỉ)
        double latDegreePerKm = 1.0 / 111.0;
        double lngDegreePerKm = 1.0 / (111.0 * Math.cos(Math.toRadians(centerLat)));
        
        double latDelta = radiusKm * latDegreePerKm;
        double lngDelta = radiusKm * lngDegreePerKm;
        
        return new double[] {
            centerLat - latDelta,  // minLat
            centerLat + latDelta,  // maxLat
            centerLng - lngDelta,  // minLng
            centerLng + lngDelta   // maxLng
        };
    }
    
    /**
     * Format khoảng cách thành chuỗi dễ đọc
     * 
     * @param distanceKm Khoảng cách (km)
     * @return "2.5 km" hoặc "500 m"
     */
    public static String formatDistance(double distanceKm) {
        if (distanceKm < 1.0) {
            int meters = (int) (distanceKm * 1000);
            return meters + " m";
        } else {
            return String.format("%.1f km", distanceKm);
        }
    }
}