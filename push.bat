@echo off
echo ==========================================
echo Pushing Traffic Simulator to GitHub
echo ==========================================
echo.

echo [1/5] Initializing Git repository...
git init

echo [2/5] Adding files to staging...
git add .

echo [3/5] Committing files...
git commit -m "Initial commit: Traffic Junction Monitor codebase and file structure"

echo [4/5] Setting remote origin...
git remote add origin https://github.com/jc-kirthi/Traffic-simulator.git
git branch -M main

echo [5/5] Pushing to main branch...
git push -u origin main

echo.
echo ==========================================
echo Process completed!
echo ==========================================
pause
