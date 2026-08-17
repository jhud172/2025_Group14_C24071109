package uk.ac.cf._5.group14.One_To_One.Birthday;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BirthdayController {

    public static final String BIRTHDAY_PATH = "/birthday/mission-vi";

    @GetMapping(BIRTHDAY_PATH)
    public String missionVi(Model model) {
        model.addAttribute("pageTitle", "Mission VI - Happy Birthday Dad");
        model.addAttribute("pageDescription", "A private birthday mission and a promise for the launch of Grand Theft Auto VI.");
        return "birthday/mission-vi";
    }
}
