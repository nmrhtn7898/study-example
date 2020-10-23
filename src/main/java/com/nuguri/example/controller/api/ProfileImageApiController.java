package com.nuguri.example.controller.api;

import com.nuguri.example.entity.Files;
import com.nuguri.example.entity.ProfileImage;
import com.nuguri.example.repository.FilesRepository;
import com.nuguri.example.service.FilesService;
import com.nuguri.example.util.FilesUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class ProfileImageApiController {

    private final FilesService filesService;

    private final FilesRepository filesRepository;

    private final FilesUtil filesUtil;

    @GetMapping("/api/v1/profileImage/{id}")
    public ResponseEntity getProfileImage(@PathVariable Long id) {
        Optional<Files> byId = filesRepository.findById(id);
        if (!byId.isPresent()) {
            return ResponseEntity
                    .notFound()
                    .build();
        }
        return ResponseEntity
                .ok()
                .body(byId.get());
    }

    @PreAuthorize("permitAll()")
    @PostMapping("/api/v1/profileImage")
    public ResponseEntity generateProfileImage(MultipartFile file) throws IOException, URISyntaxException {
        String filePath = filesUtil.upload(file);
        ProfileImage profileImage = ProfileImage
                .builder()
                .filePath(filePath)
                .name(file.getOriginalFilename())
                .build();
        profileImage = filesRepository.save(profileImage);
        GenerateProfileResponse response = new GenerateProfileResponse(profileImage);
        GenerateProfileResource resource = new GenerateProfileResource(response);
        return ResponseEntity
                .created(new URI("/api/v1/file/" + profileImage))
                .body(resource);
    }



    /* dto class */
    @Getter
    public static class GenerateProfileResponse {
        private final Long id;
        private final String filePath;
        private final String name;

        public GenerateProfileResponse(ProfileImage profileImage) {
            this.id = profileImage.getId();
            this.filePath = profileImage.getFilePath();
            this.name = profileImage.getName();
        }
    }

    @Getter
    public static class GenerateProfileResource extends EntityModel<GenerateProfileResponse> {
        private final GenerateProfileResponse content;

        public GenerateProfileResource(GenerateProfileResponse response) {
            this.content = response;
        }
    }

}
