# Simple script to start Fabric network - no complex syntax

Write-Host "Starting Hyperledger Fabric Network..." -ForegroundColor Green
Write-Host ""

# Check Docker
Write-Host "Checking Docker..." -ForegroundColor Yellow
docker --version
if ($LASTEXITCODE -ne 0) {
    Write-Host "Docker not available!" -ForegroundColor Red
    exit 1
}
Write-Host "Docker OK!" -ForegroundColor Green
Write-Host ""

# Pull images
Write-Host "Pulling Docker images..." -ForegroundColor Yellow
docker pull hyperledger/fabric-peer:2.5.4
docker pull hyperledger/fabric-orderer:2.5.4
docker pull couchdb:3.1.1
Write-Host "Images pulled!" -ForegroundColor Green
Write-Host ""

# Start network
Write-Host "Starting containers..." -ForegroundColor Yellow
docker-compose -f docker-compose-simple.yml up -d

Write-Host ""
Write-Host "Waiting for containers..." -ForegroundColor Cyan
Start-Sleep -Seconds 15

Write-Host ""
Write-Host "Container status:" -ForegroundColor Cyan
docker-compose -f docker-compose-simple.yml ps

Write-Host ""
Write-Host "Network endpoints:" -ForegroundColor Green
Write-Host "- Peer: localhost:7051" -ForegroundColor White
Write-Host "- Orderer: localhost:7050" -ForegroundColor White  
Write-Host "- CouchDB: localhost:5984" -ForegroundColor White
Write-Host ""
Write-Host "Fabric network started! Ready for Spring Boot." -ForegroundColor Green