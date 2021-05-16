package ru.kuznetsov.stories.security.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.kuznetsov.stories.models.User;
import ru.kuznetsov.stories.services.data.interfaces.UserService;

@Service("userDetailsServiceImpl")
public class JwtUserDetailsService implements UserDetailsService {
    private final UserService userService;

    @Autowired
    public JwtUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userService.findByLogin(s);
        if (user == null) {
            throw new UsernameNotFoundException("User with username: " + s + " not found");
        }
        return JwtUserFactory.create(user);
    }

}
