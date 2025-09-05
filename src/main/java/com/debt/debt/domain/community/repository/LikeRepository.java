package com.debt.debt.domain.community.repository;

import com.debt.debt.domain.community.entity.Article;
import com.debt.debt.domain.community.entity.Comment;
import com.debt.debt.domain.community.entity.Like;
import com.debt.debt.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    @Override
    ArrayList<Like> findAll();

    int countByArticleId(Long articleId);

    Optional<Like> findById(Long id);

    Optional<Like> findByArticleIdAndUserId(Long articleId, Long userId);

    boolean existsByUserIdAndArticleId(Long userId, Long articleId);
}
