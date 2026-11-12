# FEAT-08-T3: el bloqueo manual revoca la sesión abierta en el despliegue (SPEC-15 RF-15.4).
# Uso: powershell -ExecutionPolicy Bypass -File FEAT-08-revocacion-bloqueo.ps1
# Necesita una cuenta CLIENTE desechable, sin MFA. El bloqueo y el desbloqueo se hacen
# en la SPA con una cuenta ADMIN_SISTEMA mientras el guion espera.
# No imprime contraseñas ni tokens: solo estados HTTP y códigos de error.
param([string]$Base = 'https://g7-auth-service.onrender.com/api/v1')
$ErrorActionPreference = 'Stop'
[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12

function Plano([Security.SecureString]$s) {
    $p = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($s)
    try { [Runtime.InteropServices.Marshal]::PtrToStringBSTR($p) } finally { [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($p) }
}

function Llamar([string]$metodo, [string]$ruta, [hashtable]$cuerpo, [string]$acceso) {
    $opc = @{ UseBasicParsing = $true; Method = $metodo; Uri = "$Base$ruta" }
    if ($cuerpo) { $opc.ContentType = 'application/json; charset=utf-8'; $opc.Body = [Text.Encoding]::UTF8.GetBytes(($cuerpo | ConvertTo-Json -Compress)) }
    if ($acceso) { $opc.Headers = @{ Authorization = "Bearer $acceso" } }
    try {
        $r = Invoke-WebRequest @opc
        $estado = [int]$r.StatusCode; $texto = $r.Content
    } catch {
        $resp = $_.Exception.Response
        if ($null -eq $resp) { throw }
        $estado = [int]$resp.StatusCode
        $texto = if ($_.ErrorDetails -and $_.ErrorDetails.Message) { $_.ErrorDetails.Message } else { (New-Object IO.StreamReader($resp.GetResponseStream())).ReadToEnd() }
    }
    $obj = $null
    if ($texto) { try { $obj = $texto | ConvertFrom-Json } catch { } }
    [pscustomobject]@{ Estado = $estado; Cuerpo = $obj }
}

function Mostrar([string]$paso, [string]$esperado, $r) {
    $codigo = if ($r.Cuerpo -and $r.Cuerpo.code) { $r.Cuerpo.code } else { '-' }
    $obtenido = "$($r.Estado) $codigo"
    $marca = if ($esperado -eq 'informativo') { 'INF' } elseif ($obtenido.Trim() -eq $esperado -or ($esperado -match '^\d+$' -and $r.Estado -eq [int]$esperado)) { 'OK ' } else { 'DIF' }
    $hora = Get-Date -Format 'HH:mm:ss'
    '{0} | {1} | {2,-46} | esperado: {3,-28} | obtenido: {4}' -f $marca, $hora, $paso, $esperado, $obtenido
}

$correo = Read-Host 'Correo de la cuenta CLIENTE desechable'
$clave = Plano (Read-Host 'Contraseña (no se muestra)' -AsSecureString)
Write-Host "`nInicio: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss') (hora local)`n"

$login = Llamar 'POST' '/auth/login' @{ correo = $correo; contrasena = $clave }
Write-Host (Mostrar 'login de la cuenta' '200' $login)
if ($login.Estado -ne 200 -or -not $login.Cuerpo.refreshToken) { Write-Host 'Sin sesión: revisa la cuenta (sin MFA, ACTIVA).'; exit 1 }
$refresco = $login.Cuerpo.refreshToken; $acceso = $login.Cuerpo.accessToken

$r = Llamar 'POST' '/auth/refresh' @{ refreshToken = $refresco }
Write-Host (Mostrar 'renovar antes del bloqueo' '200' $r)
if ($r.Estado -eq 200) { $refresco = $r.Cuerpo.refreshToken; $acceso = $r.Cuerpo.accessToken }
Write-Host (Mostrar 'GET /auth/me antes del bloqueo' '200' (Llamar 'GET' '/auth/me' $null $acceso))

Write-Host "`n>>> Ahora, en la SPA con tu ADMIN_SISTEMA, BLOQUEA esta cuenta con el motivo «Prueba FEAT-08-T3»."
Read-Host '>>> Cuando veas «Cuenta bloqueada», presiona Enter' | Out-Null

Write-Host (Mostrar 'renovar la sesión abierta tras el bloqueo' '401 REFRESCO_INVALIDO' (Llamar 'POST' '/auth/refresh' @{ refreshToken = $refresco }))
Write-Host (Mostrar 'GET /auth/me con el acceso emitido antes' 'informativo' (Llamar 'GET' '/auth/me' $null $acceso))
Write-Host (Mostrar 'login durante el bloqueo' '401 CREDENCIALES_INVALIDAS' (Llamar 'POST' '/auth/login' @{ correo = $correo; contrasena = $clave }))

Write-Host "`n>>> Ahora, en la SPA, DESBLOQUEA esta cuenta."
Read-Host '>>> Cuando veas «Cuenta desbloqueada», presiona Enter' | Out-Null

Write-Host (Mostrar 'renovar con el refresco revocado, ya desbloqueada' '401 REFRESCO_INVALIDO' (Llamar 'POST' '/auth/refresh' @{ refreshToken = $refresco }))
$nuevo = Llamar 'POST' '/auth/login' @{ correo = $correo; contrasena = $clave }
Write-Host (Mostrar 'login después del desbloqueo' '200' $nuevo)
if ($nuevo.Estado -eq 200) { Write-Host (Mostrar 'cerrar la sesión nueva' '204' (Llamar 'POST' '/auth/logout' @{ refreshToken = $nuevo.Cuerpo.refreshToken })) }
$clave = $null; $refresco = $null; $acceso = $null; $nuevo = $null
Write-Host "`nFin: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss') (hora local)"
