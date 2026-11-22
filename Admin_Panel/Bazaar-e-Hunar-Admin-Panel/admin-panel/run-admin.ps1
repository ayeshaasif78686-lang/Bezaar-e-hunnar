$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot

if (-not (Get-Command node -ErrorAction SilentlyContinue) -or -not (Get-Command npm -ErrorAction SilentlyContinue)) {
    throw "Node.js 18+ and npm are required. Install Node.js, then reopen PowerShell."
}

if (-not (Test-Path (Join-Path $PSScriptRoot "node_modules/vite/bin/vite.js"))) {
    Write-Host "Installing admin panel dependencies for the first run..." -ForegroundColor Yellow
    npm install
    if ($LASTEXITCODE -ne 0) { throw "npm install failed. Check your internet connection and npm registry access." }
}

Write-Host "Admin panel: http://localhost:5174" -ForegroundColor Green
npm run dev -- --host 127.0.0.1
