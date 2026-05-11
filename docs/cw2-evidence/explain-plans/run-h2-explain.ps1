param(
    [Parameter(Mandatory=$true)]
    [string]$OutputPath
)

$ErrorActionPreference = "Stop"
$repoRoot = Resolve-Path (Join-Path $PSScriptRoot "..\..\..")
$h2Jar = Get-ChildItem -Path "$env:USERPROFILE\.gradle\caches\modules-2\files-2.1\com.h2database\h2" -Recurse -Filter "h2-*.jar" |
    Where-Object { $_.Name -notmatch "sources|javadoc" } |
    Sort-Object LastWriteTime -Descending |
    Select-Object -First 1

if (-not $h2Jar) {
    throw "Could not find H2 jar in Gradle cache. Run a Gradle build first so H2 is downloaded."
}

$schema = (Resolve-Path (Join-Path $repoRoot "src\main\resources\schema.sql")).Path.Replace("\", "/")
$authData = (Resolve-Path (Join-Path $repoRoot "src\main\resources\data\00-auth-demo.sql")).Path.Replace("\", "/")
$calendarData = (Resolve-Path (Join-Path $repoRoot "src\main\resources\data\01-demo-calendar.sql")).Path.Replace("\", "/")

$dbName = "cw2explain_" + [Guid]::NewGuid().ToString("N")
$dbPath = (Join-Path $PSScriptRoot $dbName).Replace("\", "/")
$url = "jdbc:h2:file:$dbPath;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE"

$commands = @(
    "RUNSCRIPT FROM '$schema'",
    "RUNSCRIPT FROM '$authData'",
    "RUNSCRIPT FROM '$calendarData'",
    "EXPLAIN SELECT id, user_id, date, title, time, completed FROM calendar_tasks WHERE user_id = (SELECT id FROM users WHERE username = 'demo') AND date = DATE '2026-01-20' ORDER BY time"
)

$output = New-Object System.Collections.Generic.List[string]

foreach ($command in $commands) {
    $output.Add("---- SQL ----")
    $output.Add($command)

    $result = & java -cp $h2Jar.FullName org.h2.tools.Shell -url $url -user sa -password "" -sql $command 2>&1
    foreach ($line in $result) {
        $output.Add([string]$line)
    }

    if ($LASTEXITCODE -ne 0) {
        $output | Out-File -FilePath $OutputPath -Encoding utf8
        throw "H2 command failed with exit code $LASTEXITCODE. See $OutputPath."
    }
}

$output | Out-File -FilePath $OutputPath -Encoding utf8

Get-ChildItem -Path $PSScriptRoot -Filter "$dbName*" |
    Remove-Item -Force -ErrorAction SilentlyContinue
