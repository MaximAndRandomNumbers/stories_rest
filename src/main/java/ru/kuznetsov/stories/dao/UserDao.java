package ru.kuznetsov.stories.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.kuznetsov.stories.models.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDao extends JpaRepository<User,Long> {
    Optional<User> findByLogin(String login);
    Optional<User> findByEmail(String email);


    @Query(
            value = "select * from user where LOWER(login) like ?1",
            countQuery = "select count(*) from user where LOWER(login) like ?1",
            nativeQuery = true
    )
    Page<User> findByLoginPart(String findByLoginPart, Pageable pageable);

    @Query(
            value = "select login from user where LOWER(login) like ?1 order by length(login) limit 10",
            nativeQuery = true
    )
    List<String> findByLoginPartAutoComplete(String findByLoginPart);

    @Query(
            value = "select distinct u.id, email, login, password, reg_date, enabled " +
                    "from user u join user_role ur on u.id = ur.user_id " +
                    "join role r on ur.role_id = r.id where r.role_name like 'ROLE_MODERATOR'",
            countQuery = "select distinct count(*)" +
                    "from user u join user_role ur on u.id = ur.user_id " +
                    "join role r on ur.role_id = r.id where r.role_name like 'ROLE_MODERATOR'",
            nativeQuery = true
    )
    Page<User> getModerators(Pageable pageable);

}
