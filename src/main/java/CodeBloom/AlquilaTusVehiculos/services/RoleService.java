package CodeBloom.AlquilaTusVehiculos.services;

import CodeBloom.AlquilaTusVehiculos.models.Role;
import CodeBloom.AlquilaTusVehiculos.models.User;
import CodeBloom.AlquilaTusVehiculos.repositories.RoleRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final UserService userService;

    public RoleService(RoleRepository roleRepository, UserService userService) {
        this.roleRepository = roleRepository;
        this.userService = userService;
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Optional<Role> getRoleById(Long id) {
        return roleRepository.findById(id);
    }

    public Optional<Role> getRoleByName(String name) {
        return roleRepository.findByName(name);
    }

    public Role saveRole(Role newRole) {
        return roleRepository.save(newRole);
    }

    public Role updateRole(Long id, Role roleDetails, User userAdmin) {
        Role role = roleRepository.findById(id).orElseThrow(() -> new RuntimeException("Role not found."));
        User admin = userService.getUserById(userAdmin.getId())
                .orElseThrow(() -> new RuntimeException("Admin user not found."));

        if (admin.getIsAdmin()) {
            role.setName(roleDetails.getName());
            return roleRepository.save(role);
        } else {
            throw new RuntimeException("Access denied: User is not and admin.");
        }
    }

    public void deleteRole(Long id, User userAdmin) {
        Role role = roleRepository.findById(id).orElseThrow(() -> new RuntimeException("Role not found."));
        User admin = userService.getUserById(userAdmin.getId())
                .orElseThrow(() -> new RuntimeException("Admin user not found."));

        if (admin.getIsAdmin()) {
            roleRepository.delete(role);
        } else {
            throw new RuntimeException("Access denied: User is not and admin.");
        }
    }
}
