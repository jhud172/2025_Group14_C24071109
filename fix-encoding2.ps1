# Fix corrupted UTF-8 characters - using byte pattern matching
$file = "src\main\resources\templates\dashboard\client-dashboard.html"
$bytes = [System.IO.File]::ReadAllBytes($file)
$content = [System.Text.Encoding]::UTF8.GetString($bytes)

# Replace corrupted characters using regex patterns
# Replace corrupted em-dash bytes (â€") with simple dash (-)
$content = $content -creplace "plan\s+Ã¢â‚¬â€\s+lock", "plan - lock"
$content = $content -creplace "track\s+Ã¢â‚¬â€\s+keep", "track - keep"
$content = $content -creplace "\s+Ã¢â‚¬â€\s+today", " - today"
$content = $content -creplace "today\s+Ã¢â‚¬â€\s+even", "today - even"
$content = $content -creplace "worked\s+Ã¢â‚¬â€\s+I", "worked - I"

# For emojis, replace with HTML entities or simpler alternatives
$content = $content -replace "Ã°Å¸â€\s*'", "TARGET_EMOJI'"
$content = $content -replace "Ã°Å¸Å½Â¯", "TARGET_EMOJI"
$content = $content -replace "Ã°Å¸†", "TROPHY_EMOJI"
$content = $content -replace "Ã¢Å¡Â¡", "LIGHTNING_EMOJI"
$content = $content -replace "Ã°Å¸Å'â„¢", "MOON_EMOJI"
$content = $content -replace "Â˜€ï¸", "SUN_EMOJI"
$content = $content -replace "Ã°Å¸Ž‰", "PARTY_EMOJI"
$content = $content -replace "Ã°Å¸"Â¥", "FIRE_EMOJI"
$content = $content -replace "Ã¢Å"¨", "SPARKLE_EMOJI"
$content = $content -replace "Ã°Å¸Å¡â€ž", "ROCKET_EMOJI"
$content = $content -replace "Ã°Å¸'ª", "MUSCLE_EMOJI"
$content = $content -replace "Â âš ï¸", "WARNING_EMOJI"
$content = $content -replace "Ã¢Å"", "CHECKMARK_EMOJI"

# Now replace placeholders with actual characters
$content = $content -replace "TARGET_EMOJI", [char]::ConvertFromUtf32(0x1F3AF)
$content = $content -replace "TROPHY_EMOJI", [char]::ConvertFromUtf32(0x1F3C6)
$content = $content -replace "LIGHTNING_EMOJI", [char]::ConvertFromUtf32(0x26A1)
$content = $content -replace "MOON_EMOJI", [char]::ConvertFromUtf32(0x1F319)
$content = $content -replace "SUN_EMOJI", [char]::ConvertFromUtf32(0x2600) + [char]::ConvertFromUtf32(0xFE0F)
$content = $content -replace "PARTY_EMOJI", [char]::ConvertFromUtf32(0x1F389)
$content = $content -replace "FIRE_EMOJI", [char]::ConvertFromUtf32(0x1F525)
$content = $content -replace "SPARKLE_EMOJI", [char]::ConvertFromUtf32(0x2728)
$content = $content -replace "ROCKET_EMOJI", [char]::ConvertFromUtf32(0x1F680)
$content = $content -replace "MUSCLE_EMOJI", [char]::ConvertFromUtf32(0x1F4AA)
$content = $content -replace "WARNING_EMOJI", [char]::ConvertFromUtf32(0x26A0) + [char]::ConvertFromUtf32(0xFE0F)
$content = $content -replace "CHECKMARK_EMOJI", [char]::ConvertFromUtf32(0x2713)

# Save with UTF-8 BOM to ensure proper handling
[System.IO.File]::WriteAllText($file, $content, [System.Text.UTF8Encoding]::new($true))

Write-Host "Fixed character encoding issues"
