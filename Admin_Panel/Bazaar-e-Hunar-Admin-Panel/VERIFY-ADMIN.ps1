$ErrorActionPreference = "Stop"
$base = "http://localhost:8081/api"
$backendEnv = Join-Path $PSScriptRoot "backend/.env"
$email = "admin@bazaarehunar.com"
$password = "Admin123!"

if (Test-Path $backendEnv) {
    Get-Content $backendEnv | ForEach-Object {
        if ($_ -match '^\s*([^#=\s]+)\s*=\s*(.*)\s*$') {
            $key = $matches[1]
            $value = $matches[2].Trim().Trim('"').Trim("'")
            if ($key -eq 'ADMIN_EMAIL' -and $value) { $email = $value }
            if ($key -eq 'ADMIN_PASSWORD' -and $value) { $password = $value }
        }
    }
}

Write-Host "Checking backend health..." -ForegroundColor Cyan
$health = Invoke-RestMethod "$base/health" -TimeoutSec 10
if ($health.status -ne "UP") { throw "Backend health check did not return UP." }
Write-Host "Backend health: UP" -ForegroundColor Green

Write-Host "Checking admin login..." -ForegroundColor Cyan
$login = Invoke-RestMethod "$base/auth/login" -Method Post -ContentType "application/json" -Body (@{ email = $email; password = $password } | ConvertTo-Json) -TimeoutSec 15
if (-not $login.token -or "$($login.role)".ToUpper() -ne "ADMIN") { throw "Login succeeded but the returned account is not ADMIN." }
$headers = @{ Authorization = "Bearer $($login.token)" }

$checks = @(
    @{ Name = "Dashboard"; Path = "/admin/dashboard" },
    @{ Name = "Products"; Path = "/products?includeInactive=true" },
    @{ Name = "Orders"; Path = "/orders" },
    @{ Name = "Categories"; Path = "/admin/categories" },
    @{ Name = "Customers"; Path = "/admin/customers" },
    @{ Name = "Sellers"; Path = "/admin/sellers" },
    @{ Name = "Messages"; Path = "/admin/messages" },
    @{ Name = "Custom Orders"; Path = "/admin/custom-orders" },
    @{ Name = "Settings"; Path = "/admin/settings" },
    @{ Name = "Notifications"; Path = "/admin/notifications" }
)
foreach ($check in $checks) {
    try {
        $null = Invoke-RestMethod "$base$($check.Path)" -Headers $headers -TimeoutSec 15
        Write-Host ("PASS  " + $check.Name) -ForegroundColor Green
    } catch {
        Write-Host ("FAIL  " + $check.Name + ": " + $_.Exception.Message) -ForegroundColor Red
        throw
    }
}
Write-Host "All admin API checks passed." -ForegroundColor Green
