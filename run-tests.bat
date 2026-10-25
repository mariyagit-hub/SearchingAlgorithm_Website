@echo off
setlocal
if not exist build\classes mkdir build\classes
if not exist build mkdir build
dir /s /b src\main\java\*.java src\test\java\*.java > build\test-sources.txt
javac -encoding UTF-8 -d build\classes @build\test-sources.txt
if errorlevel 1 (
    echo Test compilation failed. Check the Java compiler output above.
    exit /b 1
)
java -ea -Djava.awt.headless=true -cp build\classes edu.naap.searching.AlgorithmTests
if errorlevel 1 exit /b 1
