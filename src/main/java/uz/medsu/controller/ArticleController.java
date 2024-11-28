package uz.medsu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import uz.medsu.entity.Article;
import uz.medsu.payload.article.ArticleDTO;
import uz.medsu.sevice.ArticleService;
import uz.medsu.utils.ResponseMessage;

@RestController
@RequiredArgsConstructor
@RequestMapping("/article")
public class ArticleController {
    private final ArticleService articleService;

    @PreAuthorize("{hasRole('DOCTOR'), hasAuthority('POST')}")
    @PostMapping
    public ResponseEntity<ResponseMessage> addArticle(@RequestBody ArticleDTO article) {
        return ResponseEntity.ok(articleService.addArticle(article));
    }

    @PreAuthorize("{hasRole('DOCTOR'), hasAuthority('EDIT')}")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseMessage> updateArticle(@PathVariable Long id, @RequestBody ArticleDTO article) {
        return ResponseEntity.ok(articleService.updateArticle(id, article));
    }

    @PreAuthorize("{hasRole('DOCTOR'), hasAuthority('DELETE')}")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseMessage> deleteArticle(@PathVariable Long id) {
        return ResponseEntity.ok(articleService.deleteArticle(id));
    }

    @PreAuthorize("hasAuthority('READ')")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseMessage> getArticle(@PathVariable Long id) {
        return ResponseEntity.ok(articleService.getArticle(id));
    }

    @PreAuthorize("hasAuthority('READ')")
    @GetMapping
    public ResponseEntity<ResponseMessage> getArticles(Integer page, Integer size) {
        return ResponseEntity.ok(articleService.articlesMy(page, size));
    }

    @PreAuthorize("{hasRole('DOCTOR'), hasAuthority('READ')}")
    @GetMapping("/top")
    public ResponseEntity<ResponseMessage> getTopArticles(Integer page, Integer size) {
        return ResponseEntity.ok(articleService.articlesTop(page, size));
    }
}
