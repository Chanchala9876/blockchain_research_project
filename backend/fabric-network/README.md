# Hyperledger Fabric Network Setup for Thesis Management

This directory contains the Hyperledger Fabric network configuration for the blockchain-based thesis management system.

## 🚀 Quick Start

### Prerequisites
1. **Docker Desktop** - Download from https://www.docker.com/products/docker-desktop/
2. **Windows PowerShell** (for running setup scripts)

### Option 1: Simple Test Network (Recommended for Testing)

```powershell
cd fabric-network
.\start-simple.ps1
```

This will:
- Pull required Hyperledger Fabric Docker images
- Start a basic Fabric network with:
  - 1 Orderer (localhost:7050)
  - 1 Peer (localhost:7051) 
  - 1 CouchDB (localhost:5984)
- Your Spring Boot app will automatically detect and connect to real blockchain

### Option 2: Full Production Network

```powershell
cd fabric-network
.\setup-network.ps1
```

This includes:
- Certificate Authority (CA)
- TLS security
- Full crypto material
- Complete channel setup

## 📁 File Structure

```
fabric-network/
├── docker-compose.yml           # Full production network
├── docker-compose-simple.yml    # Simple test network
├── setup-network.ps1           # Full setup script
├── start-simple.ps1            # Simple setup script
├── network-config.yaml         # Network configuration
└── README.md                   # This file
```

## 🔧 Network Configuration

### Simple Test Network
- **Orderer**: localhost:7050
- **Peer**: localhost:7051 
- **CouchDB**: localhost:5984
- **TLS**: Disabled (for testing)

### Full Production Network
- **CA**: localhost:7054
- **Orderer**: localhost:7050 (with TLS)
- **Peer**: localhost:7051 (with TLS)
- **CouchDB**: localhost:5984

## 🔌 Integration with Spring Boot

The `FabricGatewayService` automatically detects if a Fabric network is running:

1. **Network Available**: Uses real blockchain storage
2. **Network Unavailable**: Falls back to simulation mode

### Configuration in application.properties:
```properties
fabric.network.name=thesischannel
fabric.chaincode.name=paperchain
fabric.network.config.path=fabric-network/network-config.yaml
fabric.peer.url=grpcs://localhost:7051
fabric.orderer.url=grpcs://localhost:7050
```

## 📝 Smart Contract (Chaincode)

The `paperchain.go` chaincode provides these functions:

- `CreatePaperRecord()` - Store thesis on blockchain
- `GetPaperRecord()` - Retrieve thesis record
- `VerifyPaperRecord()` - Verify thesis exists
- `GetAllPaperRecords()` - List all thesis records
- `UpdatePaperRecord()` - Update thesis metadata

## 🧪 Testing the Network

### Check Container Status
```powershell
docker-compose -f docker-compose-simple.yml ps
```

### View Logs
```powershell
docker-compose -f docker-compose-simple.yml logs -f
```

### Health Checks
- Peer: http://localhost:17051/healthz
- Orderer: http://localhost:17050/healthz
- CouchDB: http://localhost:5984

### Test with Spring Boot
1. Start the Fabric network
2. Start your Spring Boot application
3. Upload a thesis through the admin dashboard
4. Check logs for "BLOCKCHAIN SUCCESS" instead of "SIMULATION"

## 🛑 Stopping the Network

### Simple Network
```powershell
docker-compose -f docker-compose-simple.yml down
```

### Full Network
```powershell
docker-compose down
```

### Clean Up (Remove all data)
```powershell
docker-compose down -v
docker system prune -f
```

## 🔍 Troubleshooting

### Docker Issues
```powershell
# Check Docker status
docker info

# Check running containers
docker ps

# View container logs
docker logs <container-name>
```

### Network Issues
```powershell
# Check if ports are available
netstat -ano | findstr :7050
netstat -ano | findstr :7051

# Restart network
docker-compose -f docker-compose-simple.yml restart
```

### Spring Boot Integration Issues
1. Check `application.properties` Fabric configuration
2. Verify network config path exists
3. Check Spring Boot logs for Fabric connection messages
4. Ensure containers are running before starting Spring Boot

## 📚 Learning Resources

- [Hyperledger Fabric Documentation](https://hyperledger-fabric.readthedocs.io/)
- [Fabric Samples](https://github.com/hyperledger/fabric-samples)
- [Chaincode Development](https://hyperledger-fabric.readthedocs.io/en/latest/chaincode.html)

## 🎯 Next Steps

1. **Start Simple**: Use `start-simple.ps1` for basic testing
2. **Test Integration**: Upload a thesis and verify blockchain storage
3. **Enhance Security**: Move to full production setup with TLS
4. **Scale Network**: Add more peers and organizations
5. **Advanced Features**: Implement private data collections

## 💡 Tips

- Always start Docker Desktop before running scripts
- Use simple network for development and testing
- Monitor container health through Docker Desktop
- Check Spring Boot logs to confirm blockchain vs simulation mode
- CouchDB provides a web interface at http://localhost:5984/_utils