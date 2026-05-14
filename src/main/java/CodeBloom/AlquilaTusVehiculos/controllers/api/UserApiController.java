package CodeBloom.AlquilaTusVehiculos.controllers.api;

import CodeBloom.AlquilaTusVehiculos.models.User;
import CodeBloom.AlquilaTusVehiculos.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@Tag(name = "Clientes", description = "Gestión de clientes/usuarios")
public class UserApiController {

    private final UserService userService;

    public UserApiController(UserService userService) {
        this.userService = userService;
    }

    // ✅ PÚBLICO
    @GetMapping
    @Operation(summary = "Listar todos los clientes",
            description = "Devuelve la lista completa de clientes registrados")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    })
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // 🔒 SECURIZADO
    @GetMapping("/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Obtener cliente por ID", description = "Requiere token JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🔒 SECURIZADO
    @PutMapping("/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Actualizar cliente", description = "Requiere token JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente actualizado"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    public ResponseEntity<User> updateUser(@PathVariable Long id,
                                           @RequestBody User user) {
        try {
            return ResponseEntity.ok(userService.updateUser(id, user));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}