package com.debt.debt.domain.community.service;

import com.debt.debt.domain.community.dto.article.ArticleIndexResponseDto;
import com.debt.debt.domain.community.dto.article.ArticleRequestDto;
import com.debt.debt.domain.community.dto.article.ArticleResponseDto;
import com.debt.debt.domain.community.dto.article.ArticleShowResponseDto;
import com.debt.debt.domain.community.dto.comment.CommentShowResponseDto;
import com.debt.debt.domain.community.entity.Article;
import com.debt.debt.domain.community.entity.Comment;
import com.debt.debt.domain.community.entity.DebtType;
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

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleService {
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public ArticleResponseDto create(ArticleRequestDto requestDto, Long userId) {
        User user=userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Article article = Article.builder()
                .id(null)
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .debtType(requestDto.getDebtType())
                .user(user)
                .likes(0)
                .build();

        Article created = articleRepository.save(article);
        return new ArticleResponseDto(created.getId(), "게시글이 작성되었습니다.");
    }

    public List<ArticleIndexResponseDto> index(String sort, String debtType) {
        List<Article> articles;

        if(debtType != null && !debtType.isEmpty()) {
            articles = articleRepository.findByDebtType(DebtType.fromKoreanName(debtType));
        } else {
            articles = articleRepository.findAll();
        }

        if("LIKE".equalsIgnoreCase(sort)) {
            articles.sort(Comparator.comparing(Article::getLikes).reversed());
        } else {
            articles.sort(Comparator.comparing(Article::getCreatedAt).reversed());
        }

        return articles.stream()
                .map(a -> new ArticleIndexResponseDto(
                        a.getId(),
                        a.getTitle(),
                        a.getDebtType(),
                        a.getUser().getNickname(),
                        a.getCreatedAt(),
                        a.getLikes()
                ))
                .collect(Collectors.toList());
    }

    public ArticleShowResponseDto show(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ARTICLE_NOT_FOUND));

        List<CommentShowResponseDto> commentDtos=commentRepository.findByArticleId(id)
                                        .stream()
                                        .map(comment -> new CommentShowResponseDto(
                                                comment.getContent(),
                                                comment.getUser().getNickname(),
                                                comment.getCreatedAt()))
                                        .collect(Collectors.toList());

        return new ArticleShowResponseDto(
                article.getTitle(),
                article.getContent(),
                article.getDebtType(),
                article.getUser().getNickname(),
                article.getCreatedAt(),
                article.getLikes(),
                commentDtos);
    }
}