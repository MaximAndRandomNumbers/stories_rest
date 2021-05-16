package ru.kuznetsov.stories.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.kuznetsov.stories.models.User;
import ru.kuznetsov.stories.security.jwt.JwtUserFactory;
import ru.kuznetsov.stories.services.data.interfaces.UserService;

@Component
public class AuthenticationProviderImpl implements AuthenticationProvider {

    private final UserService userService;
    private final PasswordEncoder bCrypt;

    @Autowired
    public AuthenticationProviderImpl(UserService userService, PasswordEncoder bCrypt) {
        this.userService = userService;
        this.bCrypt = bCrypt;
    }

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        User myUser = userService.findByLogin(username);
        if (myUser == null) {
            throw new BadCredentialsException("Unknown user " + username);
        }
        if (!bCrypt.matches(password, myUser.getPassword())) {
            throw new BadCredentialsException("Bad password");
        }
        if (!myUser.getEnabled()) {
            throw new BadCredentialsException("Please activate your account to log in");
        }
        UserDetails userDetails = JwtUserFactory.create(myUser);
        return new UsernamePasswordAuthenticationToken(
                userDetails, password, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
