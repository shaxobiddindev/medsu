package uz.medsu.sevice.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.medsu.entity.Article;
import uz.medsu.entity.Drug;
import uz.medsu.entity.User;
import uz.medsu.enums.Roles;
import uz.medsu.repository.ArticleRepository;
import uz.medsu.repository.DrugRepository;
import uz.medsu.repository.UserRepository;
import uz.medsu.utils.I18nUtil;
import uz.medsu.utils.ResponseMessage;
import uz.medsu.utils.Util;


@Service
@RequiredArgsConstructor
public class UploadService {
    private final UserRepository userRepository;
    private final DrugRepository drugRepository;
    private final ArticleRepository articleRepository;
    private final ImageUpload imageUpload;


    public ResponseMessage uploadDrug(MultipartFile file, Long id) {
        Drug drug = drugRepository.findById(id).orElseThrow(()->new RuntimeException(I18nUtil.getMessage("drugNotFound")));
        if (!Util.getCurrentUser().getProfession().equals(Roles.ADMIN)) throw new RuntimeException("You cannot change it!");
        String url = imageUpload.uploadImage(file);
        drug.setImageUrl(url);
        drugRepository.save(drug);
        return ResponseMessage.builder().success(true).message("Image uploaded successfully!").data(url).build();
    }

    public ResponseMessage uploadUser(MultipartFile file, Long id) {
        if (!Util.getCurrentUser().getId().equals(id)) throw new RuntimeException("You cannot change it!");
        User user = Util.getCurrentUser();
        String url = imageUpload.uploadImage(file);
        user.setImageUrl(url);
        userRepository.save(user);
        return ResponseMessage.builder().success(true).message("Image uploaded successfully!").data(url).build();
    }


    public ResponseMessage uploadArticle(MultipartFile file, Long id) {
        Article article = articleRepository.findById(id).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("articleNotFound")));
        if (!article.getAuthor().getId().equals(Util.getCurrentUser().getId())) throw new RuntimeException("You cannot change it!");
        String url = imageUpload.uploadImage(file);
        article.setImageUrl(url);
        articleRepository.save(article);
        return ResponseMessage.builder().success(true).message("Image uploaded successfully!").data(url).build();
    }

    public ResponseMessage deleteDrug(Long id) {
        Drug drug = drugRepository.findById(id).orElseThrow(()->new RuntimeException(I18nUtil.getMessage("drugNotFound")));
        if (!Util.getCurrentUser().getProfession().equals(Roles.ADMIN)) throw new RuntimeException("You cannot change it!");
        drug.setImageUrl(null);
        drugRepository.save(drug);
        return ResponseMessage.builder().success(true).message("Image deleted successfully!").build();
    }

    public ResponseMessage deleteUser(Long id) {
        if (!Util.getCurrentUser().getId().equals(id)) throw new RuntimeException("You cannot change it!");
        User user = Util.getCurrentUser();
        user.setImageUrl(null);
        userRepository.save(user);
        return ResponseMessage.builder().success(true).message("Image deleted successfully!").build();
    }

    public ResponseMessage deleteArticle(Long id) {
        Article article = articleRepository.findById(id).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("articleNotFound")));
        if (!article.getAuthor().getId().equals(Util.getCurrentUser().getId())) throw new RuntimeException("You cannot change it!");
        article.setImageUrl(null);
        articleRepository.save(article);
        return ResponseMessage.builder().success(true).message("Image deleted successfully!").build();
    }


//    @SneakyThrows
//    private Attachment uploading(MultipartFile multipartFile) {
//        String originalFilename = multipartFile.getOriginalFilename();
//        String id = UUID.randomUUID().toString();
//        String fileName = mainPath + id + originalFilename.substring(originalFilename.lastIndexOf("."));
//
//        // Ensure the directory exists
//        Path path = Path.of(mainPath);
//        Files.createDirectories(path);
//
//        // Define the full path for the file
//        Path filePath = Path.of(fileName);
//        Files.copy(multipartFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

//        Attachment attachment = Attachment
//                .builder()
//                .id(id)
//                .fullyName(fileName)
//                .originalName(originalFilename)
//                .size(multipartFile.getSize())
//                .build();
//
//        attachmentRepository.save(attachment);
//        return attachment;
//    }


//    @SneakyThrows
//    private void deleteFile(String customPath) {
//        Path path = Paths.get(customPath);
//        Files.delete(path);
//        Attachment attachment = attachmentRepository.findByFullyName(customPath).orElseThrow(RuntimeException::new);
//        attachmentRepository.delete(attachment);
//    }
}
