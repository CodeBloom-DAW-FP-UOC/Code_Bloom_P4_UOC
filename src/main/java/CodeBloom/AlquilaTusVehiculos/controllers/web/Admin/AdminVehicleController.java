package CodeBloom.AlquilaTusVehiculos.controllers.web.Admin;

import CodeBloom.AlquilaTusVehiculos.models.Vehicle;
import CodeBloom.AlquilaTusVehiculos.services.RentalService;
import CodeBloom.AlquilaTusVehiculos.services.VehicleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/vehicles")
public class AdminVehicleController {

    private final VehicleService vehicleService;
    private final RentalService rentalService;

    public AdminVehicleController(VehicleService vehicleService,
                                  RentalService rentalService) {
        this.vehicleService = vehicleService;
        this.rentalService = rentalService;
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("vehicle", new Vehicle());
        return "vehicles/form";
    }

    @PostMapping("/save")
    public String saveVehicle(@ModelAttribute Vehicle vehicle) {
        vehicleService.saveVehicle(vehicle);
        return "redirect:/vehicles";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Vehicle vehicle = vehicleService.getVehicleById(id).orElse(null);
        if (vehicle == null) return "redirect:/vehicles";
        model.addAttribute("vehicle", vehicle);
        return "vehicles/form";
    }

    @GetMapping("/delete/{id}")
    public String deleteVehicle(@PathVariable Long id) {
        vehicleService.getVehicleById(id).ifPresent(vehicle -> {
            vehicle.getRentals().forEach(rental -> {
                rental.setVehicle(null);
                rentalService.saveRental(rental);
            });
            vehicleService.deleteVehicle(vehicle.getId());
        });
        return "redirect:/vehicles";
    }

    @GetMapping
    public String listVehicles(Model model) {
        model.addAttribute("vehicles", vehicleService.getAllVehicles());
        return "vehicles/list";
    }
}