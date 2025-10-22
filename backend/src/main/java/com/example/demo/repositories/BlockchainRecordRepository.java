package com.example.demo.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.demo.models.BlockchainRecord;

public interface BlockchainRecordRepository extends MongoRepository<BlockchainRecord, String> {
    Optional<BlockchainRecord> findByPaperHash(String paperHash);
    Page<BlockchainRecord> findByAuthorId(String authorId, Pageable pageable);
    Optional<BlockchainRecord> findTopByOrderByTimestampDesc();
}