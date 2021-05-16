package ru.kuznetsov.stories.services.data.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kuznetsov.stories.dao.RoleDao;
import ru.kuznetsov.stories.models.Role;
import ru.kuznetsov.stories.services.data.interfaces.RoleService;

@Service
public class RoleServiceImp implements RoleService {

    private final RoleDao roleDao;

    @Autowired
    public RoleServiceImp(RoleDao roleDao){
        this.roleDao = roleDao;
    }
    @Override
    public Role getRoleById(Long id) {
        return roleDao.getOne(id);
    }

    @Override
    public Role getRoleByName(String name) {
        return roleDao.findByRoleName(name);
    }
}
