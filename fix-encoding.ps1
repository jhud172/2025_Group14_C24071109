# Fix corrupted UTF-8 characters in client-dashboard.html
$file = "src\main\resources\templates\dashboard\client-dashboard.html"
$content = Get-Content $file -Raw -Encoding UTF8

# Fix corrupted em dash (â€") to simple dash
$content = $content -replace [regex]::Escape("'You hit your plan â€" lock"), "'You hit your plan - lock"
$content = $content -replace [regex]::Escape("'You're on track â€" keep"), "'You are on track - keep"  
$content = $content -replace [regex]::Escape(" â€" today's"), " - today's"
$content = $content -replace [regex]::Escape("today â€" even"), "today - even"
$content = $content -replace [regex]::Escape("worked â€" I"), "worked - I"

# Fix corrupted emojis in expressions
$content = $content -replace [regex]::Escape("'â˜€ï¸'"), "'☀️'"
$content = $content -replace [regex]::Escape("'âš¡'"), "'⚡'"
$content = $content -replace [regex]::Escape("'ðŸŒ™'"), "'🌙'"
$content = $content -replace [regex]::Escape("'ðŸ†'"), "'🏆'"
$content = $content -replace [regex]::Escape("'ðŸŽ¯'"), "'🎯'"
$content = $content -replace [regex]::Escape("'ðŸŽ‰"), "'🎉"
$content = $content -replace [regex]::Escape("'ðŸ"¥"), "'🔥"
$content = $content -replace [regex]::Escape("'âœ¨"), "'✨'"

# Fix corrupted emojis in HTML content  
$content = $content -replace [regex]::Escape(">ðŸŽ¯<"), ">🎯<"
$content = $content -replace [regex]::Escape(">ðŸš€ Ahead<"), ">🚀 Ahead<"
$content = $content -replace [regex]::Escape(">âœ" On Track<"), ">✓ On Track<"
$content = $content -replace [regex]::Escape(">âš ï¸ Behind<"), ">⚠️ Behind<"
$content = $content -replace [regex]::Escape(">ðŸ'ª<"), ">💪<"
$content = $content -replace [regex]::Escape("around ðŸš€"), "around 🚀"

# Save with UTF-8 encoding
$content | Set-Content $file -Encoding UTF8 -NoNewline

Write-Host "Fixed character encoding issues in client-dashboard.html"
