# Uso: . .\scripts\Use-AndroidEnv.ps1 [-SdkPath ruta] [-JdkPath ruta]
# Solo modifica JAVA_HOME, ANDROID_HOME y PATH del proceso actual.
# No instala componentes ni selecciona versiones para una app futura.
[CmdletBinding()]
param([string]$SdkPath, [string]$JdkPath)

& {
    param($RequestedSdk, $RequestedJdk)
    function Test-Jdk($Path) {
        return $Path -and (Test-Path -LiteralPath (Join-Path $Path 'bin\java.exe')) -and
            (Test-Path -LiteralPath (Join-Path $Path 'bin\javac.exe'))
    }
    function Find-Jdks($Root) {
        if (-not (Test-Path -LiteralPath $Root -PathType Container)) { return }
        foreach ($folder in @(Get-ChildItem -LiteralPath $Root -Directory)) {
            if (Test-Jdk $folder.FullName) { $folder.FullName }
            else {
                Get-ChildItem -LiteralPath $folder.FullName -Directory | ForEach-Object {
                    if (Test-Jdk $_.FullName) { $_.FullName }
                }
            }
        }
    }

    $selectedSdk = $RequestedSdk
    if ($selectedSdk) {
        if (-not (Test-Path -LiteralPath $selectedSdk -PathType Container)) {
            throw "SDK explicito inexistente: $selectedSdk"
        }
    } else {
        $selectedSdk = @($env:ANDROID_HOME, (Join-Path $env:LOCALAPPDATA 'Android\Sdk')) |
            Where-Object { $_ -and (Test-Path -LiteralPath $_ -PathType Container) } |
            Select-Object -First 1
    }

    $selectedJdk = $RequestedJdk
    if ($selectedJdk) {
        if (-not (Test-Jdk $selectedJdk)) { throw "JDK explicito sin java/javac: $selectedJdk" }
    } else {
        $candidates = @($env:JAVA_HOME) + @(Find-Jdks (Join-Path $env:USERPROFILE '.gradle\jdks')) +
            @(Find-Jdks (Join-Path $env:USERPROFILE '.jdks'))
        $candidates = @($candidates | Where-Object { Test-Jdk $_ } | Select-Object -Unique)
        $selectedJdk = $candidates | Where-Object {
            $release = Join-Path $_ 'release'
            (Test-Path -LiteralPath $release) -and
                ((Get-Content -LiteralPath $release -Raw) -match 'JAVA_VERSION="17[.\"]')
        } | Select-Object -First 1
        if (-not $selectedJdk) {
            $jbr = Join-Path $env:ProgramFiles 'Android\Android Studio\jbr'
            if (Test-Jdk $jbr) { $selectedJdk = $jbr }
            else { $selectedJdk = $candidates | Select-Object -First 1 }
        }
    }
    if (-not $selectedSdk) { throw 'No se encuentra Android SDK; indica -SdkPath.' }
    if (-not $selectedJdk) { throw 'No se encuentra un JDK; indica -JdkPath.' }

    $env:ANDROID_HOME = (Resolve-Path -LiteralPath $selectedSdk).ProviderPath
    $env:JAVA_HOME = (Resolve-Path -LiteralPath $selectedJdk).ProviderPath
    $preferred = @((Join-Path $env:JAVA_HOME 'bin'),
        (Join-Path $env:ANDROID_HOME 'platform-tools'),
        (Join-Path $env:ANDROID_HOME 'cmdline-tools\latest\bin'),
        (Join-Path $env:ANDROID_HOME 'emulator'))
    $entries = @($preferred | Where-Object { Test-Path -LiteralPath $_ -PathType Container }) +
        @($env:PATH -split ';' | Where-Object { $_ })
    $seen = @{}
    $env:PATH = ($entries | Where-Object {
        $key = $_.Trim().Trim('"').TrimEnd('\').ToLowerInvariant()
        if (-not $seen.ContainsKey($key)) { $seen[$key] = $true; $true } else { $false }
    }) -join ';'
    Write-Verbose "JAVA_HOME=$env:JAVA_HOME; ANDROID_HOME=$env:ANDROID_HOME"
} $SdkPath $JdkPath
