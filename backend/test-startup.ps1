# PowerShell script to test Spring Boot application startup
Write-Host "Testing Spring Boot application startup..." -ForegroundColor Green

# Change to project directory
Set-Location "c:\Users\chanc\eclipse-workspace\blockchain_project"

# Clean and compile the project
Write-Host "Cleaning and compiling project..." -ForegroundColor Yellow
./mvnw.cmd clean compile

if ($LASTEXITCODE -eq 0) {
    Write-Host "Compilation successful!" -ForegroundColor Green
    
    # Try to package the application
    Write-Host "Creating package..." -ForegroundColor Yellow
    ./mvnw.cmd package -DskipTests
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Package creation successful!" -ForegroundColor Green
        Write-Host "You can now start the application with: ./mvnw.cmd spring-boot:run" -ForegroundColor Cyan
    } else {
        Write-Host "Package creation failed!" -ForegroundColor Red
    }
} else {
    Write-Host "Compilation failed! Check the errors above." -ForegroundColor Red
}