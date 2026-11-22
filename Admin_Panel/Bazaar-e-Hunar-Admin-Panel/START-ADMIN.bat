@echo off
set ROOT=%~dp0
start "Bazaar-e-Hunar Backend" powershell -NoExit -ExecutionPolicy Bypass -File "%ROOT%backend\run-backend.ps1"
timeout /t 3 /nobreak >nul
start "Bazaar-e-Hunar Admin Panel" powershell -NoExit -ExecutionPolicy Bypass -File "%ROOT%admin-panel\run-admin.ps1"
echo.
echo Backend: http://localhost:8081/api/health
echo Admin:   http://localhost:5174
 echo.
echo Keep both terminal windows open while using the admin panel.
