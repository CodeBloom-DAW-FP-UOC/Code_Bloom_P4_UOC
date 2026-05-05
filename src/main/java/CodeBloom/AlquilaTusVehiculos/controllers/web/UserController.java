package CodeBloom.AlquilaTusVehiculos.controllers.web;

import CodeBloom.AlquilaTusVehiculos.models.User;
import CodeBloom.AlquilaTusVehiculos.repositories.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("user", new User());
        return "users/users";
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute User user) {
        if (user.getId() == null) {
            user.setIsAdmin(false);
        } else {
            Optional<User> existingUser = userRepository.findById(user.getId());
            existingUser.ifPresent(existing -> user.setIsAdmin(existing.getIsAdmin()));
        }

        userRepository.save(user);
        return "redirect:/users";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty()) {
            return "redirect:/users";
        }

        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("user", user.get());
        return "users/users";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "redirect:/users";
    }
}