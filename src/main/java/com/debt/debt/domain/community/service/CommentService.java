package com.debt.debt.domain.community.service;

import com.debt.debt.domain.community.dto.comment.CommentRequestDto;
import com.debt.debt.domain.community.dto.comment.CommentResponseDto;
import com.debt.debt.domain.community.entity.Article;
import com.debt.debt.domain.community.entity.Comment;
import com.debt.debt.domain.community.repository.ArticleRepository;
import com.debt.debt.domain.community.repository.CommentRepository;
import com.debt.debt.domain.user.entity.User;
import com.debt.debt.domain.user.repository.UserRepository;
import com.debt.debt.global.exception.CustomException;
import com.debt.debt.global.exception.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public CommentResponseDto create(CommentRequestDto requestDto, Long articleId, Long userId) {
        User user=userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Article article = articleRepository.findById(articleId)
                .orElseThrow(()->new CustomException(ErrorCode.ARTICLE_NOT_FOUND));

        Comment comment = Comment.builder()
                .id(null)
                .content(requestDto.getContent())
                .user(user)
                .article(article)
                .build();


        Comment created = commentRepository.save(comment);
        return new CommentResponseDto(created.getId(), "댓글이 작성되었습니다.");
    }
}