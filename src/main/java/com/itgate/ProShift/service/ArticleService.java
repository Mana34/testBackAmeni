package com.itgate.ProShift.service;

import com.itgate.ProShift.entity.Article;
import com.itgate.ProShift.entity.User;
import com.itgate.ProShift.repository.ArticleRepository;
import com.itgate.ProShift.repository.UserRepository;
import com.itgate.ProShift.service.interfaces.IArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleService implements IArticleService {
    @Autowired
    private  ArticleRepository articleRepository;


    @Override
    public Article create(Article article) {
        return articleRepository.save(article);
    }

    @Override
    public Article update(Long id, Article article) {
       Article article1= articleRepository.findById(id).get();
       if (article1!=null){
           article.setId(id);
           article.setDatePublication(article1.getDatePublication());
           return articleRepository.save(article);
       }
        return null;
    }

    @Override
    public List<Article> getAllFree() {
        return articleRepository.findAllByFreemium(true);
    }

    @Override
    public List<Article> getAllPremium() {
        return articleRepository.findAllByFreemium(false);
    }

    @Override
    public List<Article> getAll() {
        return articleRepository.findAll();
    }

    @Override
    public Article getById(Long id) {
        return articleRepository.findById(id).get();
    }

    @Override
    public void delete(Long id) {
    articleRepository.deleteById(id);
    }


}
