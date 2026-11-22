$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot

Write-Host "Bazaar-e-Hunar Backend Launcher" -ForegroundColor Cyan
Write-Host "Storage: MongoDB (MONGODB_URI from backend/.env)" -ForegroundColor DarkGray

if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
    Write-Host "Java is not installed or not in PATH." -ForegroundColor Red
    exit 1
}

if (-not (Get-Command mvn -ErrorAction SilentlyContinue)) {
    Write-Host "Maven is not installed or not in PATH." -ForegroundColor Red
    exit 1
}

# Stop a stale project process on 8081 so repeated launches do not fail.
$existing = Get-NetTCPConnection -LocalPort 8081 -State Listen -ErrorAction SilentlyContinue | Select-Object -First 1
if ($existing) {
    Write-Host "Port 8081 is already in use. Stopping the stale process..." -ForegroundColor Yellow
    Stop-Process -Id $existing.OwningProcess -Force -ErrorAction SilentlyContinue
    Start-Sleep -Seconds 1
}

if (-not $env:JWT_SECRET) {
    $env:JWT_SECRET = "bazaar-e-hunar-local-development-secret-key-12345678901234567890"
}

$env:SERVER_PORT = "8081"
Write-Host "Starting backend on http://localhost:8081" -ForegroundColor Green
mvn spring-boot:run
