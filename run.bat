@echo off
javac -d bin src\model\*.java src\ui\*.java
java -cp bin ui.MainFrame
pause