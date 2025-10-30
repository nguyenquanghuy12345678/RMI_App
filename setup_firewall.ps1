# Script PowerShell để cấu hình Firewall cho RMI Banking App
# Chạy với quyền Administrator

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Cấu hình Firewall cho RMI Banking App" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Kiểm tra quyền Administrator
$currentPrincipal = New-Object Security.Principal.WindowsPrincipal([Security.Principal.WindowsIdentity]::GetCurrent())
$isAdmin = $currentPrincipal.IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)

if (-not $isAdmin) {
    Write-Host "LỖI: Script này cần quyền Administrator!" -ForegroundColor Red
    Write-Host "Hãy click chuột phải vào PowerShell và chọn 'Run as Administrator'" -ForegroundColor Yellow
    pause
    exit
}

Write-Host "Đang cấu hình Firewall..." -ForegroundColor Green
Write-Host ""

# Mở port MySQL (3306)
try {
    Write-Host "1. Đang mở port 3306 (MySQL)..." -ForegroundColor Yellow
    
    # Xóa rule cũ nếu có
    Remove-NetFirewallRule -DisplayName "MySQL Server" -ErrorAction SilentlyContinue
    
    # Tạo rule mới
    New-NetFirewallRule -DisplayName "MySQL Server" `
                        -Direction Inbound `
                        -LocalPort 3306 `
                        -Protocol TCP `
                        -Action Allow `
                        -Profile Any `
                        -Description "Allow MySQL Database connections" | Out-Null
    
    Write-Host "   ✓ Đã mở port 3306 thành công" -ForegroundColor Green
} catch {
    Write-Host "   ✗ Lỗi khi mở port 3306: $_" -ForegroundColor Red
}

Write-Host ""

# Mở port RMI (1099)
try {
    Write-Host "2. Đang mở port 1099 (RMI)..." -ForegroundColor Yellow
    
    # Xóa rule cũ nếu có
    Remove-NetFirewallRule -DisplayName "RMI Server" -ErrorAction SilentlyContinue
    
    # Tạo rule mới
    New-NetFirewallRule -DisplayName "RMI Server" `
                        -Direction Inbound `
                        -LocalPort 1099 `
                        -Protocol TCP `
                        -Action Allow `
                        -Profile Any `
                        -Description "Allow RMI connections" | Out-Null
    
    Write-Host "   ✓ Đã mở port 1099 thành công" -ForegroundColor Green
} catch {
    Write-Host "   ✗ Lỗi khi mở port 1099: $_" -ForegroundColor Red
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Hoàn tất cấu hình Firewall!" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Hiển thị các rules đã tạo
Write-Host "Danh sách Firewall Rules đã tạo:" -ForegroundColor Yellow
Get-NetFirewallRule -DisplayName "MySQL Server" | Select-Object DisplayName, Enabled, Direction, Action | Format-Table
Get-NetFirewallRule -DisplayName "RMI Server" | Select-Object DisplayName, Enabled, Direction, Action | Format-Table

Write-Host "Nhấn phím bất kỳ để đóng..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
