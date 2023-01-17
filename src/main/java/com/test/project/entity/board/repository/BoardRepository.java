package com.test.project.entity.board.repository;

import com.test.project.entity.board.entity.Board;
import com.test.project.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
    Page<Board> findBoardsILiked(Pageable pageable, User user);

//    @Query(value = "SELECT DISTINCT b FROM Board b " +
//    "j")
//    Page<Board> findBoardsIReplied(Pageable pageable, User user);

    @Query(value = "SELECT DISTINCT b FROM Board b " +
        "JOIN FETCH b.user " +
        "JOIN b.replies r " +
        "WHERE r.user = :user " +
        "ORDER BY b.id DESC ",
        countQuery = "SELECT COUNT(b) FROM Board b " +
            "JOIN b.replies r " +
            "WHERE r.user = :user")
    Page<Board> findBoardsIReplied(Pageable pageable, User user);
}
