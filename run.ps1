# run.ps1 — Start ExploreBangladesh backend
# USE THIS instead of: .\mvnw.cmd spring-boot:run
# It kills any stale Java/port-8080 process first, so it NEVER fails with "port already in use".

Write-Host ""
Write-Host "  ExploreBangladesh — Starting backend" -ForegroundColor Cyan
Write-Host "  TIP: Always use .\run.ps1, never .\mvnw.cmd spring-boot:run directly" -ForegroundColor DarkGray
Write-Host ""

$port = 8080

# Kill any existing Java processes
$javaProcs = Get-Process -Name "java" -ErrorAction SilentlyContinue
if ($javaProcs) {
    Write-Host "Stopping $($javaProcs.Count) running Java process(es)..." -ForegroundColor Yellow
    $javaProcs | Stop-Process -Force
    Start-Sleep -Seconds 2
}

# Double-check the port is free
$portInUse = netstat -ano | Select-String ":$port\s"
if ($portInUse) {
    # Extract PID from netstat and kill it
    $pid = ($portInUse -split '\s+')[-1]
    Write-Host "Port $port still held by PID $pid — killing..." -ForegroundColor Yellow
    Stop-Process -Id $pid -Force -ErrorAction SilentlyContinue
    Start-Sleep -Seconds 1
}

Write-Host "Starting Spring Boot on port $port..." -ForegroundColor Green
.\mvnw.cmd spring-boot:run -Prun
