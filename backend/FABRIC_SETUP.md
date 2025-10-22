# Hyperledger Fabric Setup for Research Paper Authentication System

This guide will help you set up Hyperledger Fabric for the blockchain research paper authentication system.

## Prerequisites

1. Docker and Docker Compose installed
2. Node.js (v14 or higher)
3. Go (v1.19 or higher)
4. Git

## Step 1: Install Hyperledger Fabric Samples and Binaries

```bash
curl -sSL https://bit.ly/2ysbOFE | bash -s -- 2.5.4 1.5.6
```

This will download the fabric-samples repository and install the Hyperledger Fabric binaries.

## Step 2: Navigate to Test Network

```bash
cd fabric-samples/test-network
```

## Step 3: Start the Test Network

```bash
./network.sh up createChannel -c mychannel -ca
```

This command will:
- Start the test network with CA (Certificate Authority)
- Create a channel named "mychannel"

## Step 4: Deploy the Chaincode

1. Copy the chaincode from your project:
```bash
cp /path/to/your/project/src/main/resources/fabric/chaincode/paperchain.go fabric-samples/chaincode/paperchain/
```

2. Deploy the chaincode:
```bash
./network.sh deployCC -ccn paperchain -ccp ../chaincode/paperchain/ -ccl go
```

## Step 5: Set Environment Variables

```bash
export PATH=${PWD}/../bin:$PATH
export FABRIC_CFG_PATH=$PWD/../config/

# Environment variables for Org1
export CORE_PEER_TLS_ENABLED=true
export CORE_PEER_LOCALMSPID="Org1MSP"
export CORE_PEER_TLS_ROOTCERT_FILE=${PWD}/organizations/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt
export CORE_PEER_MSPCONFIGPATH=${PWD}/organizations/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp
export CORE_PEER_ADDRESS=localhost:7051
```

## Step 6: Test the Chaincode

Initialize the ledger:
```bash
peer chaincode invoke -o localhost:7050 --ordererTLSHostnameOverride orderer.example.com --tls --cafile "${PWD}/organizations/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem" -C mychannel -n paperchain --peerAddresses localhost:7051 --tlsRootCertFiles "${PWD}/organizations/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt" -c '{"function":"InitLedger","Args":[]}'
```

Create a sample paper record:
```bash
peer chaincode invoke -o localhost:7050 --ordererTLSHostnameOverride orderer.example.com --tls --cafile "${PWD}/organizations/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem" -C mychannel -n paperchain --peerAddresses localhost:7051 --tlsRootCertFiles "${PWD}/organizations/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt" -c '{"function":"CreatePaperRecord","Args":["student001","hash123","2025-10-08T10:00:00","John Doe","author001","2025-10-08"]}'
```

Query the paper record:
```bash
peer chaincode query -C mychannel -n paperchain -c '{"function":"GetPaperRecord","Args":["hash123"]}'
```

## Step 7: Create Application User

1. Register and enroll an application user:
```bash
cd fabric-samples/test-network
export PATH=${PWD}/../bin:${PATH}
export FABRIC_CFG_PATH=${PWD}/../config

# Register user
fabric-ca-client register --caname ca-org1 --id.name appUser --id.secret appUserpw --id.type client --tls.certfiles "${PWD}/organizations/fabric-ca/org1/tls-cert.pem"

# Enroll user
fabric-ca-client enroll -u https://appUser:appUserpw@localhost:7054 --caname ca-org1 -M "${PWD}/organizations/peerOrganizations/org1.example.com/users/appUser@org1.example.com/msp" --tls.certfiles "${PWD}/organizations/fabric-ca/org1/tls-cert.pem"
```

2. Copy the identity to wallet directory in your Spring Boot project:
```bash
mkdir -p /path/to/your/project/wallet/appUser
cp -r "${PWD}/organizations/peerOrganizations/org1.example.com/users/appUser@org1.example.com/msp" /path/to/your/project/wallet/appUser/
```

## Step 8: Update Network Configuration

Update the `network-config.yaml` file in your project with the actual certificates and configuration from your Fabric network.

## Step 9: Start Your Spring Boot Application

Your Spring Boot application should now be able to connect to the Hyperledger Fabric network and perform operations.

## API Endpoints

Once everything is set up, you can use these endpoints:

- `POST /api/fabric/papers/create` - Create a new paper record (Admin only)
- `GET /api/fabric/papers/{paperHash}` - Get a paper record
- `GET /api/fabric/papers/verify/{paperHash}` - Verify a paper record
- `GET /api/fabric/papers/all` - Get all paper records (Admin only)

## Troubleshooting

1. **Connection Issues**: Make sure all Fabric components are running
2. **Certificate Issues**: Verify that certificates are correctly copied to the wallet
3. **Chaincode Issues**: Check chaincode deployment and instantiation

## Clean Up

To stop the network and clean up:
```bash
./network.sh down
```

## Additional Notes

- The chaincode is written in Go and handles paper record creation, retrieval, and verification
- The Spring Boot application uses the Fabric Gateway SDK to interact with the blockchain
- All paper records are stored on the blockchain with immutable timestamps
- The system supports multiple operations through RESTful APIs