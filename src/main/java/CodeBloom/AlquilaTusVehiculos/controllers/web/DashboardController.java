package CodeBloom.AlquilaTusVehiculos.controllers.web;

import CodeBloom.AlquilaTusVehiculos.repositories.RentalRepository;
import CodeBloom.AlquilaTusVehiculos.repositories.UserRepository;
import CodeBloom.AlquilaTusVehiculos.repositories.VehicleRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class DashboardController {

    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final RentalRepository rentalRepository;

    public DashboardController(UserRepository userRepository,
                               VehicleRepository vehicleRepository,
                               RentalRepository rentalRepository) {
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.rentalRepository = rentalRepository;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalUsers", userRepository.count());
        model.addAttribute("totalVehicles", vehicleRepository.count());
        model.addAttribute("totalRentals", rentalRepository.count());
        return "dashboard/dashboard";
    }

}