param(
    [string]$Time = '02:00',
    [string]$TaskName = 'Paperly-MySQL-Daily-Backup'
)

$ErrorActionPreference = 'Stop'

if ($Time -notmatch '^(?:[01]\d|2[0-3]):[0-5]\d$') {
    throw "Time must use 24-hour HH:mm format, for example '02:00'."
}

$mysqlCommand = Get-Command mysql.exe -ErrorAction SilentlyContinue
if (-not $mysqlCommand) {
    $defaultMysql = Join-Path $env:ProgramFiles 'MySQL\MySQL Server 8.0\bin\mysql.exe'
    if (Test-Path $defaultMysql) {
        $mysqlPath = $defaultMysql
    } else {
        throw 'mysql.exe was not found. Install the MySQL command-line tools or add them to PATH.'
    }
} else {
    $mysqlPath = $mysqlCommand.Source
}

& $mysqlPath '--login-path=paperly-admin' '--database=paperly' '--batch' '--skip-column-names' '--execute=SELECT 1'
if ($LASTEXITCODE -ne 0) {
    throw "Could not connect to database 'paperly' using the 'paperly-admin' login path. Configure it with mysql_config_editor and verify database access before installing the task."
}

$backupScript = Join-Path $PSScriptRoot 'backup-mysql.ps1'
$powershell = Join-Path $PSHOME 'powershell.exe'
$action = New-ScheduledTaskAction `
    -Execute $powershell `
    -Argument "-NoProfile -ExecutionPolicy Bypass -File `"$backupScript`""
$trigger = New-ScheduledTaskTrigger -Daily -At ([datetime]::ParseExact($Time, 'HH:mm', $null))
$principal = New-ScheduledTaskPrincipal `
    -UserId "$env:USERDOMAIN\$env:USERNAME" `
    -LogonType Interactive `
    -RunLevel Limited
$settings = New-ScheduledTaskSettingsSet `
    -StartWhenAvailable `
    -MultipleInstances IgnoreNew `
    -ExecutionTimeLimit (New-TimeSpan -Hours 1)

Register-ScheduledTask `
    -TaskName $TaskName `
    -Action $action `
    -Trigger $trigger `
    -Principal $principal `
    -Settings $settings `
    -Description 'Creates a MySQL dump of Paperly daily and retains the most recent 30 days.' `
    -Force | Out-Null

Write-Output "Scheduled task '$TaskName' installed for daily execution at $Time for the current Windows user."
