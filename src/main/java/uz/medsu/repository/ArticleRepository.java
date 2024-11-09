package uz.medsu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.medsu.entity.Article;

public interface ArticleRepository extends JpaRepository<Article, Long> {
}
