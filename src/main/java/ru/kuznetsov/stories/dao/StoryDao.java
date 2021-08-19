package ru.kuznetsov.stories.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.kuznetsov.stories.models.Story;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoryDao extends JpaRepository<Story,Long> {

    @Query(
            value = "select * from story where is_approved=1 order by story.id desc",
            countQuery = "select count(*) from story where is_approved=1",
            nativeQuery = true
    )
    Page<Story> findAllApproved(Pageable pageable);

    @Query(value="select * from story where story.title LIKE ?1 order by length(story.title) asc limit 1",
            nativeQuery = true)
    Optional<Story> getFirstByTitle(String title);

    @Query(value = "Select distinct s.id, s.marks_amount, s.full_text, " +
            " s.publish_date, s.rating, s.short_desc, s.title, s.author_id, s.is_approved, s.on_moderation," +
            "s.on_refactoring" +
            " from story s join user u on s.author_id = u.id" +
            " join story_genre sg on s.id = sg.story_id " +
            "where s.is_approved = 1 AND LOWER(u.login) LIKE ?1 AND LOWER(s.title) LIKE ?2 AND sg.genre_id IN ?3 ORDER BY s.id desc ",
            countQuery = "Select count(distinct s.id)" +
                    " from story s join user u on s.author_id = u.id" +
                    " join story_genre sg on s.id = sg.story_id " +
                    "where s.is_approved = 1 AND LOWER(u.login) LIKE ?1 AND LOWER(s.title) LIKE ?2 AND sg.genre_id IN ?3",
            nativeQuery = true)
    Page<Story> findFiltered(String authorLogin, String storyTitle, List<Long> genreFilter, Pageable pageable);

    @Query(value="select * from story where is_approved = 1 AND marks_amount >= '1' order by rating desc limit 20",
            nativeQuery = true)
    List<Story> getBestStories();

    @Query(value = "select distinct s.id, s.marks_amount, s.full_text, " +
            " s.publish_date, s.rating, s.short_desc, s.title, s.author_id" +
            " from story s join user u on s.author_id = u.id" +
            " join story_genre sg on s.id = sg.story_id " +
            "where sg.genre_id = ?1 ORDER BY s.id desc ",
            countQuery = "Select count(distinct s.id)" +
                    " from story s join user u on s.author_id = u.id" +
                    " join story_genre sg on s.id = sg.story_id " +
                    "where sg.genre_id = ?1 ",
            nativeQuery = true)
    Page<Story> getStoryIdByGenreId(Long genreId, Pageable pageable);

    @Query(value = "select * " +
            "from story " +
            "where is_approved = 0 and on_moderation = 0 and on_refactoring = 0 " +
            "order by id limit 1", nativeQuery = true)
    Optional<Story> getStoryToModerate();

    @Query(value = "select title from story " +
            "where lower(title) like ?1 and is_approved = 1 order by length(title) limit 5",
    nativeQuery = true)
    List<String> findStoriesByPartOfTitle(String partOfTitle);

    @Query(value = "select distinct u.login from story s join user u on s.author_id = u.id " +
            "where lower(u.login) like ?1 and is_approved = 1 order by length(s.title) limit 5",
            nativeQuery = true)
    List<String> findStoriesByPartOfAuthorLogin(String partOfAuthorLogin);
}
