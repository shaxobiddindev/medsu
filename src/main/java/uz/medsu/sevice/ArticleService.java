package uz.medsu.sevice;

import uz.medsu.entity.Article;
import uz.medsu.payload.article.ArticleDTO;
import uz.medsu.utils.ResponseMessage;

public interface ArticleService {
    ResponseMessage addArticle(ArticleDTO article);
    ResponseMessage updateArticle(Long id, ArticleDTO article);
    ResponseMessage deleteArticle(Long id);
    ResponseMessage getArticle(Long id);
    ResponseMessage articlesTop(Integer page, Integer size);
    ResponseMessage articlesMy(Integer page, Integer size);
}
