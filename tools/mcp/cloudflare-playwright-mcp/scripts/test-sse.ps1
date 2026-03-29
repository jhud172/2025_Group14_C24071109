param(
    [string]$BaseUrl = "https://one-to-one-playwright-mcp.jhud172.workers.dev",
    [string]$SessionId = "codex-test-001"
)

$ErrorActionPreference = "Stop"

$outputFile = Join-Path $PSScriptRoot "sse-output.txt"
if (Test-Path $outputFile) {
    Remove-Item $outputFile -Force
}

$sseUrl = "$BaseUrl/sse?sessionId=$SessionId"
$messageUrl = "$BaseUrl/sse/message?sessionId=$SessionId"

$process = Start-Process -FilePath "curl.exe" `
    -ArgumentList "-k", "-N", $sseUrl `
    -RedirectStandardOutput $outputFile `
    -PassThru `
    -WindowStyle Hidden

try {
    Start-Sleep -Seconds 5

    $initialize = '{"jsonrpc":"2.0","id":1,"method":"initialize","params":{"protocolVersion":"2024-11-05","capabilities":{},"clientInfo":{"name":"powershell","version":"1.0"}}}'
    Invoke-WebRequest -Uri $messageUrl -Method Post -Body $initialize -ContentType "application/json" -UseBasicParsing | Out-Null

    Start-Sleep -Seconds 2

    $toolsList = '{"jsonrpc":"2.0","id":2,"method":"tools/list","params":{}}'
    Invoke-WebRequest -Uri $messageUrl -Method Post -Body $toolsList -ContentType "application/json" -UseBasicParsing | Out-Null

    Start-Sleep -Seconds 5
}
finally {
    if ($process -and !$process.HasExited) {
        Stop-Process -Id $process.Id -Force
    }
}

Get-Content $outputFile
