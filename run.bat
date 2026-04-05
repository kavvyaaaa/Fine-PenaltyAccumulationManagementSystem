@echo off
echo ==========================================
echo  PFAMS - Penalty/Fine Management System
echo ==========================================
echo.
echo Compiling Java source files...
javac -d out -cp "lib\mysql-connector-j-9.6.0.jar" src\model\*.java src\util\*.java src\dao\*.java src\service\*.java src\controller\*.java src\gui\*.java src\main\*.java

if %ERRORLEVEL% neq 0 (
    echo.
    echo [ERROR] Compilation failed! Please check your code.
    pause
    exit /b %ERRORLEVEL%
)

echo Compilation successful!
echo.
echo Starting PFAMS...
java -cp "out;lib\mysql-connector-j-9.6.0.jar" main.Main
