# Ejecutar: powershell -NoProfile -File .\scripts\Test-AndroidEnvironment.ps1
# Carga Use-AndroidEnv en este proceso. No dot-sourcear este diagnostico: termina con exit.
# No instala, acepta licencias, inicia AVD, compila una app ni envia ordenes al dron.
[CmdletBinding()]
param([string]$SdkPath, [string]$JdkPath)

$ErrorActionPreference = 'Continue'
$script:missing = 0
function Report($State, $Message) { Write-Host "[$State] $Message" }
function Missing($Message) { $script:missing++; Report 'FALTA' $Message }
function Run-Check($Label, $Executable, [string[]]$Arguments, [switch]$Optional) {
    if (-not $Executable -or -not (Test-Path -LiteralPath $Executable -PathType Leaf)) {
        if ($Optional) { Report 'AVISO' "$Label no disponible" } else { Missing "$Label no disponible" }
        return
    }
    $output = @(& $Executable @Arguments 2>&1 | ForEach-Object { "$_" })
    if ($LASTEXITCODE -ne 0) {
        if ($Optional) { Report 'AVISO' "$Label falla (codigo $LASTEXITCODE)" }
        else { Missing "$Label falla (codigo $LASTEXITCODE)" }
        return
    }
    # Algunos .bat imprimen sus comandos si el proceso hereda DEBUG; no mostrar ese eco.
    if ($Label -eq 'SDK Manager') {
        $output = @($output | Where-Object { $_.Trim() -match '^\d+(\.\d+)+[^\s]*$' })
        if ($output.Count -eq 0) { Report 'AVISO' 'SDK Manager ejecutado; version no reconocida'; return }
    }
    $summary = ($output | Where-Object { $_.Trim() } | Select-Object -First 3) -join ' | '
    Report 'OK' "$Label : $summary"
}
function Command-Path($Name) {
    $command = Get-Command $Name -CommandType Application -ErrorAction SilentlyContinue |
        Select-Object -First 1
    if ($command) { return $command.Source }
}

Report 'INFO' 'Diagnostico local Android (sin validacion de vuelo ni compatibilidad DJI).'
try {
    . (Join-Path $PSScriptRoot 'Use-AndroidEnv.ps1') -SdkPath $SdkPath -JdkPath $JdkPath
    Report 'OK' "JDK: $env:JAVA_HOME"
    Report 'OK' "SDK: $env:ANDROID_HOME"
} catch { Missing $_.Exception.Message }

Run-Check 'Git' (Command-Path 'git.exe') @('--version')
Run-Check 'Java' (Command-Path 'java.exe') @('-version')
Run-Check 'Javac' (Command-Path 'javac.exe') @('-version')
$sdkRoot = $env:ANDROID_HOME
if ($sdkRoot -and (Test-Path -LiteralPath $sdkRoot -PathType Container)) {
    $sdkManager = Join-Path $sdkRoot 'cmdline-tools\latest\bin\sdkmanager.bat'
    if (-not (Test-Path -LiteralPath $sdkManager)) {
        $sdkManager = Get-ChildItem -Path (Join-Path $sdkRoot 'cmdline-tools\*\bin\sdkmanager.bat') -ErrorAction SilentlyContinue |
            Select-Object -First 1 -ExpandProperty FullName
    }
    Run-Check 'SDK Manager' $sdkManager @('--version')
    $adbPath = Join-Path $sdkRoot 'platform-tools\adb.exe'
    Run-Check 'ADB' $adbPath @('version')
    $platforms = @(Get-ChildItem -LiteralPath (Join-Path $sdkRoot 'platforms') -Directory -ErrorAction SilentlyContinue |
        Where-Object { Test-Path -LiteralPath (Join-Path $_.FullName 'android.jar') })
    $buildTools = @(Get-ChildItem -LiteralPath (Join-Path $sdkRoot 'build-tools') -Directory -ErrorAction SilentlyContinue |
        Where-Object { (Test-Path -LiteralPath (Join-Path $_.FullName 'aapt2.exe')) -and
            (Test-Path -LiteralPath (Join-Path $_.FullName 'd8.bat')) })
    if ($platforms.Count -eq 0) { Missing 'No hay plataformas Android instaladas' }
    else {
        Report 'OK' "Plataformas: $(($platforms.Name) -join ', ')"
        $basePlatforms = @($platforms | Where-Object {
            $properties = Join-Path $_.FullName 'source.properties'
            $_.Name -match '^android-\d+$' -and (Test-Path -LiteralPath $properties) -and
                ((Get-Content -LiteralPath $properties -Raw) -notmatch 'AndroidVersion.CodeName=(?!REL\b)\S+')
        })
        if ($basePlatforms.Count -eq 0) { Report 'INFO' 'Solo plataformas con sufijo detectadas; elegir el paquete compileSdk exacto al crear la app. No implica que falte un SDK utilizable.' }
    }
    if ($buildTools.Count -eq 0) { Missing 'No hay Android Build Tools instaladas' }
    else { Report 'OK' "Build Tools: $(($buildTools.Name) -join ', ')" }
    $license = Join-Path $sdkRoot 'licenses\android-sdk-license'
    if ((Test-Path -LiteralPath $license) -and (Get-Item -LiteralPath $license).Length -gt 0) {
        Report 'OK' 'Hay registro local de licencia Android SDK; cada paquete puede requerir licencias adicionales.'
    } else { Missing 'No hay registro local de licencia Android SDK; revisar sdkmanager --licenses manualmente.' }

    $emulatorPath = Join-Path $sdkRoot 'emulator\emulator.exe'
    Run-Check 'Emulador' $emulatorPath @('-version') -Optional
    if (Test-Path -LiteralPath $emulatorPath) {
        Run-Check 'Aceleracion' $emulatorPath @('-accel-check') -Optional
        $avds = @(& $emulatorPath -list-avds 2>$null | Where-Object { $_.Trim() })
        if ($LASTEXITCODE -ne 0) { Report 'AVISO' 'No se pudo consultar la lista de AVD' }
        elseif ($avds.Count -eq 0) { Report 'AVISO' 'No hay AVD configurados' }
        else { Report 'OK' "AVD configurados: $($avds -join ', ') (no iniciados)" }
    }
    if (Test-Path -LiteralPath $adbPath) {
        # Puede iniciar el servidor local ADB; nunca muestra identificadores de dispositivos.
        $deviceLines = @(& $adbPath devices 2>$null)
        if ($LASTEXITCODE -ne 0) { Report 'AVISO' 'No se pudo consultar dispositivos ADB' }
        else {
            $states = @($deviceLines | ForEach-Object {
                if ($_ -match '^\S+\s+(device|offline|unauthorized|recovery|sideload|bootloader)\b') { $Matches[1] }
            })
            if ($states.Count -eq 0) { Report 'AVISO' 'Ningun dispositivo ADB conectado; falta la prueba en movil Android real.' }
            else {
                $counts = @($states | Group-Object | ForEach-Object { "$($_.Name)=$($_.Count)" })
                Report 'INFO' "Dispositivos ADB por estado: $($counts -join ', ') (incluye posibles emuladores)"
            }
        }
    }
} else { Missing 'No se puede inspeccionar Android SDK' }

Report 'INFO' 'La compilacion requiere una app y su Gradle Wrapper; este script no los presupone.'
Report 'INFO' 'Exito solo confirma requisitos basicos locales; no valida SDK DJI, movil, USB ni dron.'
if ($script:missing -gt 0) { Report 'FALTA' "Carencias basicas: $script:missing"; exit 1 }
Report 'OK' 'Diagnostico basico completado. Revisar los avisos antes de desarrollar/probar.'
exit 0
