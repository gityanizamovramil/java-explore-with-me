package ru.practicum.ewm.comment.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.common.CommentState;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT c FROM Comment AS c " +
            "WHERE ((:users) IS NULL OR c.author.id IN (:users)) " +
            "AND ((:states) IS NULL OR c.state IN (:states)) " +
            "AND ((:events) IS NULL OR c.event.id IN (:events)) " +
            "AND (:text IS NULL OR LOWER(c.text) LIKE LOWER(CONCAT('%', :text, '%'))) " +
            "AND (CAST(:rangeStart AS date) IS NULL OR c.created >= :rangeStart) " +
            "AND (CAST(:rangeEnd AS date) IS NULL OR c.created <= :rangeEnd)"
    )
    List<Comment> fetchComments(
            @Param("users") List<Long> users,
            @Param("states") List<CommentState> states,
            @Param("events") List<Long> events,
            @Param("text") String text,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            PageRequest pageRequest);

    List<Comment> findAllByAuthorId(Long authorId, PageRequest pageRequest);

    List<Comment> findAllByEventIdAndState(Long eventId, CommentState state, PageRequest pageRequest);
}
