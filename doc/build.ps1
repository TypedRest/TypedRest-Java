﻿$ErrorActionPreference = "Stop"
pushd $PSScriptRoot

# Ensure 0install is in PATH
if (!(Get-Command 0install -ErrorAction SilentlyContinue)) {
    echo "Downloading 0install"
    mkdir -Force "$env:TEMP\zero-install" | Out-Null
    Invoke-WebRequest "https://0install.de/files/0install.exe" -OutFile "$env:TEMP\zero-install\0install.exe"
    $env:PATH = "$env:TEMP\zero-install;$env:PATH"
}

if (Test-Path ..\target\doc) {rm -Recurse -Force ..\target\doc}
mkdir ..\target\doc | Out-Null

0install run --batch https://apps.0install.net/devel/doxygen.xml
if ($LASTEXITCODE -ne 0) {throw "Exit Code: $LASTEXITCODE"}

popd
