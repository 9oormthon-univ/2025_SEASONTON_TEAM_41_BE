package com.debt.debt.domain.community.service;

import com.debt.debt.domain.community.dto.article.ArticleRequestDto;
import com.debt.debt.domain.community.dto.article.ArticleResponseDto;
import com.debt.debt.domain.community.dto.like.LikeRequestDto;
import com.debt.debt.domain.community.dto.like.LikeResponseDto;
import com.debt.debt.domain.community.entity.Article;
import com.debt.debt.domain.community.entity.Like;
import com.debt.debt.domain.community.repository.ArticleRepository;
import com.debt.debt.domain.community.repository.CommentRepository;
import com.debt.debt.domain.community.repository.LikeRepository;
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
public class LikeService {
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private LikeRepository likeRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public LikeResponseDto create(LikeRequestDto requestDto,Long articleId, Long userId) {
        User user=userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Article article=articleRepository.findById(articleId)
                .orElseThrow(() -> new CustomException(ErrorCode.ARTICLE_NOT_FOUND));

        boolean alreadyLiked = likeRepository.existsByUserIdAndArticleId(userId, articleId);
        if (!alreadyLiked) {
            Like like = Like.builder()
                    .id(null)
                    .user(user)
                    .article(article)
                    .build();
            likeRepository.save(like);
        }

        int likeCount = likeRepository.countByArticleId(articleId);
        article.setLikes(likeCount);
        if (!alreadyLiked) return new LikeResponseDto(likeCount, "좋아요가 반영되었습니다.");
        else return new LikeResponseDto(likeCount, "이미 좋아요를 눌렀습니다.");
    }

    @Transactional
    public LikeResponseDto delete(Long articleId, Long userId){
        Article article=articleRepository.findById(articleId)
                .orElseThrow(() -> new CustomException(ErrorCode.ARTICLE_NOT_FOUND));

        Like target = likeRepository.findByArticleIdAndUserId(articleId,userId)
                .orElseThrow(()->new CustomException(ErrorCode.LIKE_NOT_FOUND));

        likeRepository.delete(target);
        int likeCount = likeRepository.countByArticleId(articleId);
        article.setLikes(likeCount);
        return new LikeResponseDto(likeCount, "좋아요가 반영되었습니다.");
    }
}
