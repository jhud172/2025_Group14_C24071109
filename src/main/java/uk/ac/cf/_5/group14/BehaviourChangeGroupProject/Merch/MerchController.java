package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Merch;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

@Controller
@RequestMapping("/merch")
public class MerchController {

    private final MerchProductService productService;
    private final AuthHelper authHelper;

    public MerchController(MerchProductService productService, AuthHelper authHelper) {
        this.productService = productService;
        this.authHelper = authHelper;
    }

    @GetMapping
    public ModelAndView shop() {
        User user = authHelper.getAuthenticatedUser();
        ModelAndView mav = new ModelAndView("merch/shop");
        mav.addObject("products", productService.getActiveProducts());
        mav.addObject("user", user);
        return mav;
    }
}
