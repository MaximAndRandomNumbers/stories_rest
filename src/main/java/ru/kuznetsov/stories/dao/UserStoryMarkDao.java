package ru.kuznetsov.stories.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.kuznetsov.stories.models.UserStoryMark;
import ru.kuznetsov.stories.models.UserStoryMarkPK;

@Repository
public interface UserStoryMarkDao extends JpaRepository<UserStoryMark,UserStoryMarkPK> {

    @Query(value = "select sum(mark) from user_story_mark where story_id = ?1",nativeQuery = true)
    Double findSumByStoryId(Long storyId);

    @Query(value = "select count(mark) from user_story_mark where story_id = ?1",nativeQuery = true)
    Long findAmountOfMarksByStoryId(Long storyId);
}
