package com.artenatural.Back.controllers;

import com.artenatural.Back.entities.ArtistData;
import com.artenatural.Back.repositories.UserRepository;
import com.artenatural.Back.utils.JwtTokenUtil;
import com.artenatural.Back.entities.User;
import jakarta.annotation.PostConstruct;
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
    // Reads the path '/Railway/Images' from the environment variable
    // on most operating systems (including the one your local JVM runs on).
    @Value("${app.upload-dir:/tmp/default-uploads}")
    private String uploadDir; // No longer needs to be in application.properties

    private Path root;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    public void init() {
        try {
            this.root = Paths.get(uploadDir);
            // On Linux/Mac: /tmp/default-uploads. On Windows: a temp path.
            // On Railway: /Railway/Images (overridden by ENV var).
            Files.createDirectories(root);
            System.out.println("File upload directory initialized at: " + this.root.toAbsolutePath());
        } catch (Exception e) {
            System.err.println("ERROR: Could not initialize storage location at " + uploadDir);
            throw new RuntimeException("Could not initialize storage location!", e);
        }
    }


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
    @GetMapping("/list/{id}")
    public ResponseEntity<?> listFiles(@PathVariable int id) {
        try {
            User user = userRepository.findById(id).get();
            return ResponseEntity.ok().body(user.getArtistData().getImages());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    @GetMapping("/list/all")
    public ResponseEntity<?> listAllFiles() {
        try {
            return ResponseEntity.ok().body(Files.walk(root)
                    .filter(p -> !Files.isDirectory(p))
                    .map(Path::toString)
                    .map(s-> s.substring(s.indexOf("Images")-1)
                            .replace("\\","/")));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
