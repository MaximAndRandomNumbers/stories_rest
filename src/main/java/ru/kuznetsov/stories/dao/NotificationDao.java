package ru.kuznetsov.stories.dao;

import org.aspectj.weaver.ast.Not;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.kuznetsov.stories.models.Notification;
import ru.kuznetsov.stories.models.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationDao extends JpaRepository<Notification, Long> {
    List<Notification> findAllByUserOrderByIdDesc(User user);
    Optional<Notification> getById(Long id);

    @Query(
            value="select count(*) from notification n where n.user_id = ?1 and n.is_read = 0",
            nativeQuery = true
    )
    Long getAmountOfNew(Long userId);
}
