@echo off
setlocal
if not exist build\classes mkdir build\classes
if not exist build mkdir build
dir /s /b src\main\java\*.java > build\sources.txt
javac -encoding UTF-8 -d build\classes @build\sources.txt
if errorlevel 1 (
    echo Compilation failed. Check the Java compiler output above.
    exit /b 1
)
java -cp build\classes edu.naap.searching.Main
