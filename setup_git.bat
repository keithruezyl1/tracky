@echo off
git init
git branch -M main
git remote add origin https://github.com/keithruezyl1/tracky
git add .
git commit -m "Initial commit of Tracky codebase"
git push -u origin main
