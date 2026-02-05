package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Profile;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorageService {
    String storeProfileImage(Long userId, MultipartFile file) throws IOException;
}
