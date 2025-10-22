package com.example.demo.controllers;

import com.example.demo.services.FabricGatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/blockchain-explorer")
@CrossOrigin(origins = "*")
public class BlockchainExplorerController {

    @Autowired
    private FabricGatewayService fabricGatewayService;

    @GetMapping("/records")
    public ResponseEntity<Map<String, Object>> getAllBlockchainRecords() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Query all records from blockchain
            List<Map<String, Object>> records = fabricGatewayService.queryAllRecords();
            
            response.put("success", true);
            response.put("totalRecords", records.size());
            response.put("records", records);
            response.put("message", "Successfully retrieved all blockchain records");
            response.put("networkStatus", fabricGatewayService.getNetworkStatus());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            response.put("records", new ArrayList<>());
        }
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/record/{txId}")
    public ResponseEntity<Map<String, Object>> getRecordByTxId(@PathVariable String txId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Map<String, Object> record = fabricGatewayService.queryRecordByTxId(txId);
            
            if (record != null) {
                response.put("success", true);
                response.put("record", record);
                response.put("message", "Record found on blockchain");
            } else {
                response.put("success", false);
                response.put("message", "Record not found with transaction ID: " + txId);
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/network-info")
    public ResponseEntity<Map<String, Object>> getNetworkInfo() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            response.put("success", true);
            response.put("networkStatus", fabricGatewayService.getNetworkStatus());
            response.put("isRealBlockchain", fabricGatewayService.isFabricNetworkAvailable());
            response.put("message", "Network information retrieved successfully");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
}