package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    // signup
    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("user", new User());
        return "User/signup";
    }

    @PostMapping("/signup")
    public String signupUser(@Valid @ModelAttribute("user") User user,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirectAttributes) { // <--- 1. Add this argument

        // check for validation errors
        if (result.hasErrors()) {
            return "User/signup";
        }

        // check whether email exists
        if (userService.emailExists(user.getEmail())) {
            model.addAttribute("error", "Email already registered!");
            return "User/signup";
        }

        // check whether username exists
        if (userService.usernameExists(user.getUsername())) {
            model.addAttribute("error", "Username already taken!");
            return "User/signup";
        }

        // save user to database
        userService.saveUser(user);

        return "redirect:/login?registered";
    }

    // login page
    @GetMapping("/login")
    public String showLoginForm() {
        return "User/login";
    }

// post mapping - login with userdetailsservice

//    @GetMapping("/")
//    public String homepage(HttpSession session, Model model) {
//        // check whether user is logged in
//        User user = (User) session.getAttribute("user");
//        if (user == null) {
//            return "redirect:/login";
//        }
//
//        model.addAttribute("user", user);
//        return "index";
//    }

    // logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}