#!/bin/sh
# Maven Wrapper startup script

# Locate the maven-wrapper.jar
MAVEN_WRAPPER_JAR=".mvn/wrapper/maven-wrapper.jar"

if [ ! -f "$MAVEN_WRAPPER_JAR" ]; then
    echo "Maven wrapper JAR not found: $MAVEN_WRAPPER_JAR"
    echo "Please download it from Maven Central"
    exit 1
fi

# Find Java
if [ -n "$JAVA_HOME" ]; then
    JAVA="$JAVA_HOME/bin/java"
else
    JAVA="java"
fi

# Execute the wrapper
exec "$JAVA" \
    -Dmaven.multiModuleProjectDirectory="$MAVEN_PROJECTBASEDIR" \
    -jar "$MAVEN_PROJECTBASEDIR/$MAVEN_WRAPPER_JAR" \
    "$@"
