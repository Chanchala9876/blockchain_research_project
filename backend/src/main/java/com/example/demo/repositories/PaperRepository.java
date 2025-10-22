package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.demo.models.Paper;

public interface PaperRepository extends MongoRepository<Paper, String> {
    Page<Paper> findByAuthorId(String authorId, Pageable pageable);
    Page<Paper> findByStatus(String status, Pageable pageable);
    List<Paper> findByHash(String hash);
}