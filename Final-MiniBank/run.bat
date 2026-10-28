@echo off
setlocal
if not exist out mkdir out
dir /s /b src\main\java\*.java > sources.txt
javac -encoding UTF-8 -d out @sources.txt
if errorlevel 1 (
  echo Compilation failed. Check the error messages above.
  del sources.txt
  exit /b 1
)
del sources.txt
java -cp out minibank.app.MiniBank
