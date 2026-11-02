param(
    [string]$BackupDirectory = (Join-Path $PSScriptRoot '..\backups'),
    [int]$RetentionDays = 30,
    [string]$LoginPath = 'paperly-admin',
    [string]$Database = 'paperly'
)

$ErrorActionPreference = 'Stop'

if ($RetentionDays -lt 1) {
    throw 'RetentionDays must be at least 1.'
}

$backupPath = [System.IO.Path]::GetFullPath($BackupDirectory)
New-Item -ItemType Directory -Path $backupPath -Force | Out-Null

$dumpCommand = Get-Command mysqldump.exe -ErrorAction SilentlyContinue
if (-not $dumpCommand) {
    $defaultDump = Join-Path $env:ProgramFiles 'MySQL\MySQL Server 8.0\bin\mysqldump.exe'
    if (Test-Path $defaultDump) {
        $dumpPath = $defaultDump
    } else {
        throw 'mysqldump.exe was not found. Install the MySQL command-line tools or add them to PATH.'
    }
} else {
    $dumpPath = $dumpCommand.Source
}

$timestamp = Get-Date -Format 'yyyyMMdd-HHmmss'
$temporaryFile = Join-Path $backupPath "paperly-$timestamp.sql.tmp"
$backupFile = Join-Path $backupPath "paperly-$timestamp.sql"

try {
    & $dumpPath "--login-path=$LoginPath" '--single-transaction' '--skip-lock-tables' `
        '--triggers' '--hex-blob' '--no-tablespaces' `
        "--result-file=$temporaryFile" '--databases' $Database
    if ($LASTEXITCODE -ne 0) {
        throw "mysqldump failed with exit code $LASTEXITCODE."
    }
    if (-not (Test-Path $temporaryFile) -or (Get-Item $temporaryFile).Length -eq 0) {
        throw 'mysqldump returned success but did not produce a non-empty backup file.'
    }

    Move-Item -LiteralPath $temporaryFile -Destination $backupFile
    $cutoff = (Get-Date).AddDays(-$RetentionDays)
    Get-ChildItem -LiteralPath $backupPath -Filter 'paperly-*.sql' -File |
        Where-Object { $_.LastWriteTime -lt $cutoff } |
        Remove-Item -Force

    Write-Output "MySQL backup created: $backupFile"
} catch {
    if (Test-Path $temporaryFile) {
        Remove-Item -LiteralPath $temporaryFile -Force
    }
    throw
}
