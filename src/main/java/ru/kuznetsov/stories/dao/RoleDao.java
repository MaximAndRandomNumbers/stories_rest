package ru.kuznetsov.stories.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kuznetsov.stories.models.Role;

@Repository
public interface RoleDao extends JpaRepository<Role,Long> {
    Role findByRoleName(String name);
}
