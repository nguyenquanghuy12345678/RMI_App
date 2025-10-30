@echo off
chcp 65001 >nul
title RMI Bank Client

echo ========================================
echo   RMI BANK CLIENT
echo ========================================
echo.

REM Kiểm tra Java
java -version >nul 2>&1
if errorlevel 1 (
    echo LỖI: Không tìm thấy Java!
    echo Vui lòng cài đặt Java JDK và thêm vào PATH
    pause
    exit /b 1
)

echo [INFO] Đang khởi động Client UI...
echo.

REM Chạy Client (cần điều chỉnh classpath theo project của bạn)
cd bin
java --module-path . --module RMI_App/client.BankClientUI

pause
