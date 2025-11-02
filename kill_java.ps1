# Script kill tất cả process Java RMI Server/Client

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  KILL Java RMI Processes" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Tìm tất cả process Java đang chạy
$javaProcesses = Get-Process -Name "java" -ErrorAction SilentlyContinue

if ($javaProcesses) {
    Write-Host "Tìm thấy $($javaProcesses.Count) Java process(es):" -ForegroundColor Yellow
    Write-Host ""
    
    foreach ($proc in $javaProcesses) {
        Write-Host "  PID: $($proc.Id) - $($proc.ProcessName)" -ForegroundColor Gray
    }
    
    Write-Host ""
    $confirm = Read-Host "Bạn có muốn KILL tất cả Java processes? (Y/N)"
    
    if ($confirm -eq 'Y' -or $confirm -eq 'y') {
        foreach ($proc in $javaProcesses) {
            try {
                Stop-Process -Id $proc.Id -Force
                Write-Host "  ✓ Đã kill PID: $($proc.Id)" -ForegroundColor Green
            } catch {
                Write-Host "  ✗ Không thể kill PID: $($proc.Id)" -ForegroundColor Red
            }
        }
        
        Write-Host ""
        Write-Host "✓ Đã kill xong tất cả Java processes!" -ForegroundColor Green
        Write-Host ""
        Write-Host "Bây giờ bạn có thể:" -ForegroundColor Yellow
        Write-Host "  1. Clean project trong Eclipse" -ForegroundColor White
        Write-Host "  2. Chạy lại BankServer.java" -ForegroundColor White
        Write-Host "  3. Chạy lại BankClientUI.java" -ForegroundColor White
        
    } else {
        Write-Host "Đã hủy." -ForegroundColor Yellow
    }
    
} else {
    Write-Host "✓ Không có Java process nào đang chạy." -ForegroundColor Green
    Write-Host ""
    Write-Host "Bạn có thể chạy Server và Client ngay bây giờ." -ForegroundColor Cyan
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Nhấn phím bất kỳ để đóng..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
