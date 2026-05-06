package uk.ac.cf._5.group14.One_To_One.Security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import uk.ac.cf._5.group14.One_To_One.Users.User;

@Component
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final ObjectProvider<CurrentUserResolver> currentUserResolverProvider;

    public CurrentUserArgumentResolver(ObjectProvider<CurrentUserResolver> currentUserResolverProvider) {
        this.currentUserResolverProvider = currentUserResolverProvider;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class)
                && User.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        HttpSession session = request != null ? request.getSession(false) : null;
        CurrentUser annotation = parameter.getParameterAnnotation(CurrentUser.class);
        CurrentUserResolver currentUserResolver = currentUserResolverProvider.getIfAvailable();

        User user = currentUserResolver != null
                ? currentUserResolver.resolveCurrentUser(authentication, session)
                : (session != null && session.getAttribute("user") instanceof User sessionUser ? sessionUser : null);
        if (user == null && annotation != null && annotation.required()) {
            if (currentUserResolver != null) {
                return currentUserResolver.requireCurrentUser(authentication, session);
            }
            throw new org.springframework.security.access.AccessDeniedException("Authenticated user not found");
        }
        return user;
    }
}
