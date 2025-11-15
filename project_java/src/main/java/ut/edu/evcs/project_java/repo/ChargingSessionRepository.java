package ut.edu.evcs.project_java.repo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ut.edu.evcs.project_java.domain.session.ChargingSession;

public interface ChargingSessionRepository extends JpaRepository<ChargingSession, String> {

    List<ChargingSession> findByStatusIn(List<String> statuses);

    @Query(value = """
            SELECT COUNT(*)
            FROM sessions s
            WHERE s.start_time BETWEEN :from AND :to
            """, nativeQuery = true)
    long countSessionsBetween(@Param("from") LocalDateTime from,
                              @Param("to") LocalDateTime to);

    @Query(value = """
            SELECT COUNT(*)
            FROM sessions s
            WHERE s.status = 'COMPLETED'
              AND s.start_time BETWEEN :from AND :to
            """, nativeQuery = true)
    long countCompletedSessionsBetween(@Param("from") LocalDateTime from,
                                       @Param("to") LocalDateTime to);

    @Query(value = """
            SELECT COUNT(*)
            FROM sessions s
            WHERE s.status = 'ACTIVE'
              AND s.start_time BETWEEN :from AND :to
            """, nativeQuery = true)
    long countActiveSessionsBetween(@Param("from") LocalDateTime from,
                                    @Param("to") LocalDateTime to);

    @Query(value = """
            SELECT COALESCE(SUM(s.kwh_delivered), 0)
            FROM sessions s
            WHERE s.status = 'COMPLETED'
              AND s.start_time BETWEEN :from AND :to
            """, nativeQuery = true)
    BigDecimal sumKwhBetween(@Param("from") LocalDateTime from,
                             @Param("to") LocalDateTime to);

    @Query(value = """
            SELECT DATE(s.start_time)                       AS day,
                   COALESCE(SUM(s.total_cost), 0)           AS total_revenue,
                   COALESCE(SUM(s.energy_cost), 0)          AS energy_revenue,
                   COALESCE(SUM(s.time_cost), 0)            AS time_revenue,
                   COALESCE(SUM(s.idle_fee), 0)             AS idle_revenue
            FROM sessions s
            WHERE s.status = 'COMPLETED'
              AND s.start_time BETWEEN :from AND :to
            GROUP BY DATE(s.start_time)
            ORDER BY day
            """, nativeQuery = true)
    List<Object[]> revenueByDay(@Param("from") LocalDateTime from,
                                @Param("to") LocalDateTime to);

    @Query(value = """
            SELECT HOUR(s.start_time)                       AS hour_of_day,
                   COUNT(*)                                 AS sessions_count,
                   COALESCE(SUM(s.kwh_delivered), 0)        AS total_kwh
            FROM sessions s
            WHERE s.status = 'COMPLETED'
              AND s.start_time BETWEEN :from AND :to
            GROUP BY HOUR(s.start_time)
            ORDER BY sessions_count DESC
            """, nativeQuery = true)
    List<Object[]> peakHours(@Param("from") LocalDateTime from,
                             @Param("to") LocalDateTime to);

    @Query(value = """
            SELECT AVG(TIMESTAMPDIFF(MINUTE, s.start_time, s.end_time))
            FROM sessions s
            WHERE s.status = 'COMPLETED'
              AND s.start_time BETWEEN :from AND :to
              AND s.end_time IS NOT NULL
            """, nativeQuery = true)
    Double avgDurationMinutes(@Param("from") LocalDateTime from,
                              @Param("to") LocalDateTime to);
}
