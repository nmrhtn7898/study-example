package com.nuguri.example.util;

import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class FileUtil {

    private final Tika tika;

    private final ResourceLoader resourceLoader;

    @Value("${file.path}")
    private String fileDir;

    public String upload(MultipartFile multipartFile) throws IOException {
        String mediaType = detectType(multipartFile.getOriginalFilename());
        String filePath = fileDir + "/" + System.currentTimeMillis() + mediaType;
        multipartFile.transferTo(new File(filePath));
        return filePath;
    }

    public Resource download(String filepath) throws IOException {
        return resourceLoader.getResource("file:" + filepath);
    }

    public String detectType(String filename) {
        return tika.detect(filename);
    }

}
