package ut.edu.evcs.project_java.service.event;

import ut.edu.evcs.project_java.domain.session.Reservation;

public class ReservationCancelledEvent {

    private final Reservation reservation;

    public ReservationCancelledEvent(Reservation reservation) {
        this.reservation = reservation;
    }

    public Reservation getReservation() {
        return reservation;
    }
}
