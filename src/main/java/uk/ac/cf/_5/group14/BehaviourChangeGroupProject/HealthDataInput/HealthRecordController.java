package uk.ac.cf._5.group14.BehaviourChangeGroupProject.HealthDataInput;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import jakarta.validation.Valid;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.PathVariable;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.HealthDataInput.PhysicalCondition.PhysicalCondition;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.HealthDataInput.PhysicalCondition.PhysicalConditionService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserService;

import java.util.List;

@Controller
public class HealthRecordController {

    private final HealthRecordService healthRecordService;
    private final PhysicalConditionService physicalConditionService;
    private final UserService userService;

    @Autowired
    private AuthHelper authHelper;

    public HealthRecordController(HealthRecordService healthRecordService, PhysicalConditionService physicalConditionService, UserService userService) {
        this.healthRecordService = healthRecordService;
        this.physicalConditionService = physicalConditionService;
        this.userService = userService;
    }

    @GetMapping("/health-record")
    public ModelAndView getHealthDataForm() {
        ModelAndView modelAndView = new ModelAndView("health-record/health-record-form");
        User user = authHelper.getAuthenticatedUser();

        HealthRecordForm emptyHealthRecordForm = healthRecordService.createHealthRecordForm(user);
        List<PhysicalCondition> allPhysicalConditions = physicalConditionService.getAllPhysicalConditions();

        modelAndView.addObject("healthRecordForm", emptyHealthRecordForm);
        modelAndView.addObject("allPhysicalConditions", allPhysicalConditions);
        return modelAndView;
    }

    @PostMapping("/health-record")
    public ModelAndView postHealthDataForm(
            @Valid @ModelAttribute("healthRecordForm") HealthRecordForm healthRecordForm,
            BindingResult bindingResult,
            Model model) {

        User user = authHelper.getAuthenticatedUser();

        if (bindingResult.hasErrors()) {
            model.addAttribute("allPhysicalConditions", physicalConditionService.getAllPhysicalConditions());
            return new ModelAndView("health-record/health-record-form", model.asMap());
        }

        healthRecordService.addHealthRecord(healthRecordForm,user);
        return new ModelAndView("redirect:/health-record/list?success");
    }


    @GetMapping("/health-record/list")
    public ModelAndView listHealthRecords() {
        User user = authHelper.getAuthenticatedUser();
        ModelAndView mav = new ModelAndView("health-record/health-record-list");
        mav.addObject("records", healthRecordService.getAllHealthRecords(user));
        return mav;
    }

    @GetMapping("/health-record/{id}")
    public ModelAndView viewHealthRecord(@PathVariable Long id) {
        ModelAndView mav = new ModelAndView("health-record/health-record-view");
        mav.addObject("record", healthRecordService.getHealthRecordById(id));
        return mav;
    }





}
