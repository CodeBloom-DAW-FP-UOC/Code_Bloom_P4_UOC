package CodeBloom.AlquilaTusVehiculos.controllers.web;

import CodeBloom.AlquilaTusVehiculos.models.Vehicle;
import CodeBloom.AlquilaTusVehiculos.repositories.VehicleRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/vehicles")
public class VehicleController {

    private final VehicleRepository vehicleRepository;

    public VehicleController(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @GetMapping
    public String listVehicles(Model model) {
        model.addAttribute("vehicles", vehicleRepository.findAll());
        return "vehicles/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("vehicle", new Vehicle());
        return "vehicles/form";
    }

    @PostMapping("/save")
    public String saveVehicle(@ModelAttribute Vehicle vehicle) {
        vehicleRepository.save(vehicle);
        return "redirect:/vehicles";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Vehicle vehicle = vehicleRepository.findById(id).orElse(null);

        if (vehicle == null) {
            return "redirect:/vehicles";
        }

        model.addAttribute("vehicle", vehicle);
        return "vehicles/form";
    }

    @GetMapping("/delete/{id}")
    public String deleteVehicle(@PathVariable Long id) {
        vehicleRepository.deleteById(id);
        return "redirect:/vehicles";
    }
}