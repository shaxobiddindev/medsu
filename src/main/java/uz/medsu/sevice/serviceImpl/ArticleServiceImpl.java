package uz.medsu.sevice.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import uz.medsu.entity.Article;
import uz.medsu.payload.article.ArticleDTO;
import uz.medsu.payload.article.ResponseArticleDTO;
import uz.medsu.repository.ArticleRepository;
import uz.medsu.repository.UserRepository;
import uz.medsu.sevice.ArticleService;
import uz.medsu.utils.I18nUtil;
import uz.medsu.utils.ResponseMessage;
import uz.medsu.utils.Util;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {
    private final ArticleRepository articleRepository;


    @Override
    public ResponseMessage addArticle(ArticleDTO articleDTO) {
        Article article = Article
                .builder()
                .title(articleDTO.title())
                .content(articleDTO.content())
                .author(Util.getCurrentUser())
                .views(0L)
                .build();
        articleRepository.save(article);
        return ResponseMessage.builder().success(true).data(
                new ResponseArticleDTO(
                        article.getId(),
                        article.getTitle(),
                        article.getContent(),
                        article.getAuthor().getId(),
                        article.getViews(),
                        article.getImageUrl(),
                        article.getCreatedAt().toLocalDateTime().toString()
                )
        ).build();
    }

    @Override
    public ResponseMessage updateArticle(Long id, ArticleDTO articleDTO) {
        Article article = articleRepository.findById(id).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("articleNotFound")));
        if (!article.getAuthor().getId().equals(Util.getCurrentUser().getId())) throw new RuntimeException(I18nUtil.getMessage("articleNotFound"));
        article.setTitle(articleDTO.title());
        article.setTitle(articleDTO.content());
        articleRepository.save(article);
        return ResponseMessage.builder().success(true).data(
                new ResponseArticleDTO(
                        article.getId(),
                        article.getTitle(),
                        article.getContent(),
                        article.getAuthor().getId(),
                        article.getViews(),
                        article.getImageUrl(),
                        article.getCreatedAt().toLocalDateTime().toString()
                )
        ).build();
    }

    @Override
    public ResponseMessage deleteArticle(Long id) {
        Article article = articleRepository.findById(id).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("articleNotFound")));
        if (!article.getAuthor().getId().equals(Util.getCurrentUser().getId())) throw new RuntimeException(I18nUtil.getMessage("articleNotFound"));

        articleRepository.delete(article);
        return ResponseMessage.builder().success(true).message(I18nUtil.getMessage("articleDeleted")).build();
    }

    @Override
    public ResponseMessage getArticle(Long id) {
        Article article = articleRepository.findById(id).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("articleNotFound")));
        return ResponseMessage.builder().success(true).data(
                new ResponseArticleDTO(
                        article.getId(),
                        article.getTitle(),
                        article.getContent(),
                        article.getAuthor().getId(),
                        article.getViews(),
                        article.getImageUrl(),
                        article.getCreatedAt().toLocalDateTime().toString()
                )
        ).build();
    }

    @Override
    public ResponseMessage articlesTop(Integer page, Integer size) {
        List<ResponseArticleDTO> articleDTOS = articleRepository.findTopArticlesByViews(PageRequest.of(page, size)).stream().map(article -> {
            return new ResponseArticleDTO(
                    article.getId(),
                    article.getTitle(),
                    article.getContent(),
                    article.getAuthor().getId(),
                    article.getViews(),
                    article.getImageUrl(),
                    article.getCreatedAt().toLocalDateTime().toString()
            );
        }).toList();
        return ResponseMessage.builder().success(true).data(articleDTOS).build();
    }

    @Override
    public ResponseMessage articlesMy(Integer page, Integer size) {
        List<ResponseArticleDTO> articleDTOS = articleRepository.findAllByAuthor(Util.getCurrentUser(), PageRequest.of(page, size)).stream().map(article -> {
            return new ResponseArticleDTO(
                    article.getId(),
                    article.getTitle(),
                    article.getContent(),
                    article.getAuthor().getId(),
                    article.getViews(),
                    article.getImageUrl(),
                    article.getCreatedAt().toLocalDateTime().toString()
            );
        }).toList();
        return ResponseMessage.builder().success(true).data(articleDTOS).build();
    }
}
