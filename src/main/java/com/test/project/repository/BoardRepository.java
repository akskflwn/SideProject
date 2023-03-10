package com.test.project.repository;

import com.test.project.entity.Board;
import com.test.project.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query("SELECT b FROM Board b ORDER BY b.id DESC ")
    Page<Board> findAllByDesc(Pageable pageable);

    @Query("SELECT b FROM Board b ORDER BY size(b.likes) DESC, b.id DESC")
    Page<Board> findAllLikesDesc(Pageable pageable);

    @Query(value = "SELECT DISTINCT b FROM Board b " +
        "JOIN FETCH b.user " +
        "JOIN b.likes l " +
        "WHERE l.user = :user " +
        "ORDER BY b.id DESC ",
        countQuery = "SELECT COUNT(b) FROM Board b " +
            "JOIN b.likes l " +
            "WHERE l.user = :user")
    Page<Board> findBoardsILiked(Pageable pageable,  @Param("user")User user);

    @Query(value = "SELECT DISTINCT b FROM Board b " +
        "JOIN FETCH b.user " +
        "JOIN b.replies r " +
        "WHERE r.user = :user " +
        "ORDER BY b.id DESC ",
        countQuery = "SELECT COUNT(b) FROM Board b " +
            "JOIN b.replies r " +
            "WHERE r.user = :user")
    Page<Board> findBoardsIReplied(Pageable pageable, @Param("user") User user);

    @Query(value = "SELECT DISTINCT b FROM Board b " +
        "JOIN FETCH b.user " +
        "WHERE b.user = :user " +
        "ORDER BY b.id DESC",
        countQuery = "SELECT COUNT(b) FROM Board b " +
            "WHERE b.user = :user")
    Page<Board> findByUser(Pageable pageable, @Param("user") User user);
}
