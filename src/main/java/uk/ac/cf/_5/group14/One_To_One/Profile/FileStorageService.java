package uk.ac.cf._5.group14.One_To_One.Profile;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String storeProfileImage(Long userId, MultipartFile file) throws IOException;

    boolean deleteProfileImage(String imageUrl) throws IOException;
}
