@echo off
echo ========================================
echo Compilation du projet Maven
echo ========================================
call mvn clean package

if %errorlevel% equ 0 (
    echo.
    echo [SUCCES] Build termin? avec succes!
    echo Fichier WAR g?n?r?: target\modelle-memoire.war
) else (
    echo.
    echo [ERREUR] La compilation a ?chou?
    pause
    exit /b %errorlevel%
)

echo.
echo ========================================
echo Pour d?ployer sur Tomcat:
echo Copier target\modelle-memoire.war vers Tomcat\webapps\
echo ========================================
pause
