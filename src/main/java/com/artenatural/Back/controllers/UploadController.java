package com.artenatural.Back.controllers;

import com.artenatural.Back.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/uploads")
@CrossOrigin
public class UploadController {
    private final  String projectPath = System.getProperty("user.dir");
    private final Path root = Paths.get(projectPath, "static", "uploads");
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @PostMapping("/upload")
    public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile file, @RequestHeader("Authorization") String token) {
        try {
            String userID = jwtTokenUtil.getUserIdFromToken(token);
            Path userPath = this.root.resolve(userID);
            Path endPath = userPath.resolve(file.getOriginalFilename());
            Files.copy(file.getInputStream(), this.root.resolve(file.getOriginalFilename()));
            return ResponseEntity.ok().body("{\"resp\":\"Archivo cargado con éxito\"}");
        } catch (Exception e) {
            if (e instanceof FileAlreadyExistsException) {
                throw new RuntimeException("A file of that name already exists.");
            }
            throw new RuntimeException(e.getMessage());
        }
    }
    @GetMapping("/list")
    public ResponseEntity<?> listFiles(@RequestHeader String token) {
        try {
            String userID = jwtTokenUtil.getUserIdFromToken(token);
            Path endPath = this.root.resolve(userID);
            return ResponseEntity.ok().body(Files.list(endPath).map(Path::toString).collect(Collectors.toList()));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
