package CodeBloom.AlquilaTusVehiculos.controllers.web;

import CodeBloom.AlquilaTusVehiculos.models.Rental;
import CodeBloom.AlquilaTusVehiculos.models.Vehicle;
import CodeBloom.AlquilaTusVehiculos.models.User;
import CodeBloom.AlquilaTusVehiculos.repositories.RentalRepository;
import CodeBloom.AlquilaTusVehiculos.repositories.UserRepository;
import CodeBloom.AlquilaTusVehiculos.repositories.VehicleRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Controller
@RequestMapping("/rentals")
public class RentalController {

    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;

    public RentalController(RentalRepository rentalRepository,
                            UserRepository userRepository,
                            VehicleRepository vehicleRepository) {
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
    }

    @GetMapping
    public String listRentals(Model model) {
        model.addAttribute("rentals", rentalRepository.findAll());
        return "rentals/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("rental", new Rental());
        loadFormData(model);
        return "rentals/create";
    }

    @PostMapping("/save")
    public String saveRental(@ModelAttribute Rental rental,
                             @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                             @RequestParam("estimatedReturnDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate estimatedReturnDate,
                             Model model) {

        if (rental.getUser() == null || rental.getUser().getId() == null) {
            model.addAttribute("error", "Debes seleccionar un cliente.");
            loadFormData(model, rental);
            return "rentals/create";
        }

        if (rental.getVehicle() == null || rental.getVehicle().getId() == null) {
            model.addAttribute("error", "Debes seleccionar un vehículo.");
            loadFormData(model, rental);
            return "rentals/create";
        }

        if (startDate == null || estimatedReturnDate == null) {
            model.addAttribute("error", "Debes indicar ambas fechas.");
            loadFormData(model, rental);
            return "rentals/create";
        }

        if (estimatedReturnDate.isBefore(startDate)) {
            model.addAttribute("error", "La fecha de fin no puede ser anterior a la fecha de inicio.");
            loadFormData(model, rental);
            return "rentals/create";
        }

        User user = userRepository.findById(rental.getUser().getId()).orElse(null);
        if (user == null) {
            model.addAttribute("error", "El cliente seleccionado no existe.");
            loadFormData(model, rental);
            return "rentals/create";
        }

        Vehicle vehicle = vehicleRepository.findById(rental.getVehicle().getId()).orElse(null);
        if (vehicle == null) {
            model.addAttribute("error", "El vehículo seleccionado no existe.");
            loadFormData(model, rental);
            return "rentals/create";
        }

        rental.setUser(user);
        rental.setVehicle(vehicle);

        rental.setStartDate(startDate.atStartOfDay());
        rental.setEstimatedReturnDate(estimatedReturnDate.atStartOfDay());

        long days = ChronoUnit.DAYS.between(startDate, estimatedReturnDate) + 1;

        BigDecimal totalPrice = BigDecimal.valueOf(vehicle.getDailyPrice())
                .multiply(BigDecimal.valueOf(days));

        rental.setPrice(totalPrice.doubleValue());

        rentalRepository.save(rental);

        return "redirect:/rentals/new";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Rental rental = rentalRepository.findById(id).orElse(null);

        if (rental == null) {
            return "redirect:/rentals/new";
        }

        model.addAttribute("rental", rental);
        loadFormData(model);
        return "rentals/create";
    }

    @GetMapping("/delete/{id}")
    public String deleteRental(@PathVariable Long id) {
        rentalRepository.deleteById(id);
        return "redirect:/rentals/new";
    }

    private void loadFormData(Model model) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("vehicles", vehicleRepository.findAll());
        model.addAttribute("rentals", rentalRepository.findAll());
    }

    private void loadFormData(Model model, Rental rental) {
        model.addAttribute("rental", rental);
        loadFormData(model);
    }

    @InitBinder("rental")
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields("startDate", "estimatedReturnDate", "returnDate");
    }
}