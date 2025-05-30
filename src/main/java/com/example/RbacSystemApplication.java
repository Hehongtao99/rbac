package com.example;

import com.example.service.TimeSlotConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RbacSystemApplication implements CommandLineRunner {
    
    @Autowired
    private TimeSlotConfigService timeSlotConfigService;
    
    public static void main(String[] args) {
        SpringApplication.run(RbacSystemApplication.class, args);
    }
    
    @Override
    public void run(String... args) throws Exception {
        // 系统启动时初始化时间段配置
        timeSlotConfigService.initTimeSlotConfig();
    }
} 