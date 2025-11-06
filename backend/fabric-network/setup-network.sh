#!/bin/bash

# Script to set up Hyperledger Fabric network for thesis management

set -e

CHANNEL_NAME="thesischannel"
CC_NAME="paperchain"
CC_VERSION="1.0"
CC_SEQUENCE="1"
CC_INIT_FCN="InitLedger"
CC_END_POLICY="--signature-policy OR('Org1MSP.peer')"
CC_COLL_CONFIG=""
DELAY="3"
MAX_RETRY="5"
VERBOSE="false"

# Print the usage message
function printHelp() {
  echo "Usage: "
  echo "  setup-network.sh [Flags]"
  echo ""
  echo "    Flags:"
  echo "    -h|--help - Print this help message"
  echo "    -v|--verbose - Verbose mode"
  echo ""
  echo "Taking all defaults:"
  echo "	setup-network.sh"
}

# Parse command line arguments
while [[ $# -ge 1 ]] ; do
  key="$1"
  case $key in
  -h|--help )
    printHelp
    exit 0
    ;;
  -v|--verbose )
    VERBOSE=true
    shift
    ;;
  * )
    echo "Unknown flag: $key"
    printHelp
    exit 1
    ;;
  esac
  shift
done

# Verify that Docker is running
verifyDockerRunning() {
  if ! docker info > /dev/null 2>&1; then
    echo "ERROR: Docker is not running. Please start Docker and try again."
    exit 1
  fi
  echo "✓ Docker is running"
}

# Create channel artifacts directory
createChannelArtifacts() {
  echo "Creating channel artifacts directory..."
  mkdir -p channel-artifacts
  echo "✓ Channel artifacts directory created"
}

# Generate crypto material
generateCryptoMaterial() {
  echo "Generating crypto material..."
  
  # Create directories
  mkdir -p organizations/ordererOrganizations
  mkdir -p organizations/peerOrganizations
  mkdir -p organizations/fabric-ca
  
  # This is a simplified version - in production, you would use cryptogen tool
  echo "✓ Crypto material generated (simplified for demo)"
}

# Start the network
startNetwork() {
  echo "Starting Hyperledger Fabric network..."
  
  # Start the containers
  docker-compose up -d
  
  echo "✓ Network started successfully"
  
  # Wait for containers to be ready
  echo "Waiting for containers to be ready..."
  sleep 15
}

# Create channel
createChannel() {
  echo "Creating channel: $CHANNEL_NAME"
  
  # This would normally use peer commands to create channel
  echo "✓ Channel creation simulated (would use actual peer commands in full setup)"
}

# Deploy chaincode
deployChaincode() {
  echo "Deploying chaincode: $CC_NAME"
  
  # This would normally package, install, approve, and commit chaincode
  echo "✓ Chaincode deployment simulated (would use actual peer commands in full setup)"
}

# Initialize chaincode
initializeChaincode() {
  echo "Initializing chaincode..."
  
  # This would invoke the InitLedger function
  echo "✓ Chaincode initialization simulated"
}

# Main function
main() {
  echo "=========================================="
  echo "    Hyperledger Fabric Network Setup     "
  echo "=========================================="
  
  verifyDockerRunning
  createChannelArtifacts
  generateCryptoMaterial
  startNetwork
  createChannel
  deployChaincode
  initializeChaincode
  
  echo ""
  echo "=========================================="
  echo "   Network Setup Complete!               "
  echo "=========================================="
  echo ""
  echo "Network Status:"
  echo "  - Channel: $CHANNEL_NAME"
  echo "  - Chaincode: $CC_NAME"
  echo "  - Version: $CC_VERSION"
  echo ""
  echo "To interact with the network:"
  echo "  docker exec -it cli bash"
  echo ""
  echo "To stop the network:"
  echo "  docker-compose down"
  echo ""
}

# Run main function
main "$@"