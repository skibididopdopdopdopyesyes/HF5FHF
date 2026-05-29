@echo off
setlocal

set DIRNAME=%~dp0
set APP_HOME=%DIRNAME%

set WRAPPER_JAR="%APP_HOME%\gradle\wrapper\gradle-wrapper.jar"
set WRAPPER_PROPERTIES="%APP_HOME%\gradle\wrapper\gradle-wrapper.properties"

"%JAVA_EXE%" -version 2>nul
if %errorlevel% neq 0 (
    echo Java not found. Please install Java 21.
    pause
    exit /b 1
)

"%JAVA_EXE%" -jar "%WRAPPER_JAR%" %*
if %errorlevel% neq 0 (
    echo Gradle build failed.
    pause
    exit /b %errorlevel%
)

pause
