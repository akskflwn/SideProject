package com.test.project.service;

import static com.test.project.constants.DefaultImageConstants.DEFAULT_BOARD_IMAGE_ID;
import static com.test.project.constants.SortStatus.DEFAULT;
import static com.test.project.constants.SortStatus.LIKES;

import com.test.project.constants.SortStatus;
import com.test.project.dto.LikeDto;
import com.test.project.dto.ReplyDto.Request;
import com.test.project.dto.ReplyDto.SuperRequest;
import com.test.project.entity.Board;
import com.test.project.entity.Category;
import com.test.project.entity.Image;
import com.test.project.entity.Like;
import com.test.project.entity.Reply;
import com.test.project.dto.BoardDto;
import com.test.project.dto.BoardDto.SaveRequest;
import com.test.project.dto.ReplyDto.Response;
import com.test.project.exception.board.CategoryNotFoundException;
import com.test.project.repository.BoardRepository;
import com.test.project.repository.CategoryRepository;
import com.test.project.repository.LikeRepository;
import com.test.project.repository.ReplyRepository;
import com.test.project.entity.User;
import com.test.project.repository.UserRepository;
import com.test.project.exception.LikeNotFoundException;
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
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class BoardService {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final LikeRepository likeRepository;
    private final ReplyRepository replyRepository;
    private final CategoryRepository categoryRepository;
    private final ImageService imageService;


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

    @Transactional
    public Long saveBoard(SaveRequest requestDTO, Long userId, MultipartFile multipartFile) {
        Board board = requestDTO.toEntity(getUser(userId),getCategory(requestDTO.getCategoryId()));

        Image image = getImage(multipartFile);
        board.setImage(image);

        boardRepository.save(board);

        return board.getId();
    }

    private Image getImage(MultipartFile multipartFile) {
        Image image;

        if (multipartFile == null) {
            image = imageService.getImageById(DEFAULT_BOARD_IMAGE_ID);
        } else {
            image = imageService.savePostImage(multipartFile);
        }

        return image;
    }

    @Transactional
    public Long updateBoard(Long boardId, SaveRequest requestDto, MultipartFile multipartFile,Long userId) {

        Board board = getBoard(boardId);

        User user = getUser(userId);
        if (user != board.getUser()) {
            throw new UserNotMatchException("본인이 작성한글만 수정할 수 있습니다.");
        }

        if (!multipartFile.isEmpty()) {
            Image image = imageService.savePostImage(multipartFile);
            board.setImage(image);
        }

        board.updateBoard(requestDto.getTitle(), requestDto.getContent());

        boardRepository.save(board);
        return board.getId();
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



    public LikeDto likeProcess(Long boardId, Long userId) {

        User user = getUser(userId);
        Board board = getBoard(boardId);

        boolean likeStatus = false;
        if (!isLiked(user, board)) {
            likeRepository.save(Like.builder()
                .user(user)
                .board(board).build());
            likeStatus = true;
        } else {
            Like like = likeRepository.findByUserAndBoard(user, board)
                .orElseThrow(() -> new LikeNotFoundException("좋아요 누른 게시물이 없습니다."));
            likeRepository.delete(like);
        }

        return LikeDto.builder()
            .likeStatus(likeStatus)
            .count(likeRepository.countByBoard(board)).build();
    }

    public Category getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
            .orElseThrow(() -> new CategoryNotFoundException("존재하지 않는 카테고리입니다."));
    }

    private Reply getReply(Long replyId) {
        return replyRepository.findById(replyId)
            .orElseThrow(() -> new ReplyNotFoundException("댓글이 존재하지 않습니다."));
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


}
