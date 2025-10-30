# Script để kiểm tra kết nối MySQL và RMI

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Kiểm tra kết nối RMI Banking System" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Nhập thông tin
$db1Host = Read-Host "Nhập IP của Database Server 1 (Enter = localhost)"
if ([string]::IsNullOrWhiteSpace($db1Host)) { $db1Host = "localhost" }

$db2Host = Read-Host "Nhập IP của Database Server 2 (Enter = localhost)"
if ([string]::IsNullOrWhiteSpace($db2Host)) { $db2Host = "localhost" }

$rmiHost = Read-Host "Nhập IP của RMI Server (Enter = localhost)"
if ([string]::IsNullOrWhiteSpace($rmiHost)) { $rmiHost = "localhost" }

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Bắt đầu kiểm tra..." -ForegroundColor Yellow
Write-Host ""

# Kiểm tra kết nối MySQL Database 1
Write-Host "1. Kiểm tra MySQL Database Server 1 ($db1Host:3306)..." -ForegroundColor Yellow
$db1Result = Test-NetConnection -ComputerName $db1Host -Port 3306 -WarningAction SilentlyContinue

if ($db1Result.TcpTestSucceeded) {
    Write-Host "   ✓ Kết nối Database 1 thành công!" -ForegroundColor Green
} else {
    Write-Host "   ✗ KHÔNG thể kết nối Database 1!" -ForegroundColor Red
    Write-Host "     - Kiểm tra MySQL đã chạy chưa" -ForegroundColor Yellow
    Write-Host "     - Kiểm tra Firewall đã mở port 3306 chưa" -ForegroundColor Yellow
}

Write-Host ""

# Kiểm tra kết nối MySQL Database 2
Write-Host "2. Kiểm tra MySQL Database Server 2 ($db2Host:3306)..." -ForegroundColor Yellow
$db2Result = Test-NetConnection -ComputerName $db2Host -Port 3306 -WarningAction SilentlyContinue

if ($db2Result.TcpTestSucceeded) {
    Write-Host "   ✓ Kết nối Database 2 thành công!" -ForegroundColor Green
} else {
    Write-Host "   ✗ KHÔNG thể kết nối Database 2!" -ForegroundColor Red
    Write-Host "     - Kiểm tra MySQL đã chạy chưa" -ForegroundColor Yellow
    Write-Host "     - Kiểm tra Firewall đã mở port 3306 chưa" -ForegroundColor Yellow
}

Write-Host ""

# Kiểm tra kết nối RMI Server
Write-Host "3. Kiểm tra RMI Server ($rmiHost:1099)..." -ForegroundColor Yellow
$rmiResult = Test-NetConnection -ComputerName $rmiHost -Port 1099 -WarningAction SilentlyContinue

if ($rmiResult.TcpTestSucceeded) {
    Write-Host "   ✓ Kết nối RMI Server thành công!" -ForegroundColor Green
} else {
    Write-Host "   ✗ KHÔNG thể kết nối RMI Server!" -ForegroundColor Red
    Write-Host "     - Kiểm tra RMI Server đã chạy chưa" -ForegroundColor Yellow
    Write-Host "     - Kiểm tra Firewall đã mở port 1099 chưa" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Tổng kết:" -ForegroundColor Cyan

$totalTests = 3
$passedTests = 0

if ($db1Result.TcpTestSucceeded) { $passedTests++ }
if ($db2Result.TcpTestSucceeded) { $passedTests++ }
if ($rmiResult.TcpTestSucceeded) { $passedTests++ }

Write-Host "Đã vượt qua: $passedTests/$totalTests tests" -ForegroundColor $(if ($passedTests -eq $totalTests) { "Green" } else { "Yellow" })

if ($passedTests -eq $totalTests) {
    Write-Host "✓ Hệ thống sẵn sàng hoạt động!" -ForegroundColor Green
} else {
    Write-Host "⚠ Một số thành phần chưa sẵn sàng. Vui lòng kiểm tra lại." -ForegroundColor Yellow
}

Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Nhấn phím bất kỳ để đóng..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
