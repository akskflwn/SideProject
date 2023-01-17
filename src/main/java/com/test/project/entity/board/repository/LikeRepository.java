package com.test.project.entity.board.repository;


import com.test.project.entity.board.entity.Board;
import com.test.project.entity.board.entity.Like;
import com.test.project.entity.user.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByUserAndBoard(User user, Board board);

    Optional<Like> findByUser(User user);

    boolean existsByUserAndBoard(User user, Board board);

    int countByBoard(Board board);
}
