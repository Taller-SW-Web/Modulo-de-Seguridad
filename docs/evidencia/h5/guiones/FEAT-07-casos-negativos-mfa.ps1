# FEAT-07-T5: casos negativos del segundo factor contra el despliegue (SPEC-09).
# Uso: powershell -ExecutionPolicy Bypass -File FEAT-07-casos-negativos-mfa.ps1 -Caso D
# Casos: D (desafío manipulado), A (intentos), B (reutilización), C (vencido), E (cuota).
# Orden recomendado: D, A, B, C y E seguidos, en menos de 15 minutos.
# No imprime contraseñas, códigos ni tokens: solo estados HTTP y códigos de error.
param(
    [Parameter(Mandatory = $true)][ValidateSet('A', 'B', 'C', 'D', 'E')][string]$Caso,
    [string]$Base = 'https://g7-auth-service.onrender.com/api/v1'
)
$ErrorActionPreference = 'Stop'
[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12

function Plano([Security.SecureString]$s) {
    $p = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($s)
    try { [Runtime.InteropServices.Marshal]::PtrToStringBSTR($p) } finally { [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($p) }
}

function Llamar([string]$ruta, [hashtable]$cuerpo) {
    $bytes = [Text.Encoding]::UTF8.GetBytes(($cuerpo | ConvertTo-Json -Compress))
    try {
        $r = Invoke-WebRequest -UseBasicParsing -Method Post -Uri "$Base$ruta" -ContentType 'application/json; charset=utf-8' -Body $bytes
        $estado = [int]$r.StatusCode; $texto = $r.Content; $retry = $r.Headers['Retry-After']
    } catch {
        $resp = $_.Exception.Response
        if ($null -eq $resp) { throw }
        $estado = [int]$resp.StatusCode; $retry = $resp.Headers['Retry-After']
        $texto = (New-Object IO.StreamReader($resp.GetResponseStream())).ReadToEnd()
    }
    $obj = $null
    if ($texto) { try { $obj = $texto | ConvertFrom-Json } catch { } }
    [pscustomobject]@{ Estado = $estado; Cuerpo = $obj; RetryAfter = $retry }
}

function Mostrar([string]$paso, [string]$esperado, $r) {
    $codigo = if ($r.Cuerpo -and $r.Cuerpo.code) { $r.Cuerpo.code } else { '-' }
    $extra = @()
    if ($r.Cuerpo -and $null -ne $r.Cuerpo.intentosRestantes) { $extra += "intentosRestantes=$($r.Cuerpo.intentosRestantes)" }
    if ($r.RetryAfter) { $extra += "Retry-After=$($r.RetryAfter)" }
    if ($r.Cuerpo -and $r.Cuerpo.accessToken) { $extra += "accessToken con $($r.Cuerpo.accessToken.Split('.').Count) partes (no se muestra)" }
    $obtenido = "$($r.Estado) $codigo"
    $marca = if ($obtenido.Trim() -eq $esperado -or ($esperado -match '^\d+$' -and $r.Estado -eq [int]$esperado)) { 'OK ' } else { 'DIF' }
    '{0} | {1,-42} | esperado: {2,-26} | obtenido: {3} {4}' -f $marca, $paso, $esperado, $obtenido, ($extra -join ' ')
}

function PedirCodigo { Plano (Read-Host 'Código de 6 dígitos que llegó al correo (no se muestra)' -AsSecureString) }

$correo = Read-Host 'Correo de la cuenta con MFA'
$clave = Plano (Read-Host 'Contraseña (no se muestra)' -AsSecureString)

Write-Host "`nCaso $Caso — $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss') (hora local)`n"
$inicio = Get-Date
$login = Llamar '/auth/login' @{ correo = $correo; contrasena = $clave }
$clave = $null
if ($login.Estado -ne 200 -or -not $login.Cuerpo.mfaRequerido) {
    Mostrar 'login' '200 con mfaRequerido' $login
    Write-Host 'El login no pidió segundo factor: usa una cuenta con MFA activo.'; exit 1
}
Write-Host (Mostrar 'login (desafío de 300 s)' '200' $login)
$desafio = $login.Cuerpo.challengeToken

switch ($Caso) {
    'D' {
        $alterado = $desafio.Substring(0, $desafio.Length - 1) + $(if ($desafio.EndsWith('A')) { 'B' } else { 'A' })
        Write-Host (Mostrar 'solicitar con desafío alterado' '401 TOKEN_INVALIDO' (Llamar '/auth/otp/solicitar' @{ challengeToken = $alterado; canal = 'EMAIL' }))
        Write-Host (Mostrar 'verificar con desafío alterado' '401 TOKEN_INVALIDO' (Llamar '/auth/otp/verificar' @{ challengeToken = $alterado; codigo = '123456' }))
        Write-Host (Mostrar 'verificar con desafío inventado' '401 TOKEN_INVALIDO' (Llamar '/auth/otp/verificar' @{ challengeToken = 'desafio-inventado'; codigo = '123456' }))
        Write-Host 'No debe llegar ningún correo.'
    }
    'A' {
        Write-Host (Mostrar 'solicitar código' '202' (Llamar '/auth/otp/solicitar' @{ challengeToken = $desafio; canal = 'EMAIL' }))
        Write-Host (Mostrar 'código erróneo 1' '401 CODIGO_INVALIDO' (Llamar '/auth/otp/verificar' @{ challengeToken = $desafio; codigo = '000000' }))
        Write-Host (Mostrar 'código erróneo 2' '401 CODIGO_INVALIDO' (Llamar '/auth/otp/verificar' @{ challengeToken = $desafio; codigo = '111111' }))
        Write-Host (Mostrar 'código erróneo 3' '401 OTP_INTENTOS_AGOTADOS' (Llamar '/auth/otp/verificar' @{ challengeToken = $desafio; codigo = '222222' }))
        $codigo = PedirCodigo
        Write-Host (Mostrar 'código correcto tras agotar intentos' '401 OTP_INTENTOS_AGOTADOS' (Llamar '/auth/otp/verificar' @{ challengeToken = $desafio; codigo = $codigo }))
    }
    'B' {
        Write-Host (Mostrar 'solicitar código' '202' (Llamar '/auth/otp/solicitar' @{ challengeToken = $desafio; canal = 'EMAIL' }))
        $codigo = PedirCodigo
        $ok = Llamar '/auth/otp/verificar' @{ challengeToken = $desafio; codigo = $codigo }
        Write-Host (Mostrar 'código correcto' '200' $ok)
        Write-Host (Mostrar 'mismo código y desafío otra vez' '401 TOKEN_INVALIDO' (Llamar '/auth/otp/verificar' @{ challengeToken = $desafio; codigo = $codigo }))
        if ($ok.Estado -eq 200) { Write-Host (Mostrar 'cerrar la sesión abierta' '204' (Llamar '/auth/logout' @{ refreshToken = $ok.Cuerpo.refreshToken })) }
        $ok = $null
    }
    'C' {
        Write-Host (Mostrar 'solicitar código' '202' (Llamar '/auth/otp/solicitar' @{ challengeToken = $desafio; canal = 'EMAIL' }))
        $codigo = PedirCodigo
        $espera = [int][Math]::Ceiling(305 - ((Get-Date) - $inicio).TotalSeconds)
        if ($espera -gt 0) { Write-Host "Esperando $espera s a que venzan el código y el desafío..."; Start-Sleep -Seconds $espera }
        Write-Host (Mostrar 'código correcto después de 5 minutos' '401 TOKEN_INVALIDO' (Llamar '/auth/otp/verificar' @{ challengeToken = $desafio; codigo = $codigo }))
    }
    'E' {
        Write-Host (Mostrar 'cuarta solicitud en 15 minutos' '429 DEMASIADAS_SOLICITUDES' (Llamar '/auth/otp/solicitar' @{ challengeToken = $desafio; canal = 'EMAIL' }))
        Write-Host 'No debe llegar ningún correo. Hasta que pase el Retry-After, esta cuenta no puede recibir códigos.'
    }
}
$codigo = $null; $desafio = $null
