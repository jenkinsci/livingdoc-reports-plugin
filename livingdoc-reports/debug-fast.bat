SET JAVA_HOME=C:\java\jdks\Oracle_JDK7
SET MAVEN_HOME=C:\Tools\dev\apache-maven-3.3.3
SET M2_HOME=%MAVEN_HOME%
SET PATH=%JAVA_HOME%\bin;%M2_HOME%\bin

SET MAVEN_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=8000,suspend=n

rmdir /S /Q work\plugins

mvn clean hpi:run

