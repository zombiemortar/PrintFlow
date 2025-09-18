@echo off
setlocal ENABLEDELAYEDEXPANSION
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0coverage.ps1"
endlocal

