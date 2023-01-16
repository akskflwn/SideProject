package com.test.project.entity.board.service;

import static com.test.project.constants.SortStatus.DEFAULT;
import static com.test.project.constants.SortStatus.LIKES;

import com.test.project.constants.SortStatus;
import com.test.project.entity.board.dto.ReplyDto.Request;
import com.test.project.entity.board.dto.ReplyDto.SuperRequest;
import com.test.project.entity.board.entity.Board;
import com.test.project.entity.board.entity.Reply;
import com.test.project.entity.board.dto.BoardDto;
import com.test.project.entity.board.dto.BoardDto.SaveRequest;
import com.test.project.entity.board.dto.BoardDto.UpdateRequest;
import com.test.project.entity.board.dto.ReplyDto.Response;
import com.test.project.entity.board.repository.BoardRepository;
import com.test.project.entity.board.repository.LikeRepository;
import com.test.project.entity.board.repository.ReplyRepository;
import com.test.project.entity.user.User;
import com.test.project.entity.user.UserRepository;
import com.test.project.exception.board.BoardNotFoundException;
import com.test.project.exception.board.ReplyNotFoundException;
import com.test.project.exception.user.UserNotFoundException;
import com.test.project.exception.user.UserNotMatchException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BoardService {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final LikeRepository likeRepository;
    private final ReplyRepository replyRepository;


    public BoardDto.Response getBoardDetail(Long boardId, Long userId) {
        Board board = getBoard(boardId);

        if (!board.getUser().getId().equals(userId)) {
            board.addViewCount(board.getView());
            boardRepository.save(board);
        }
        return BoardDto.Response.builder()
            .board(board)
            .replies(replyConvertToDto(getReplies(board)))
            .isLiked(isLikedByCurrentUser(userId, board))
            .build();

    }

    private boolean isLikedByCurrentUser(Long userId, Board board) {
        boolean isLiked = false;
        if (userId != null && isLiked(getUser(userId), board)) {
            isLiked = true;
        }
        return isLiked;
    }

    private boolean isLiked(User user, Board board) {
        return likeRepository.findByUserAndBoard(user, board).isPresent();
    }


    @Transactional
    public Long saveBoard(SaveRequest savedRequestDto, Long userId) {
        Board board = savedRequestDto.toEntity(getUser(userId));
        boardRepository.save(board);
        return board.getId();
    }

    @Transactional
    public Long updateBoard(Long boardId, UpdateRequest requestDto, Long userId) {
        Board board = getBoard(boardId);
        User user = getUser(userId);
        if (user != board.getUser()) {
            throw new UserNotMatchException("본인이 작성한글만 수정할 수 있습니다.");
        }
        board.updateBoard(requestDto.getTitle(), requestDto.getContent());
        boardRepository.save(board);
        return board.getId();
    }


    public Board getBoard(Long boardId) {
        return boardRepository.findById(boardId)
            .orElseThrow(() -> new BoardNotFoundException("존재하지 않는 게시글 입니다."));
    }


    public User getUser(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자입니다."));
    }

    private List<Reply> getReplies(Board board) {
        // 부모 댓글이 없는 최상위 댓글을 필터링하는 메서드
        return board.getReplies().stream()
            .filter(reply -> reply.getParentReply() == null)
            .collect(Collectors.toList());
    }

    private List<Response> replyConvertToDto(List<Reply> replies) {
        List<Response> responseList = new ArrayList<>();

        for (Reply reply : replies) {
            String targetNickname = "";
            if (reply.getParentUser() != null) {
                targetNickname = reply.getParentUser().getNickname();
            }

            List<Response> children = new ArrayList<>();
            if (!reply.getChildren().isEmpty()) {
                children = replyConvertToDto(reply.getChildren());
            }
            Response response = Response.builder()
                .reply(reply).targetNickname(targetNickname)
                .children(children)
                .build();
            responseList.add(response);
        }
        return responseList;
    }

    @Transactional
    public void deleteBoard(Long boardId, Long userId) {
        Board board = getBoard(boardId);
        User user = getUser(userId);
        if (user != board.getUser()) {
            throw new UserNotMatchException("본인이 작성한 글만 삭제할 수 있습니다");
        }
        board.changeStatus();

        boardRepository.save(board);
    }

    public Page<BoardDto.Response> getBoardList(Long userId, Pageable pageable, SortStatus status) {

        Page<Board> boards = new PageImpl<>(Collections.emptyList());

        if (status.equals(DEFAULT)) {
            boards = boardRepository.findAllByDesc(pageable);
        } else if (status.equals(LIKES)) {
            boards = boardRepository.findAllLikesDesc(pageable);
        }

        return new PageImpl<>(entityToListDto(boards, userId), pageable, boards.getTotalElements());
    }

    private List<BoardDto.Response> entityToListDto(Page<Board> boards, Long userId) {
        return boards.stream()
            .map(board -> BoardDto.Response.builder()
                .board(board)
                .isLiked(isLikedByCurrentUser(userId, board)).build())
            .collect(Collectors.toList());
    }

    public Long saveSuperReply(Long userId, SuperRequest superRequestDto) {
        Reply reply = Reply.createReply(getBoard(superRequestDto.getBoardId()), getUser(userId),
            superRequestDto.getContent());

        replyRepository.save(reply);
        return reply.getId();
    }

    public Long saveSubReply(Long userId, Request subRequestDto) {
        Reply parentReply = replyRepository.findById(subRequestDto.getReplyId())
            .orElseThrow(() -> new IllegalStateException("댓글이 존재하지 않습니다"));
        Reply reply = Reply.createReply(parentReply.getBoard(), getUser(userId),
            subRequestDto.getContent());
        reply.setParentUser(parentReply.getUser());

        if (parentReply.getParentReply() == null) {
            reply.setParentReply(parentReply);
        } else {
            reply.setParentReply(parentReply.getParentReply());
        }

        replyRepository.save(reply);
        return reply.getId();
    }

    public Long updateReply(Request dto, Long userId) {
        Reply reply = getReply(dto.getReplyId());

        User user = getUser(userId);
        if (user != reply.getUser()) {
            throw new UserNotMatchException("본인이 작성한 댓글만 수정할 수 있습니다.");
        }

        reply.updateReply(dto.getContent());
        replyRepository.save(reply);
        return reply.getId();
    }


    public void deleteReply(Long replyId, Long userId) {
        Reply reply = getReply(replyId);

        User user = getUser(userId);
        if (user != reply.getUser()) {
            throw new UserNotMatchException("본인이 작성한 댓글만 삭제할 수 있습니다");
        }

        reply.changeDeleteStatus();
        replyRepository.save(reply);
    }

    private Reply getReply(Long replyId) {
        return replyRepository.findById(replyId)
            .orElseThrow(() -> new ReplyNotFoundException("댓글이 존재하지 않습니다."));
    }
}
