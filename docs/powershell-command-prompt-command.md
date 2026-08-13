# PowerShell & Command Prompt — Common Commands

Quick reference for common Windows terminal tasks, with equivalents for both **PowerShell** and **Command Prompt (cmd.exe)**.

## Find process running on a port

**PowerShell**
```powershell
Get-Process -Id (Get-NetTCPConnection -LocalPort 8080).OwningProcess
# or simply
netstat -ano | findstr :8080
```

**Command Prompt**
```cmd
netstat -ano | findstr :8080
```
The last column in the output is the PID. Look it up with:
```cmd
tasklist /FI "PID eq 1234"
```

## Kill a process by port

**PowerShell**
```powershell
Get-Process -Id (Get-NetTCPConnection -LocalPort 8080).OwningProcess | Stop-Process -Force
# or by PID directly
Stop-Process -Id 1234 -Force
```

**Command Prompt**
```cmd
for /f "tokens=5" %a in ('netstat -ano ^| findstr :8080') do taskkill /PID %a /F
:: or by PID directly
taskkill /PID 1234 /F
```

## Kill a process by name

**PowerShell**
```powershell
Stop-Process -Name "node" -Force
# or
Get-Process node | Stop-Process -Force
```

**Command Prompt**
```cmd
taskkill /IM node.exe /F
```

## List all listening ports

**PowerShell**
```powershell
Get-NetTCPConnection -State Listen | Sort-Object LocalPort
```

**Command Prompt**
```cmd
netstat -an | findstr LISTENING
```

## Get command history

**PowerShell**
```powershell
Get-History
# or view the saved history file for the current session (PSReadLine)
Get-Content (Get-PSReadLineOption).HistorySavePath
```

**Command Prompt**
```cmd
doskey /history
```

## Clear command history

**PowerShell**
```powershell
Clear-History
# also clear the persisted PSReadLine history file
Remove-Item (Get-PSReadLineOption).HistorySavePath -Force
```

**Command Prompt**
```cmd
:: cmd history is session-only (in-memory), just close the window,
:: or clear the visible screen buffer with:
cls
```

## Clear terminal screen

**PowerShell**
```powershell
Clear-Host
# alias
cls
```

**Command Prompt**
```cmd
cls
```

## Check IP configuration

**PowerShell**
```powershell
Get-NetIPAddress
ipconfig /all
```

**Command Prompt**
```cmd
ipconfig /all
```

## Flush DNS cache

**PowerShell**
```powershell
Clear-DnsClientCache
```

**Command Prompt**
```cmd
ipconfig /flushdns
```

## List running processes

**PowerShell**
```powershell
Get-Process
Get-Process | Sort-Object CPU -Descending | Select-Object -First 10
```

**Command Prompt**
```cmd
tasklist
```

## Check disk usage

**PowerShell**
```powershell
Get-PSDrive -PSProvider FileSystem
Get-Volume
```

**Command Prompt**
```cmd
wmic logicaldisk get size,freespace,caption
```

## Find a file by name

**PowerShell**
```powershell
Get-ChildItem -Path C:\ -Filter "*.log" -Recurse -ErrorAction SilentlyContinue
```

**Command Prompt**
```cmd
dir C:\*.log /s /b
```

## Search file contents (grep equivalent)

**PowerShell**
```powershell
Select-String -Path ".\*.log" -Pattern "ERROR"
```

**Command Prompt**
```cmd
findstr /S /I "ERROR" *.log
```

## Environment variables

**PowerShell**
```powershell
Get-ChildItem Env:
$env:PATH
[Environment]::SetEnvironmentVariable("MY_VAR", "value", "User")
```

**Command Prompt**
```cmd
set
echo %PATH%
setx MY_VAR "value"
```

## Check listening/used ports with owning process name directly

**PowerShell**
```powershell
Get-NetTCPConnection -State Listen |
  Select-Object LocalAddress, LocalPort, OwningProcess,
    @{Name="ProcessName";Expression={(Get-Process -Id $_.OwningProcess).ProcessName}} |
  Sort-Object LocalPort
```

## Restart a service

**PowerShell**
```powershell
Restart-Service -Name "Spooler" -Force
Get-Service -Name "Spooler"
```

**Command Prompt**
```cmd
net stop spooler
net start spooler
```

## Check who is using a file/folder (locked file)

**PowerShell**
```powershell
# Requires handle.exe (Sysinternals) on both shells
handle.exe "C:\path\to\file"
```

## Copy path of current directory

**PowerShell**
```powershell
(Get-Location).Path | Set-Clipboard
```

**Command Prompt**
```cmd
cd | clip
```

## Notes
- Run terminal **as Administrator** for commands like `taskkill /F` on system processes, `Get-NetTCPConnection`, service restarts, or `ipconfig /flushdns`.
- `Get-NetTCPConnection` is PowerShell-only (Windows 8/Server 2012+); `netstat` works in both shells and is the most portable option.
- PowerShell can run any Command Prompt command as well (e.g., `netstat`, `tasklist`, `taskkill`), but not vice versa — cmd.exe cannot run PowerShell cmdlets like `Get-Process`.
