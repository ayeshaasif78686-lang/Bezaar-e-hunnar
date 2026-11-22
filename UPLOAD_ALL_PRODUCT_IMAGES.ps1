$ErrorActionPreference = 'Stop'
foreach ($name in 'CLOUDINARY_CLOUD_NAME', 'CLOUDINARY_API_KEY', 'CLOUDINARY_API_SECRET') {
    if ([string]::IsNullOrWhiteSpace([Environment]::GetEnvironmentVariable($name))) { throw "Set $name in this PowerShell window before running this command." }
}
Add-Type -AssemblyName System.Net.Http
$root = $PSScriptRoot
$assetsDirectory = Join-Path $root 'frontend\public\assets\products'
$outputFile = Join-Path $root 'frontend\public\cloudinary-product-images.js'
$client = New-Object System.Net.Http.HttpClient
$mapping = [ordered]@{}
function Get-Sha1Hex([string]$value) {
  $sha1=[System.Security.Cryptography.SHA1]::Create(); try { $bytes=$sha1.ComputeHash([System.Text.Encoding]::UTF8.GetBytes($value)); return ([System.BitConverter]::ToString($bytes)).Replace('-','').ToLowerInvariant() } finally { $sha1.Dispose() }
}
function Get-MimeType([string]$extension) { switch ($extension.ToLowerInvariant()) { '.png' { 'image/png' } '.webp' { 'image/webp' } default { 'image/jpeg' } } }
$images=Get-ChildItem -Path $assetsDirectory -Recurse -File | Sort-Object FullName
if ($images.Count -eq 0) { throw 'No product images were found.' }
foreach ($image in $images) {
  $relativePath=$image.FullName.Substring($assetsDirectory.Length).TrimStart('\').Replace('\','/')
  $parts=$relativePath.Split('/')
  $category=$parts[0]
  $subPath=($parts[1..($parts.Length-1)] -join '/')
  $localUrl="/assets/products/$relativePath"
  $folder="bazaar-e-hunar/products/$category"
  $publicId=[System.IO.Path]::ChangeExtension($subPath,$null).Replace('\','/')
  $timestamp=[DateTimeOffset]::UtcNow.ToUnixTimeSeconds()
  $toSign="folder=$folder&overwrite=true&public_id=$publicId&timestamp=$timestamp$($env:CLOUDINARY_API_SECRET)"
  $signature=Get-Sha1Hex $toSign
  $form=New-Object System.Net.Http.MultipartFormDataContent
  try {
    [byte[]]$bytes=[System.IO.File]::ReadAllBytes($image.FullName)
    $fileContent=[System.Net.Http.ByteArrayContent]::new($bytes)
    $fileContent.Headers.ContentType=[System.Net.Http.Headers.MediaTypeHeaderValue]::Parse((Get-MimeType $image.Extension))
    $form.Add($fileContent,'file',$image.Name)
    foreach ($pair in @(@('api_key',$env:CLOUDINARY_API_KEY),@('timestamp',"$timestamp"),@('folder',$folder),@('public_id',$publicId),@('overwrite','true'),@('signature',$signature))) { $form.Add((New-Object System.Net.Http.StringContent($pair[1])),$pair[0]) }
    $uri="https://api.cloudinary.com/v1_1/$($env:CLOUDINARY_CLOUD_NAME)/image/upload"
    $response=$client.PostAsync($uri,$form).Result; $body=$response.Content.ReadAsStringAsync().Result
    if (-not $response.IsSuccessStatusCode) { throw "Upload failed for $relativePath : $body" }
    $uploaded=$body | ConvertFrom-Json; $mapping[$localUrl]=$uploaded.secure_url
    Write-Host "Uploaded $relativePath -> $folder"
  } finally { $form.Dispose() }
}
$json=$mapping | ConvertTo-Json -Depth 5
[System.IO.File]::WriteAllText($outputFile,"window.__BAZAAR_CLOUDINARY_IMAGES = $json;`r`n",(New-Object System.Text.UTF8Encoding($false)))
Write-Host "Success. $($mapping.Count) images uploaded into category-specific Cloudinary folders."
