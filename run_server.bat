@echo off
chcp 65001 >nul
title RMI Bank Server

echo ========================================
echo   RMI BANK SERVER
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

echo [INFO] Đang khởi động RMI Server...
echo [INFO] Server sẽ lắng nghe trên port 1099
echo.

REM Chạy Server (cần điều chỉnh classpath theo project của bạn)
cd bin
java --module-path . --module RMI_App/server.BankServer

pause
