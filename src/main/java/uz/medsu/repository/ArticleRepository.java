package uz.medsu.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.medsu.entity.Article;
import uz.medsu.entity.User;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    @Query("SELECT a FROM Article a ORDER BY a.views DESC")
    List<Article> findTopArticlesByViews(PageRequest pageable);

    List<Article> findAllByAuthor(User author, PageRequest pageable);

}
