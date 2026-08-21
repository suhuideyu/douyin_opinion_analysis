package com.dy.comment;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication
@MapperScan("com.dy.comment.mapper")
public class DyCommentApplication {
    public static void main(String[] args) {
        // 自动启动 Flask 分词服务
        Process flaskProcess = null;
        try {
            File baseDir = new File(System.getProperty("user.dir"));
            String pythonExe = new File(baseDir, "../.venv/Scripts/python.exe").getCanonicalPath();
            File scriptDir = new File(baseDir, "../Py_Data/clean");
            ProcessBuilder pb = new ProcessBuilder(pythonExe, "wordcloud_service.py");
            pb.directory(scriptDir);
            pb.redirectErrorStream(true);
            pb.environment().put("PYTHONIOENCODING", "utf-8");
            flaskProcess = pb.start();
            System.out.println("[Flask] 分词服务已启动");
        } catch (Exception e) {
            System.out.println("[Flask] 分词服务启动失败，词云将不可用: " + e.getMessage());
        }

        final Process fp = flaskProcess;
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (fp != null && fp.isAlive()) {
                fp.destroy();
                System.out.println("[Flask] 分词服务已关闭");
            }
        }));

        SpringApplication.run(DyCommentApplication.class, args);
        System.out.println("==============================================");
        System.out.println("        启动成功！http://localhost:8080");
        System.out.println("        Swagger: http://localhost:8080/swagger-ui.html");
        System.out.println("==============================================");
    }
}
