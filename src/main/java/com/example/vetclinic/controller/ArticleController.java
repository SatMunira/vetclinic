package com.example.vetclinic.controller;

import com.example.vetclinic.dto.ArticleRequest;
import com.example.vetclinic.entity.Article;
import com.example.vetclinic.entity.User;
import com.example.vetclinic.repository.ArticleRepository;
import com.example.vetclinic.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> createArticle(@RequestBody ArticleRequest req,
                                           Principal principal) {
        User author = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Article article = new Article();
        article.setTitle(req.title);
        article.setContent(req.content);
        article.setCreatedAt(LocalDateTime.now());
        article.setAuthor(author);

        return ResponseEntity.ok(articleRepository.save(article));
    }


    @GetMapping
    public ResponseEntity<?> getAllArticles() {
        return ResponseEntity.ok(articleRepository.findAllByOrderByCreatedAtDesc());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getArticleById(@PathVariable Long id) {
        return ResponseEntity.ok(articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteArticle(@PathVariable Long id, Principal principal) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found"));

        if (!article.getAuthor().getUsername().equals(principal.getName())) {
            return ResponseEntity.status(403).body("You can delete only your own articles");
        }

        articleRepository.delete(article);
        return ResponseEntity.ok("Deleted");
    }
}
