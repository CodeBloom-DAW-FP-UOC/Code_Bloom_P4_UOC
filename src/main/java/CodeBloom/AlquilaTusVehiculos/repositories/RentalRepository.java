package CodeBloom.AlquilaTusVehiculos.repositories;

import CodeBloom.AlquilaTusVehiculos.models.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {
    List<Rental> findByEnabledTrue();
    List<Rental> findByVehicleIdAndEnabledTrue(Long id);
    @Query("SELECT COUNT(r) > 0 FROM Rental r WHERE r.vehicle.id = :vehicleId "+
            "AND r.enabled = true " +
            "AND :startDate < r.estimatedReturnDate " +
            "AND :endDate > r.startDate")
    boolean existsOverlappingRentals(@Param("vehicleId") Long vehicleId,
                                     @Param("startDate")LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);
}