package CodeBloom.AlquilaTusVehiculos.services;

import CodeBloom.AlquilaTusVehiculos.models.Rental;
import CodeBloom.AlquilaTusVehiculos.models.User;
import CodeBloom.AlquilaTusVehiculos.models.Vehicle;
import CodeBloom.AlquilaTusVehiculos.repositories.RentalRepository;
import CodeBloom.AlquilaTusVehiculos.repositories.UserRepository;
import CodeBloom.AlquilaTusVehiculos.repositories.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class RentalService {

    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;

    public RentalService(RentalRepository rentalRepository,
                         UserRepository userRepository,
                         VehicleRepository vehicleRepository) {
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public List<Rental> getAllRentals() {
        return rentalRepository.findAll();
    }

    public List<Rental> getAllEnabledRentals() {
        return rentalRepository.findByEnabledTrue();
    }

    public Optional<Rental> getRentalById(Long id) {
        return rentalRepository.findById(id);
    }

    @Transactional
    public Rental saveRental(Rental newRental) {
        if (newRental.getUser() == null || newRental.getUser().getId() == null) {
            throw new IllegalArgumentException("Debes seleccionar un cliente.");
        }

        if (newRental.getVehicle() == null || newRental.getVehicle().getId() == null) {
            throw new IllegalArgumentException("Debes seleccionar un vehículo.");
        }

        validateRentalDates(newRental.getStartDate(), newRental.getEstimatedReturnDate());

        User user = userRepository.findById(newRental.getUser().getId())
                .orElseThrow(() -> new IllegalArgumentException("El cliente seleccionado no existe."));

        Vehicle vehicle = vehicleRepository.findById(newRental.getVehicle().getId())
                .orElseThrow(() -> new IllegalArgumentException("El vehículo seleccionado no existe."));

        if (!isVehicleAvailable(vehicle.getId(), newRental.getStartDate(), newRental.getEstimatedReturnDate(), null)) {
            throw new IllegalArgumentException("El vehículo no está disponible en estas fechas.");
        }

        newRental.setUser(user);
        newRental.setVehicle(vehicle);
        newRental.setEnabled(true);
        newRental.setPrice(calculateTotalPrice(newRental.getStartDate(), newRental.getEstimatedReturnDate(), vehicle.getDailyPrice()));

        return rentalRepository.save(newRental);
    }

    @Transactional
    public Rental updateRental(Long id, Rental rentalDetails) {
        Rental rental = rentalRepository.findById(id).orElseThrow(() -> new RuntimeException("Rental not found."));

        validateRentalDates(rentalDetails.getStartDate(), rentalDetails.getEstimatedReturnDate());

        if (!isVehicleAvailable(rental.getVehicle().getId(), rentalDetails.getStartDate(), rentalDetails.getEstimatedReturnDate(), id)) {
            throw new IllegalArgumentException("El vehículo no está disponible en las nuevas fechas.");
        }

        rental.setStartDate(rentalDetails.getStartDate());
        rental.setEstimatedReturnDate(rentalDetails.getEstimatedReturnDate());
        rental.setNote(rentalDetails.getNote());
        rental.setPrice(calculateTotalPrice(rentalDetails.getStartDate(), rentalDetails.getEstimatedReturnDate(), rentalDetails.getVehicle().getDailyPrice()));

        return rentalRepository.save(rental);
    }

    @Transactional
    public void softDeleteRental(Long id) {
        Rental rental = rentalRepository.findById(id).orElseThrow(() -> new RuntimeException("Rental not found."));
        rental.setEnabled(false);
        rentalRepository.save(rental);
    }

    @Transactional
    public void hardDeleteRental(Long id) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rental not found."));

        rental.setUser(null);
        rental.setVehicle(null);

        rentalRepository.save(rental);

        rentalRepository.delete(rental);
    }

    private BigDecimal calculateTotalPrice(LocalDateTime startDate, LocalDateTime estimatedReturnDate, double dailyPrice) {
        long days = ChronoUnit.DAYS.between(startDate, estimatedReturnDate) + 1;
        return BigDecimal.valueOf(dailyPrice).multiply(BigDecimal.valueOf(days));
    }

    private boolean isVehicleAvailable(Long vehicleId, LocalDateTime start, LocalDateTime end, Long currentRentalId) {
        List<Rental> activeRentals = rentalRepository.findByVehicleIdAndEnabledTrue(vehicleId);

        return activeRentals.stream()
                .filter(r -> currentRentalId == null || !r.getId().equals(currentRentalId))
                .noneMatch(r -> start.isBefore(r.getEstimatedReturnDate()) && end.isAfter(r.getStartDate()));
    }

    private void validateRentalDates(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Debes indicar ambas fechas.");
        }

        if (start.toLocalDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser anterior a hoy.");
        }

        if (end.isBefore(start)) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la fecha de inicio.");
        }
    }
}
