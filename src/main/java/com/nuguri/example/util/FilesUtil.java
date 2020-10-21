package com.nuguri.example.util;

import com.nuguri.example.entity.Files;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Component
@RequiredArgsConstructor
public class FilesUtil {

    private final Tika tika;

    @Value("${file.path}")
    private String fileDir;

    public String upload(MultipartFile multipartFile) throws IOException {
        String mediaType = detectExtension(multipartFile.getOriginalFilename());
        String filePath = fileDir + "/" + System.currentTimeMillis() + mediaType;
        multipartFile.transferTo(new File(filePath));
        return filePath;
    }

    public String detectMimeType(String filename) {
        return tika.detect(filename);
    }

    public String detectExtension(String filename) {
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

}
