package uk.ac.cf._5.group14.One_To_One.Config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import uk.ac.cf._5.group14.One_To_One.Users.AuthHelper;
import uk.ac.cf._5.group14.One_To_One.Users.User;

@ControllerAdvice
public class RoleSurfaceModelAdvice {

    private final AuthHelper authHelper;

    public RoleSurfaceModelAdvice(AuthHelper authHelper) {
        this.authHelper = authHelper;
    }

    @ModelAttribute("roleSurface")
    public RoleSurfaceContext roleSurface() {
        User user = authHelper.getAuthenticatedUser();
        return user == null ? RoleSurfaceContext.guest() : RoleSurfaceContext.forRole(user.getRole());
    }
}
