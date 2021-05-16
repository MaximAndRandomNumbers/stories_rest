package ru.kuznetsov.stories.services.data.interfaces;

import org.springframework.stereotype.Component;
import ru.kuznetsov.stories.models.Role;

@Component
public interface RoleService {
    Role getRoleById(Long id);
    Role getRoleByName(String name);
}
