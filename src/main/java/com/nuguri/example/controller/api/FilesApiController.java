package com.nuguri.example.controller.api;

import com.nuguri.example.entity.Files;
import com.nuguri.example.repository.FilesRepository;
import com.nuguri.example.util.FilesUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class FilesApiController {

    private final FilesRepository filesRepository;

    private final ResourceLoader resourceLoader;

    private final FilesUtil filesUtil;

    @GetMapping("/file/download/{id}")
    public ResponseEntity download(@PathVariable Long id) {
        Optional<Files> byId = filesRepository.findById(id);
        if (!byId.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        String filePath = byId.get().getFilePath();
        Resource resource = resourceLoader.getResource("file:/" + filePath);
        try {
            File file = resource.getFile();
            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, filesUtil.detectMimeType(file.getName()))
                    .header(HttpHeaders.CONTENT_LENGTH, file.length() + "")
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
