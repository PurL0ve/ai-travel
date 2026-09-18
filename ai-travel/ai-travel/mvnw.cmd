@rem
@rem Copyright 2001-present The Apache Software Foundation
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem     https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem

@if "%DEBUG%"=="" @echo off
@rem ##########################################################################
@rem  Maven2 Start Up Batch script
@rem
@rem  Required ENV vars:
@rem  JAVA_HOME - must point at your Java Development Kit installation
@rem
@rem  Optional ENV vars
@rem  MAVEN_BASEDIR - prefix for wrapper script location
@rem  MAVEN_OPTS - parameters passed to the Java VM when running Maven
@rem     e.g. to debug Maven itself, use
@rem     set MAVEN_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000
@rem  MAVEN_SKIP_RC - flag to disable loading of mavenrc files
@rem  MAVEN_CONFIG - user-specific location of mavenrc file
@rem ##########################################################################

@rem Decide how to start up Maven

setlocal

set "MAVEN_SKIP_RC=false"

@rem Find the project base dir, i.e. the directory that contains the folder ".mvn"
@rem Note that there may be other folders named ".mvn" in the filesystem,
@rem but this one is special.
@rem Modify this logic to suite your needs.
set "MAVEN_PROJECTBASEDIR=%MAVEN_BASEDIR%"
if NOT "%MAVEN_PROJECTBASEDIR%"=="" goto endDetectBaseDir

set "EXEC_DIR=%CD%"
set "WD=%EXEC_DIR%"
:findBaseDir
if EXIST "%WD%\.mvn" goto endIterateBaseDir
cd ..
set "WD=%CD%"
if "%WD%"=="%EXEC_DIR%" goto exIterateBaseDir
goto findBaseDir
:exIterateBaseDir
set "MAVEN_PROJECTBASEDIR=%EXEC_DIR%"
cd "%EXEC_DIR%"
goto endDetectBaseDir

:endIterateBaseDir
set "MAVEN_PROJECTBASEDIR=%WD%"
cd "%WD%"

:endDetectBaseDir

SETLOCAL

set "_MVNCONFIGDIR=%USERPROFILE%\.m2"
if NOT "%MAVEN_CONFIG%"=="" set "_MVNCONFIGDIR=%MAVEN_CONFIG%"

set "MVN_CMD=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar"
set "MVN_MAIN_CLASS=org.apache.maven.wrapper.MavenWrapperMain"

if EXIST "%MVN_CMD%" goto init

echo.
echo ERROR: Maven wrapper not found.
echo.
echo This script should be run from within a Maven project, where a .mvn directory
echo containing a wrapper JAR exists.
echo.
echo Please check your project structure and ensure the Maven wrapper is properly set up.

goto end

:init
set "JAVACMD=%JAVA_HOME%\bin\java.exe"

@rem Check if Java is installed
if NOT "%JAVA_HOME%"=="" goto checkJavaExists
set "JAVACMD=java.exe"

:checkJavaExists
"%JAVACMD%" -version >NUL 2>&1
if %ERRORLEVEL% EQU 0 goto executeWithJava

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto end

:executeWithJava
set "MAVEN_OPTS=%MAVEN_OPTS:-Xmx1g -XX:MaxPermSize=256m%"
set "CLASSPATH=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar"

%JAVACMD% %MAVEN_OPTS% -classpath "%CLASSPATH%" %MVN_MAIN_CLASS% %*

:end
ENDLOCAL

:end
exit /b %ERRORLEVEL%
