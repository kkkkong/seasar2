@echo off
rem Usage: build-runner.bat <JDK_HOME> <module_dir> [maven_goals] [extra_args]
set JDK_HOME=%~1
set MODULE_DIR=%~2
set GOALS=%~3
set EXTRA_ARGS=%~4

if "%GOALS%"=="" set GOALS=clean install

set "JAVA_HOME=%JDK_HOME%"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo ============================================
echo JDK_HOME=%JAVA_HOME%
echo MODULE_DIR=%MODULE_DIR%
echo GOALS=%GOALS%
echo EXTRA_ARGS=%EXTRA_ARGS%
echo ============================================
"%JAVA_HOME%\bin\java.exe" -version 2>&1
echo --------------------------------------------
call mvn -version 2>&1
echo --------------------------------------------

cd /d "%MODULE_DIR%"
if "%EXTRA_ARGS%"=="" (
  call mvn %GOALS%
) else (
  call mvn %GOALS% %EXTRA_ARGS%
)
set EXIT_CODE=%ERRORLEVEL%
echo ============================================
echo EXIT_CODE=%EXIT_CODE%
exit /b %EXIT_CODE%
