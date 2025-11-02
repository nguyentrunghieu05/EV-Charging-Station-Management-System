package ut.edu.evcs.project_java.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "stations")
public class Station {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String address;

    @Column(nullable = false)
    private double lat;

    @Column(nullable = false)
    private double lng;

    @Column(name = "available_ports", nullable = false)
    private int availablePorts;

    public Station() {
    }

    public Station(Long id, String name, String address, double lat, double lng, int availablePorts) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.availablePorts = availablePorts;
    }

    public static StationBuilder builder() {
        return new StationBuilder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public int getAvailablePorts() {
        return availablePorts;
    }

    public void setAvailablePorts(int availablePorts) {
        this.availablePorts = availablePorts;
    }

    public static class StationBuilder {
        private Long id;
        private String name;
        private String address;
        private double lat;
        private double lng;
        private int availablePorts;

        StationBuilder() {
        }

        public StationBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public StationBuilder name(String name) {
            this.name = name;
            return this;
        }

        public StationBuilder address(String address) {
            this.address = address;
            return this;
        }

        public StationBuilder lat(double lat) {
            this.lat = lat;
            return this;
        }

        public StationBuilder lng(double lng) {
            this.lng = lng;
            return this;
        }

        public StationBuilder availablePorts(int availablePorts) {
            this.availablePorts = availablePorts;
            return this;
        }

        public Station build() {
            return new Station(id, name, address, lat, lng, availablePorts);
        }

        @Override
        public String toString() {
            return "Station.StationBuilder(id=" + id + ", name=" + name + ", address=" + address + ", lat=" + lat + ", lng=" + lng + ", availablePorts=" + availablePorts + ")";
        }
    }

    @Override
    public String toString() {
        return "Station(id=" + id + ", name=" + name + ", address=" + address + ", lat=" + lat + ", lng=" + lng + ", availablePorts=" + availablePorts + ")";
    }
}
