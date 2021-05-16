package ru.kuznetsov.stories.dao;

import org.hibernate.annotations.SQLInsert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.kuznetsov.stories.models.ModeratedStory;
import ru.kuznetsov.stories.models.Story;
import ru.kuznetsov.stories.models.User;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ModeratedStoryDao extends JpaRepository<ModeratedStory, Long> {
    @Query(
            value = "SELECT * from moderated_story ms where DATEDIFF(?1,ms.started_date) >= 1",
            nativeQuery = true
    )
    List<ModeratedStory> getExpiredStories(Date currentDate);


    @Query(
            value = "SELECT * from moderated_story where moderator_id = ?1",
            nativeQuery = true
    )
    Optional<ModeratedStory> findByModeratorId(Long moderatorId);

    List<ModeratedStory> findAll();

    @Transactional
    @Modifying
    @Query(value = "insert into moderated_story(story_id,moderator_id,started_date) values(:story_id,:moderator_id,:started_date)",
    nativeQuery = true)
    void modifyingQueryInsertPerson(@Param("story_id")Long story_id,
                                    @Param("moderator_id")Long moderator_id,
                                    @Param("started_date")Date started_date);

    @Query(
            value = "SELECT * from moderated_story where story_id = ?1",
            nativeQuery = true
    )
    ModeratedStory findByStoryId(Long storyId);

}
