@echo off
setlocal
if not exist out mkdir out
if not exist dist mkdir dist
dir /s /b src\main\java\*.java > sources.txt
javac -encoding UTF-8 -d out @sources.txt
if errorlevel 1 (
  echo Compilation failed. Check the error messages above.
  del sources.txt
  exit /b 1
)
del sources.txt
jar --create --file dist\minibank.jar --main-class minibank.app.MiniBank -C out .
if errorlevel 1 exit /b 1
echo Created dist\minibank.jar
echo Run it with: java -jar dist\minibank.jar
