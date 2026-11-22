param(
  [string]$Email = "",
  [string]$Password = ""
)

$ErrorActionPreference = "Stop"
$base = "http://localhost:8081/api"

Write-Host "Checking shared Bazaar-e-Hunar API..." -ForegroundColor Cyan
$health = Invoke-RestMethod -Method Get -Uri "$base/health"
Write-Host "Health: OK" -ForegroundColor Green

if ([string]::IsNullOrWhiteSpace($Email)) {
  $Email = Read-Host "Admin email"
}
if ([string]::IsNullOrWhiteSpace($Password)) {
  $secure = Read-Host "Admin password" -AsSecureString
  $ptr = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($secure)
  try { $Password = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($ptr) }
  finally { [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($ptr) }
}

$body = @{ email = $Email; password = $Password } | ConvertTo-Json
$login = Invoke-RestMethod -Method Post -Uri "$base/auth/login" -ContentType "application/json" -Body $body
if (-not $login.token) { throw "Login did not return a JWT token." }
$headers = @{ Authorization = "Bearer $($login.token)" }

$checks = @(
  @{ Name = "Admin dashboard"; Path = "/admin/dashboard" },
  @{ Name = "Admin notifications"; Path = "/admin/notifications" },
  @{ Name = "Admin categories"; Path = "/admin/categories" },
  @{ Name = "Admin messages"; Path = "/admin/messages" },
  @{ Name = "Admin custom orders"; Path = "/admin/custom-orders" },
  @{ Name = "Admin orders"; Path = "/orders" },
  @{ Name = "Admin products"; Path = "/products?includeInactive=true" }
)

foreach ($check in $checks) {
  try {
    Invoke-RestMethod -Method Get -Uri "$base$($check.Path)" -Headers $headers | Out-Null
    Write-Host ("PASS  " + $check.Name) -ForegroundColor Green
  } catch {
    Write-Host ("FAIL  " + $check.Name + ": " + $_.Exception.Message) -ForegroundColor Red
    throw
  }
}

$publicProducts = Invoke-RestMethod -Method Get -Uri "$base/products"
$publicCategories = Invoke-RestMethod -Method Get -Uri "$base/categories"
Write-Host ("PASS  Public storefront products (" + @($publicProducts).Count + ")") -ForegroundColor Green
Write-Host ("PASS  Public storefront categories (" + @($publicCategories).Count + ")") -ForegroundColor Green
Write-Host "All shared API checks passed." -ForegroundColor Green
