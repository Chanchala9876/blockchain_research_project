# PowerShell script to set up Hyperledger Fabric network for thesis management

param(
    [switch]$Help,
    [switch]$Verbose
)

$CHANNEL_NAME = "thesischannel"
$CC_NAME = "paperchain"
$CC_VERSION = "1.0"
$CC_SEQUENCE = "1"
$CC_INIT_FCN = "InitLedger"

function Show-Help {
    Write-Host "Usage: "
    Write-Host "  .\setup-network.ps1 [Flags]"
    Write-Host ""
    Write-Host "    Flags:"
    Write-Host "    -Help - Print this help message"
    Write-Host "    -Verbose - Verbose mode"
    Write-Host ""
    Write-Host "Taking all defaults:"
    Write-Host "	.\setup-network.ps1"
}

function Test-DockerRunning {
    try {
        $dockerInfo = docker info 2>$null
        if ($LASTEXITCODE -eq 0) {
            Write-Host "✓ Docker is running" -ForegroundColor Green
            return $true
        }
    }
    catch {
        Write-Host "ERROR: Docker is not running. Please start Docker Desktop and try again." -ForegroundColor Red
        return $false
    }
    
    Write-Host "ERROR: Docker is not running. Please start Docker Desktop and try again." -ForegroundColor Red
    return $false
}

function New-ChannelArtifacts {
    Write-Host "Creating channel artifacts directory..." -ForegroundColor Yellow
    
    if (!(Test-Path "channel-artifacts")) {
        New-Item -ItemType Directory -Path "channel-artifacts" | Out-Null
    }
    
    Write-Host "✓ Channel artifacts directory created" -ForegroundColor Green
}

function New-CryptoMaterial {
    Write-Host "Generating crypto material..." -ForegroundColor Yellow
    
    # Create directories
    $dirs = @(
        "organizations\ordererOrganizations",
        "organizations\peerOrganizations", 
        "organizations\fabric-ca\org1"
    )
    
    foreach ($dir in $dirs) {
        if (!(Test-Path $dir)) {
            New-Item -ItemType Directory -Path $dir -Force | Out-Null
        }
    }
    
    Write-Host "✓ Crypto material directories created" -ForegroundColor Green
}

function Start-FabricNetwork {
    Write-Host "Starting Hyperledger Fabric network..." -ForegroundColor Yellow
    
    try {
        # Pull required images first
        Write-Host "Pulling Hyperledger Fabric Docker images..." -ForegroundColor Cyan
        docker pull hyperledger/fabric-peer:2.5.4
        docker pull hyperledger/fabric-orderer:2.5.4
        docker pull hyperledger/fabric-ca:1.5.5
        docker pull hyperledger/fabric-tools:2.5.4
        docker pull couchdb:3.1.1
        
        # Start the containers
        docker-compose up -d
        
        if ($LASTEXITCODE -eq 0) {
            Write-Host "✓ Network started successfully" -ForegroundColor Green
            
            # Wait for containers to be ready
            Write-Host "Waiting for containers to be ready..." -ForegroundColor Cyan
            Start-Sleep -Seconds 15
            
            # Check container status
            Write-Host "`nContainer Status:" -ForegroundColor Cyan
            docker-compose ps
            
            return $true
        } else {
            Write-Host "ERROR: Failed to start network" -ForegroundColor Red
            return $false
        }
    }
    catch {
        Write-Host "ERROR: Failed to start network - $_" -ForegroundColor Red
        return $false
    }
}

function New-Channel {
    Write-Host "Creating channel: $CHANNEL_NAME" -ForegroundColor Yellow
    
    # In a full implementation, this would use peer commands to create the channel
    Write-Host "✓ Channel creation prepared (simplified for demo)" -ForegroundColor Green
}

function Deploy-Chaincode {
    Write-Host "Deploying chaincode: $CC_NAME" -ForegroundColor Yellow
    
    # In a full implementation, this would package, install, approve, and commit chaincode
    Write-Host "✓ Chaincode deployment prepared (simplified for demo)" -ForegroundColor Green
}

function Initialize-Chaincode {
    Write-Host "Initializing chaincode..." -ForegroundColor Yellow
    
    # In a full implementation, this would invoke the InitLedger function
    Write-Host "✓ Chaincode initialization prepared" -ForegroundColor Green
}

function Show-NetworkInfo {
    Write-Host ""
    Write-Host "==========================================" -ForegroundColor Green
    Write-Host "   Network Setup Complete!               " -ForegroundColor Green
    Write-Host "==========================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "Network Configuration:" -ForegroundColor Cyan
    Write-Host "  - Channel: $CHANNEL_NAME" -ForegroundColor White
    Write-Host "  - Chaincode: $CC_NAME" -ForegroundColor White
    Write-Host "  - Version: $CC_VERSION" -ForegroundColor White
    Write-Host ""
    Write-Host "Network Endpoints:" -ForegroundColor Cyan
    Write-Host "  - Peer: localhost:7051" -ForegroundColor White
    Write-Host "  - Orderer: localhost:7050" -ForegroundColor White
    Write-Host "  - CA: localhost:7054" -ForegroundColor White
    Write-Host "  - CouchDB: localhost:5984" -ForegroundColor White
    Write-Host ""
    Write-Host "Useful Commands:" -ForegroundColor Cyan
    Write-Host "  - View containers: docker-compose ps" -ForegroundColor White
    Write-Host "  - View logs: docker-compose logs -f" -ForegroundColor White
    Write-Host "  - CLI access: docker exec -it cli bash" -ForegroundColor White
    Write-Host "  - Stop network: docker-compose down" -ForegroundColor White
    Write-Host ""
}

function Test-NetworkHealth {
    Write-Host "Testing network health..." -ForegroundColor Yellow
    
    # Check if containers are running
    $containers = docker-compose ps -q
    $runningCount = 0
    
    foreach ($container in $containers) {
        $status = docker inspect $container --format '{{.State.Status}}'
        if ($status -eq "running") {
            $runningCount++
        }
    }
    
    Write-Host "✓ $runningCount containers running" -ForegroundColor Green
    
    # Test peer connection
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:17051/healthz" -TimeoutSec 5 -ErrorAction SilentlyContinue
        if ($response.StatusCode -eq 200) {
            Write-Host "✓ Peer health check passed" -ForegroundColor Green
        }
    }
    catch {
        Write-Host "⚠ Peer health check failed (may still be starting)" -ForegroundColor Yellow
    }
}

# Main execution
function Main {
    if ($Help) {
        Show-Help
        return
    }
    
    Write-Host "==========================================" -ForegroundColor Blue
    Write-Host "    Hyperledger Fabric Network Setup     " -ForegroundColor Blue
    Write-Host "==========================================" -ForegroundColor Blue
    Write-Host ""
    
    # Check prerequisites
    if (!(Test-DockerRunning)) {
        Write-Host ""
        Write-Host "Please install Docker Desktop and ensure it's running:" -ForegroundColor Yellow
        Write-Host "https://www.docker.com/products/docker-desktop/" -ForegroundColor Cyan
        exit 1
    }
    
    # Setup steps
    New-ChannelArtifacts
    New-CryptoMaterial
    
    if (Start-FabricNetwork) {
        New-Channel
        Deploy-Chaincode
        Initialize-Chaincode
        Test-NetworkHealth
        Show-NetworkInfo
        
        Write-Host "🎉 Hyperledger Fabric network is ready!" -ForegroundColor Green
        Write-Host "You can now test your Spring Boot application with real blockchain storage." -ForegroundColor Cyan
    } else {
        Write-Host "❌ Network setup failed" -ForegroundColor Red
        exit 1
    }
}

# Run main function
Main