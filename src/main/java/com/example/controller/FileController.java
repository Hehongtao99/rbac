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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class FileController {

    @Value("${file.upload.path:uploads}")
    private String uploadPath;

    @PostMapping("/upload")
    public Result<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("请选择要上传的文件");
        }

        try {
            // 检查文件类型
            String contentType = file.getContentType();
            String originalFilename = file.getOriginalFilename();
            
            if (!isAllowedFile(contentType, originalFilename)) {
                return Result.error("不支持的文件类型，只支持图片文件(jpg,png,gif,webp)和文档文件(pdf,doc,docx)");
            }

            // 根据文件类型确定子目录
            String subDir = isImageFile(contentType) ? "images" : "documents";
            
            // 创建上传目录
            String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String uploadDir = uploadPath + "/" + subDir + "/" + datePath;
            Path uploadDirPath = Paths.get(uploadDir);
            if (!Files.exists(uploadDirPath)) {
                Files.createDirectories(uploadDirPath);
            }

            // 生成唯一文件名
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID().toString() + extension;

            // 保存文件
            Path filePath = uploadDirPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath);

            // 返回文件访问路径和原始文件名
            String fileUrl = "/uploads/" + subDir + "/" + datePath + "/" + filename;
            
            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("originalName", originalFilename);
            result.put("size", String.valueOf(file.getSize()));
            
            return Result.success(result);

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
    
    private boolean isDocumentFile(String contentType, String filename) {
        if (contentType != null) {
            return contentType.equals("application/pdf") ||
                   contentType.equals("application/msword") ||
                   contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        }
        
        // 如果contentType为空，根据文件扩展名判断
        if (filename != null) {
            String lowerFilename = filename.toLowerCase();
            return lowerFilename.endsWith(".pdf") ||
                   lowerFilename.endsWith(".doc") ||
                   lowerFilename.endsWith(".docx");
        }
        
        return false;
    }
    
    private boolean isAllowedFile(String contentType, String filename) {
        return isImageFile(contentType) || isDocumentFile(contentType, filename);
    }
} 