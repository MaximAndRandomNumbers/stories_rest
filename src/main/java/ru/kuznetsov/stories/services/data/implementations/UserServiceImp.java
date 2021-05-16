package ru.kuznetsov.stories.services.data.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.webjars.NotFoundException;
import ru.kuznetsov.stories.dao.UserDao;
import ru.kuznetsov.stories.dto.RegRequestDto;
import ru.kuznetsov.stories.models.User;
import ru.kuznetsov.stories.services.data.interfaces.RoleService;
import ru.kuznetsov.stories.services.data.interfaces.UserService;

import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class UserServiceImp implements UserService {

    private final UserDao userDao;
    private final RoleService roleService;
    private final PasswordEncoder bCrypt;

    @Autowired
    public UserServiceImp(UserDao userDao, RoleService roleService, PasswordEncoder bCrypt){
        this.userDao = userDao;
        this.roleService = roleService;
        this.bCrypt = bCrypt;
    }
    @Override
    public User save(RegRequestDto regRequest) {
        User user = new User();
        user.setLogin(regRequest.getLogin());
        user.setPassword(bCrypt.encode(regRequest.getPassword()));
        user.setEmail(regRequest.getEmail());
        user.setEnabled(false);
        user.setRegistrationDate(new Date());
        user.getRoles().add(roleService.getRoleByName("ROLE_USER"));
        userDao.save(user);
        return user;
    }

    @Override
    public User findByLogin(String login) {
        return userDao.findByLogin(login).orElse(null);
    }

    @Override
    public User findByEmail(String email) {
        return userDao.findByEmail(email).orElse(null);
    }

    @Override
    public void enableUser(User user) {
        user.setEnabled(true);
    }

    @Override
    public void updatePassword(User user, String newPassword) {
        user.setPassword(bCrypt.encode(newPassword));
        userDao.save(user);
    }

    @Override
    public Page<User> getUsers(Pageable pageable) {
        return userDao.findAll(pageable);
    }

    @Override
    public Page<User> getUsersByLogin(String loginPart, Pageable pageable) {
        return userDao.findByLoginPart("%"+loginPart.toLowerCase()+"%", pageable);
    }

    @Override
    public List<String> getUsersByLoginAutoComplete(String loginPart) {
        return userDao.findByLoginPartAutoComplete("%"+loginPart.toLowerCase()+"%");
    }

    @Override
    public Page<User> getModerators(Pageable pageable) {
        return userDao.getModerators(pageable);
    }

    @Override
    public User getById(Long id) {
        return userDao.getOne(id);
    }

    @Override
    public User setModeratorRole(Long id) {
        User user = getById(id);
        if(user == null) {
            throw new NotFoundException("Данного пользователя нет в базе");
        }
        user.getRoles().add(roleService.getRoleByName("ROLE_MODERATOR"));
        return userDao.save(user);
    }

    @Override
    public User removeModeratorRole(Long id) {
        User user = getById(id);
        if(user == null) {
            throw new NotFoundException("Данного пользователя нет в базе");
        }
        user.getRoles().remove(roleService.getRoleByName("ROLE_MODERATOR"));
        return userDao.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        User user = getById(id);
        if(user == null) {
            throw new NotFoundException("Данного пользователя нет в базе");
        }
        userDao.delete(user);
    }


}
