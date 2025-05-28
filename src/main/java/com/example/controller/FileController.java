package com.example.controller;

import com.example.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class FileController {

    @Value("${file.upload.path:uploads}")
    private String uploadPath;

    @PostMapping("/upload")
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("请选择要上传的文件");
        }

        try {
            // 检查文件类型
            String contentType = file.getContentType();
            if (!isImageFile(contentType)) {
                return Result.error("只支持上传图片文件");
            }

            // 创建上传目录
            String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String uploadDir = uploadPath + "/" + datePath;
            Path uploadDirPath = Paths.get(uploadDir);
            if (!Files.exists(uploadDirPath)) {
                Files.createDirectories(uploadDirPath);
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID().toString() + extension;

            // 保存文件
            Path filePath = uploadDirPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath);

            // 返回文件访问路径
            String fileUrl = "/uploads/" + datePath + "/" + filename;
            return Result.success(fileUrl);

        } catch (IOException e) {
            return Result.error("文件上传失败：" + e.getMessage());
        }
    }

    private boolean isImageFile(String contentType) {
        return contentType != null && (
                contentType.startsWith("image/jpeg") ||
                contentType.startsWith("image/png") ||
                contentType.startsWith("image/gif") ||
                contentType.startsWith("image/webp")
        );
    }
} 