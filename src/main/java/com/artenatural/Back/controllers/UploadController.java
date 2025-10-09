package com.artenatural.Back.controllers;

import com.artenatural.Back.entities.ArtistData;
import com.artenatural.Back.repositories.UserRepository;
import com.artenatural.Back.utils.JwtTokenUtil;
import com.artenatural.Back.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/uploads")
@CrossOrigin
public class UploadController {
    private final  String projectPath = System.getProperty("user.dir");
    private final  Path root = Paths.get(this.projectPath,"Static", "Images");
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/upload")
    public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile file, @RequestHeader("Authorization") String token) {
        try {
            String userID = jwtTokenUtil.getUserIdFromToken(token.substring(7));
            System.out.println("userID = " + userID);
            User user = userRepository.findById(Integer.parseInt(userID)).get();
            Path userPath = Paths.get(this.root.toString(), userID);
            if (!Files.exists(userPath))
                Files.createDirectory(userPath);
            Files.copy(file.getInputStream(), userPath.resolve(file.getOriginalFilename()));
            if(user.getArtistData() == null) {
                ArtistData artistData = new ArtistData();
                artistData.setImages(new ArrayList<>());
                artistData.getImages().add(String.format("/Images/%s/%s", userID, file.getOriginalFilename()));
                user.setArtistData(artistData);
            }
            else
                user.getArtistData().getImages().add(String.format("/Images/%s/%s", userID, file.getOriginalFilename()));
            userRepository.save(user);
            return ResponseEntity.ok().body("{\"resp\":\"Archivo cargado con éxito\"}");

        } catch (Exception e) {
            if (e instanceof FileAlreadyExistsException) {
                throw new RuntimeException("A file of that name already exists.");
            }
            throw new RuntimeException(e.getMessage());
        }
    }
    @GetMapping("/list")
    public ResponseEntity<?> listFiles(@RequestHeader("Authorization") String token) {
        try {
            String userID = jwtTokenUtil.getUserIdFromToken(token.substring(7));
            User user = userRepository.findById(Integer.parseInt(userID)).get();
            return ResponseEntity.ok().body(user.getArtistData().getImages());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> listAllImages() {
        try {
            var allImages = userRepository.findAll().stream()
                    .filter(u -> u.getArtistData() != null && u.getArtistData().getImages() != null)
                    .flatMap(u -> u.getArtistData().getImages().stream())
                    .collect(Collectors.toList());
            return ResponseEntity.ok(allImages);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al obtener todas las imágenes");
        }
    }

}
