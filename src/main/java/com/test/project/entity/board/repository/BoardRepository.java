package com.test.project.entity.board.repository;

import com.test.project.entity.board.entity.Board;
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
}
