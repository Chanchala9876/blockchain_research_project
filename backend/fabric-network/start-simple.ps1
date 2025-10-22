# Simple PowerShell script to start basic Fabric network for testing

Write-Host "==========================================" -ForegroundColor Blue
Write-Host "  Starting Basic Fabric Network (Test)   " -ForegroundColor Blue
Write-Host "==========================================" -ForegroundColor Blue
Write-Host ""

# Check if Docker is running
try {
    $dockerInfo = docker info 2>$null
    if ($LASTEXITCODE -ne 0) {
        Write-Host "❌ Docker is not running. Please start Docker Desktop." -ForegroundColor Red
        Write-Host ""
        Write-Host "Download Docker Desktop from:" -ForegroundColor Yellow
        Write-Host "https://www.docker.com/products/docker-desktop/" -ForegroundColor Cyan
        exit 1
    }
    Write-Host "✓ Docker is running" -ForegroundColor Green
}
catch {
    Write-Host "❌ Docker not found. Please install Docker Desktop." -ForegroundColor Red
    exit 1
}

# Pull required Docker images
Write-Host ""
Write-Host "Pulling Hyperledger Fabric Docker images..." -ForegroundColor Yellow
$images = @(
    "hyperledger/fabric-peer:2.5.4",
    "hyperledger/fabric-orderer:2.5.4", 
    "couchdb:3.1.1"
)

foreach ($image in $images) {
    Write-Host "  Pulling $image..." -ForegroundColor Cyan
    docker pull $image
    if ($LASTEXITCODE -eq 0) {
        Write-Host "    ✓ $image pulled successfully" -ForegroundColor Green
    } else {
        Write-Host "    ❌ Failed to pull $image" -ForegroundColor Red
    }
}

# Start the network
Write-Host ""
Write-Host "Starting Fabric network containers..." -ForegroundColor Yellow
docker-compose -f docker-compose-simple.yml up -d

if ($LASTEXITCODE -eq 0) {
    Write-Host "✓ Network started successfully" -ForegroundColor Green
    
    # Wait for containers to be ready
    Write-Host ""
    Write-Host "Waiting for containers to be ready..." -ForegroundColor Cyan
    Start-Sleep -Seconds 10
    
    # Check container status
    Write-Host ""
    Write-Host "Container Status:" -ForegroundColor Cyan
    docker-compose -f docker-compose-simple.yml ps
    
    Write-Host ""
    Write-Host "==========================================" -ForegroundColor Green
    Write-Host "   Basic Fabric Network Started!         " -ForegroundColor Green
    Write-Host "==========================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "Network Endpoints:" -ForegroundColor Cyan
    Write-Host "  - Peer: localhost:7051" -ForegroundColor White
    Write-Host "  - Orderer: localhost:7050" -ForegroundColor White
    Write-Host "  - CouchDB: localhost:5984" -ForegroundColor White
    Write-Host ""
    Write-Host "Health Check URLs:" -ForegroundColor Cyan
    Write-Host "  - Peer Health: http://localhost:17051/healthz" -ForegroundColor White
    Write-Host "  - Orderer Health: http://localhost:17050/healthz" -ForegroundColor White
    Write-Host "  - CouchDB: http://localhost:5984" -ForegroundColor White
    Write-Host ""
    Write-Host "To stop the network:" -ForegroundColor Yellow
    Write-Host "  docker-compose -f docker-compose-simple.yml down" -ForegroundColor White
    Write-Host ""
    Write-Host "🎉 Your Spring Boot app can now connect to real Fabric!" -ForegroundColor Green
    Write-Host "   The FabricGatewayService will detect the network and" -ForegroundColor Cyan
    Write-Host "   switch from simulation to real blockchain storage." -ForegroundColor Cyan
    
} else {
    Write-Host "❌ Failed to start network" -ForegroundColor Red
    Write-Host "Check Docker logs: docker-compose -f docker-compose-simple.yml logs" -ForegroundColor Yellow
    exit 1
}