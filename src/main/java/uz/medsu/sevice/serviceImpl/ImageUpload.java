package uz.medsu.sevice.serviceImpl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.medsu.components.ImgurClient;

import java.util.Map;

@Service
public class ImageUpload {
    private final ImgurClient imgurClient;
    private final String clientId;


    public ImageUpload(ImgurClient imgurClient, @Value("${imgur.client-id}") String clientId) {
        this.imgurClient = imgurClient;
        this.clientId = clientId;
    }

    public String uploadImage(MultipartFile file) {
        if (file.isEmpty()) throw new RuntimeException("File is empty");
        if (file.getSize() > (1048576*5)) throw new RuntimeException("File is too big");
        String authorizationHeader = "Client-ID " + clientId;
        Map<String, Object> response = imgurClient.uploadImage(file, authorizationHeader);

        // "data" obyektidan rasm linkini olish
        Map<String, Object> data = (Map<String, Object>) response.get("data");
        String imageUrl = (String) data.get("link");
        System.out.println(imageUrl);
        return imageUrl;
    }
}
