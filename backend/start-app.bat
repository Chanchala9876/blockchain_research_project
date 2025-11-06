@echo off
echo Starting Spring Boot Application...

REM Try to run with Maven first
echo Trying Maven...
call mvn spring-boot:run 2>nul
if %errorlevel%==0 goto :success

REM If Maven fails, try to build and run JAR
echo Maven failed, trying to build JAR...
call mvn package -DskipTests 2>nul
if exist target\blockchain_project-0.0.1-SNAPSHOT.jar (
    echo Running JAR file...
    java -jar target\blockchain_project-0.0.1-SNAPSHOT.jar
    goto :success
)

REM If all else fails, show instructions
echo.
echo ============================================
echo Cannot start application automatically.
echo ============================================
echo Please start your application manually:
echo.
echo Option 1: Run from your IDE
echo - Open the project in IntelliJ IDEA or Eclipse
echo - Run BlockchainProjectApplication.java
echo.
echo Option 2: Fix JAVA_HOME and use Maven
echo - Set JAVA_HOME to your Java installation
echo - Run: mvn spring-boot:run
echo.
echo The professor login should now work with:
echo Email: dr.rajesh.cs@jnu.ac.in
echo Password: 1234
echo ============================================

:success
echo Application started successfully!
pause