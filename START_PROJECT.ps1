$root = $PSScriptRoot
Write-Host "Starting Bazaar-e-Hunar..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList '-NoExit','-ExecutionPolicy','Bypass','-File',"$root\RUN_BACKEND.ps1"
Start-Sleep -Seconds 4
Start-Process powershell -ArgumentList '-NoExit','-ExecutionPolicy','Bypass','-File',"$root\RUN_FRONTEND.ps1"
Write-Host "Backend: http://localhost:8081" -ForegroundColor Green
Write-Host "Frontend: http://localhost:5173" -ForegroundColor Green
