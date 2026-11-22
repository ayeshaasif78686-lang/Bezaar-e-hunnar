$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot

Write-Host "Bazaar-e-Hunar Frontend Launcher" -ForegroundColor Cyan

if (-not (Get-Command npm -ErrorAction SilentlyContinue)) {
    Write-Host "Node.js/npm is not installed or not in PATH." -ForegroundColor Red
    exit 1
}

# Stop stale Vite process if port 5173 is still occupied.
$existing = Get-NetTCPConnection -LocalPort 5173 -State Listen -ErrorAction SilentlyContinue | Select-Object -First 1
if ($existing) {
    Write-Host "Port 5173 is already in use. Stopping the stale process..." -ForegroundColor Yellow
    Stop-Process -Id $existing.OwningProcess -Force -ErrorAction SilentlyContinue
    Start-Sleep -Seconds 1
}

$env:VITE_API_URL = "http://localhost:8081/api"

if (-not (Test-Path "node_modules")) {
    Write-Host "Installing frontend dependencies..." -ForegroundColor Yellow
    npm install
}

Write-Host "Starting frontend on http://localhost:5173" -ForegroundColor Green
npm run dev
