package com.debt.debt.domain.community.repository;

import com.debt.debt.domain.community.entity.Article;
import com.debt.debt.domain.community.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Override
    ArrayList<Comment> findAll();

    Optional<Comment> findById(Long id);

    @Query(value = "SELECT * FROM comments WHERE article_id = :id", nativeQuery = true)
    List<Comment> findByArticleId(Long id);

    int countByArticleId(Long articleId);
}