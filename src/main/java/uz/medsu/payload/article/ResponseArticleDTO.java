package uz.medsu.payload.article;

public record ResponseArticleDTO(
        Long id,
        String title,
        String content,
        Long authorId,
        Long views,
        String imageUrl,
        String createdAt
) {
}
