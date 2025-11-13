package ut.edu.evcs.project_java.service.event;

import ut.edu.evcs.project_java.domain.session.Reservation;

public class ReservationCreatedEvent {

    private final Reservation reservation;

    public ReservationCreatedEvent(Reservation reservation) {
        this.reservation = reservation;
    }

    public Reservation getReservation() {
        return reservation;
    }
}
