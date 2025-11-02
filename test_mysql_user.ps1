# Script kiểm tra MySQL user rmiuser1

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Kiểm tra MySQL User: rmiuser1" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$mysqlPath = "C:\xampp\mysql\bin\mysql.exe"

# Kiểm tra MySQL có tồn tại không
if (-not (Test-Path $mysqlPath)) {
    Write-Host "❌ KHÔNG tìm thấy MySQL!" -ForegroundColor Red
    Write-Host "   Đường dẫn: $mysqlPath" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Vui lòng kiểm tra lại đường dẫn XAMPP của bạn." -ForegroundColor Yellow
    Write-Host "Hoặc sửa biến `$mysqlPath trong script này." -ForegroundColor Yellow
    pause
    exit
}

Write-Host "✓ Đã tìm thấy MySQL: $mysqlPath" -ForegroundColor Green
Write-Host ""

# Test 1: Kết nối với rmiuser1@localhost
Write-Host "Test 1: Kết nối rmiuser1@localhost..." -ForegroundColor Yellow

$testQuery = "SELECT 'OK' as status, USER() as current_user, DATABASE() as db;"

$result = & $mysqlPath -h localhost -u rmiuser1 -prmi1 -e $testQuery 2>&1

if ($LASTEXITCODE -eq 0) {
    Write-Host "   ✓ Kết nối THÀNH CÔNG!" -ForegroundColor Green
    Write-Host "   User: rmiuser1@localhost" -ForegroundColor Green
    Write-Host ""
} else {
    Write-Host "   ✗ Kết nối THẤT BẠI!" -ForegroundColor Red
    Write-Host "   Lỗi: $result" -ForegroundColor Red
    Write-Host ""
    Write-Host "   Cần tạo user. Chạy lệnh:" -ForegroundColor Yellow
    Write-Host "   mysql -u root -p < FIX_DATABASE_USER.sql" -ForegroundColor Cyan
    Write-Host ""
}

# Test 2: Kết nối với database bank_db
Write-Host "Test 2: Kết nối database bank_db..." -ForegroundColor Yellow

$testQuery2 = "USE bank_db; SELECT COUNT(*) as total_accounts FROM accounts;"

$result2 = & $mysqlPath -h localhost -u rmiuser1 -prmi1 -e $testQuery2 2>&1

if ($LASTEXITCODE -eq 0) {
    Write-Host "   ✓ Truy cập database THÀNH CÔNG!" -ForegroundColor Green
    Write-Host "   Database: bank_db" -ForegroundColor Green
    Write-Host ""
} else {
    Write-Host "   ✗ KHÔNG thể truy cập database!" -ForegroundColor Red
    Write-Host "   Lỗi: $result2" -ForegroundColor Red
    Write-Host ""
    Write-Host "   Cần cấp quyền. Chạy lệnh:" -ForegroundColor Yellow
    Write-Host "   GRANT ALL PRIVILEGES ON bank_db.* TO 'rmiuser1'@'localhost';" -ForegroundColor Cyan
    Write-Host ""
}

# Test 3: Kiểm tra quyền
Write-Host "Test 3: Kiểm tra quyền user..." -ForegroundColor Yellow

$testQuery3 = "SHOW GRANTS FOR 'rmiuser1'@'localhost';"

$result3 = & $mysqlPath -h localhost -u root -e $testQuery3 2>&1

if ($LASTEXITCODE -eq 0) {
    Write-Host "   ✓ Quyền của user rmiuser1:" -ForegroundColor Green
    Write-Host $result3 -ForegroundColor Gray
    Write-Host ""
} else {
    Write-Host "   ⚠ Không thể kiểm tra quyền" -ForegroundColor Yellow
    Write-Host ""
}

# Test 4: Danh sách tài khoản
Write-Host "Test 4: Lấy danh sách tài khoản..." -ForegroundColor Yellow

$testQuery4 = "SELECT account_number, account_name, balance FROM bank_db.accounts LIMIT 5;"

$result4 = & $mysqlPath -h localhost -u rmiuser1 -prmi1 -e $testQuery4 2>&1

if ($LASTEXITCODE -eq 0) {
    Write-Host "   ✓ Dữ liệu tài khoản:" -ForegroundColor Green
    Write-Host $result4 -ForegroundColor Gray
    Write-Host ""
} else {
    Write-Host "   ✗ Không thể lấy dữ liệu" -ForegroundColor Red
    Write-Host "   Lỗi: $result4" -ForegroundColor Red
    Write-Host ""
}

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Kết quả kiểm tra" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

$allTestsPassed = $true

if ($LASTEXITCODE -ne 0) {
    $allTestsPassed = $false
}

if ($allTestsPassed) {
    Write-Host "✓ TẤT CẢ TESTS ĐỀU PASS!" -ForegroundColor Green
    Write-Host "  User rmiuser1 đã sẵn sàng!" -ForegroundColor Green
    Write-Host ""
    Write-Host "  Bạn có thể chạy RMI Server và Client ngay bây giờ." -ForegroundColor Cyan
} else {
    Write-Host "✗ MỘT SỐ TESTS THẤT BẠI" -ForegroundColor Red
    Write-Host ""
    Write-Host "  Hành động cần làm:" -ForegroundColor Yellow
    Write-Host "  1. Mở XAMPP Shell" -ForegroundColor White
    Write-Host "  2. Chạy: mysql -u root -p" -ForegroundColor White
    Write-Host "  3. Chạy: source D:/eclipse-workspace/RMI_App/FIX_DATABASE_USER.sql" -ForegroundColor White
    Write-Host ""
    Write-Host "  Hoặc đọc file: FIX_ACCESS_DENIED.md" -ForegroundColor Cyan
}

Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Nhấn phím bất kỳ để đóng..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
