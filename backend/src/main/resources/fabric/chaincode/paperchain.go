package main

import (
	"encoding/json"
	"fmt"
	"time"

	"github.com/hyperledger/fabric-contract-api-go/contractapi"
)

// PaperChaincode provides functions for managing research papers
type PaperChaincode struct {
	contractapi.Contract
}

// PaperRecord represents a research paper record on the blockchain
type PaperRecord struct {
	StudentID string `json:"studentId"`
	PaperHash string `json:"paperHash"`
	Timestamp string `json:"timestamp"`
	Author    string `json:"author"`
	AuthorID  string `json:"authorId"`
	PaperDate string `json:"paperDate"`
}

// CreatePaperRecord creates a new paper record on the blockchain
func (pc *PaperChaincode) CreatePaperRecord(ctx contractapi.TransactionContextInterface, studentId, paperHash, timestamp, author, authorId, paperDate string) error {
	// Check if paper record already exists
	existingRecord, err := ctx.GetStub().GetState(paperHash)
	if err != nil {
		return fmt.Errorf("failed to read from world state: %v", err)
	}
	if existingRecord != nil {
		return fmt.Errorf("paper record with hash %s already exists", paperHash)
	}

	// Create new paper record
	paperRecord := PaperRecord{
		StudentID: studentId,
		PaperHash: paperHash,
		Timestamp: timestamp,
		Author:    author,
		AuthorID:  authorId,
		PaperDate: paperDate,
	}

	// Convert to JSON
	paperRecordJSON, err := json.Marshal(paperRecord)
	if err != nil {
		return fmt.Errorf("failed to marshal paper record: %v", err)
	}

	// Save to blockchain
	err = ctx.GetStub().PutState(paperHash, paperRecordJSON)
	if err != nil {
		return fmt.Errorf("failed to put paper record to world state: %v", err)
	}

	// Emit event
	err = ctx.GetStub().SetEvent("PaperRecordCreated", paperRecordJSON)
	if err != nil {
		return fmt.Errorf("failed to emit event: %v", err)
	}

	return nil
}

// GetPaperRecord retrieves a paper record from the blockchain
func (pc *PaperChaincode) GetPaperRecord(ctx contractapi.TransactionContextInterface, paperHash string) (*PaperRecord, error) {
	paperRecordJSON, err := ctx.GetStub().GetState(paperHash)
	if err != nil {
		return nil, fmt.Errorf("failed to read from world state: %v", err)
	}
	if paperRecordJSON == nil {
		return nil, fmt.Errorf("paper record with hash %s does not exist", paperHash)
	}

	var paperRecord PaperRecord
	err = json.Unmarshal(paperRecordJSON, &paperRecord)
	if err != nil {
		return nil, fmt.Errorf("failed to unmarshal paper record: %v", err)
	}

	return &paperRecord, nil
}

// VerifyPaperRecord checks if a paper record exists on the blockchain
func (pc *PaperChaincode) VerifyPaperRecord(ctx contractapi.TransactionContextInterface, paperHash string) (bool, error) {
	paperRecordJSON, err := ctx.GetStub().GetState(paperHash)
	if err != nil {
		return false, fmt.Errorf("failed to read from world state: %v", err)
	}

	return paperRecordJSON != nil, nil
}

// GetAllPaperRecords retrieves all paper records from the blockchain
func (pc *PaperChaincode) GetAllPaperRecords(ctx contractapi.TransactionContextInterface) ([]*PaperRecord, error) {
	// Get all records using range query
	resultsIterator, err := ctx.GetStub().GetStateByRange("", "")
	if err != nil {
		return nil, fmt.Errorf("failed to get state by range: %v", err)
	}
	defer resultsIterator.Close()

	var paperRecords []*PaperRecord
	for resultsIterator.HasNext() {
		queryResponse, err := resultsIterator.Next()
		if err != nil {
			return nil, fmt.Errorf("failed to get next result: %v", err)
		}

		var paperRecord PaperRecord
		err = json.Unmarshal(queryResponse.Value, &paperRecord)
		if err != nil {
			// Skip invalid records
			continue
		}

		paperRecords = append(paperRecords, &paperRecord)
	}

	return paperRecords, nil
}

// UpdatePaperRecord updates an existing paper record (if needed)
func (pc *PaperChaincode) UpdatePaperRecord(ctx contractapi.TransactionContextInterface, paperHash, newTimestamp string) error {
	// Check if paper record exists
	existingRecordJSON, err := ctx.GetStub().GetState(paperHash)
	if err != nil {
		return fmt.Errorf("failed to read from world state: %v", err)
	}
	if existingRecordJSON == nil {
		return fmt.Errorf("paper record with hash %s does not exist", paperHash)
	}

	// Unmarshal existing record
	var existingRecord PaperRecord
	err = json.Unmarshal(existingRecordJSON, &existingRecord)
	if err != nil {
		return fmt.Errorf("failed to unmarshal existing paper record: %v", err)
	}

	// Update timestamp
	existingRecord.Timestamp = newTimestamp

	// Marshal updated record
	updatedRecordJSON, err := json.Marshal(existingRecord)
	if err != nil {
		return fmt.Errorf("failed to marshal updated paper record: %v", err)
	}

	// Save updated record
	err = ctx.GetStub().PutState(paperHash, updatedRecordJSON)
	if err != nil {
		return fmt.Errorf("failed to put updated paper record to world state: %v", err)
	}

	// Emit event
	err = ctx.GetStub().SetEvent("PaperRecordUpdated", updatedRecordJSON)
	if err != nil {
		return fmt.Errorf("failed to emit event: %v", err)
	}

	return nil
}

// InitLedger initializes the ledger with sample data (optional)
func (pc *PaperChaincode) InitLedger(ctx contractapi.TransactionContextInterface) error {
	// You can add sample data here if needed
	return nil
}

func main() {
	paperChaincode, err := contractapi.NewChaincode(&PaperChaincode{})
	if err != nil {
		fmt.Printf("Error creating paper chaincode: %v", err)
		return
	}

	if err := paperChaincode.Start(); err != nil {
		fmt.Printf("Error starting paper chaincode: %v", err)
	}
}