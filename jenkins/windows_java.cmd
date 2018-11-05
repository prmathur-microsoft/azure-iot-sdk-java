@REM Copyright (c) Microsoft. All rights reserved.
@REM Licensed under the MIT license. See LICENSE file in the project root for full license information.

setlocal

set build-root=%~dp0..
rem // resolve to fully qualified path
for %%i in ("%build-root%") do set build-root=%%~fi

REM --Java SDK Run E2E  --
cd %build-root%
call mvn install -DskipITs=false -T 2C -Dfailsafe.rerunFailingTestsCount=2
if errorlevel 1 goto :eof
cd %build-root%

