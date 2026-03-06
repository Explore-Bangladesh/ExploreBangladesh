@echo off
echo Starting ExploreBangladesh backend...
echo (Port 8080 will be freed automatically if already in use)
echo.
call mvnw.cmd spring-boot:run -Prun
