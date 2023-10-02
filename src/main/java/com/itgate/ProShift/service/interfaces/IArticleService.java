package com.itgate.ProShift.service.interfaces;

import com.itgate.ProShift.entity.Article;

import java.util.List;

public interface IArticleService {
    Article create(Article article);
    Article update(Long id, Article article);
    List<Article> getAllFree();
    List<Article> getAllPremium();
    List<Article> getAll();
    Article getById(Long id);
    void delete(Long id);
}
