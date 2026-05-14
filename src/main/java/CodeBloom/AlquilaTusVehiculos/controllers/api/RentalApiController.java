package CodeBloom.AlquilaTusVehiculos.controllers.api;

import CodeBloom.AlquilaTusVehiculos.models.Rental;
import CodeBloom.AlquilaTusVehiculos.services.RentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alquileres")
@Tag(name = "Alquileres", description = "Gestión de alquileres")
public class RentalApiController {

    private final RentalService rentalService;

    public RentalApiController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    // ✅ PÚBLICO
    @GetMapping
    @Operation(summary = "Listar alquileres activos",
            description = "Devuelve todos los alquileres activos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    })
    public ResponseEntity<List<Rental>> getAllRentals() {
        return ResponseEntity.ok(rentalService.getAllRentals());
    }

    // 🔒 SECURIZADO
    @PostMapping
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Crear nuevo alquiler", description = "Requiere token JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Alquiler creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos incorrectos o vehículo no disponible"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    public ResponseEntity<?> createRental(@RequestBody Rental rental) {
        try {
            return ResponseEntity.ok(rentalService.saveRental(rental));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}