package com.debt.debt.domain.community.repository;

import com.debt.debt.domain.community.entity.Article;
import com.debt.debt.domain.community.entity.DebtType;
import com.debt.debt.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    @Override
    ArrayList<Article> findAll();

    List<Article> findByDebtType(DebtType debtType);

    Optional<Article> findById(Long id);
}
