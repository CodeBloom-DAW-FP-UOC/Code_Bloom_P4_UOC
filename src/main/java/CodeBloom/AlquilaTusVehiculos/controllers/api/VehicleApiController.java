package CodeBloom.AlquilaTusVehiculos.controllers.api;

import CodeBloom.AlquilaTusVehiculos.models.Vehicle;
import CodeBloom.AlquilaTusVehiculos.services.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehiculos")
@Tag(name = "Vehículos", description = "Gestión de vehículos")
public class VehicleApiController {

    private final VehicleService vehicleService;

    public VehicleApiController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    // ✅ PÚBLICO
    @GetMapping
    @Operation(summary = "Listar todos los vehículos",
            description = "Devuelve la lista completa de vehículos disponibles")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    })
    public ResponseEntity<List<Vehicle>> getAllVehicles() {
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }

    // ✅ PÚBLICO
    @GetMapping("/{id}")
    @Operation(summary = "Obtener vehículo por ID",
            description = "Devuelve un vehículo concreto por su ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vehículo encontrado"),
            @ApiResponse(responseCode = "404", description = "Vehículo no encontrado")
    })
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable Long id) {
        return vehicleService.getVehicleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🔒 SECURIZADO
    @PutMapping("/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Actualizar vehículo", description = "Requiere token JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vehículo actualizado"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Vehículo no encontrado")
    })
    public ResponseEntity<Vehicle> updateVehicle(@PathVariable Long id,
                                                 @RequestBody Vehicle vehicle) {
        try {
            return ResponseEntity.ok(vehicleService.updateVehicle(id, vehicle));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 🔒 SECURIZADO
    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Eliminar vehículo", description = "Requiere token JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Vehículo eliminado"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Vehículo no encontrado")
    })
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        try {
            vehicleService.deleteVehicle(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}