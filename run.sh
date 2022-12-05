#!/bin/sh
mvn compile package shade:shade -f java/pom.xml
java -cp 'java/processing4/core.jar:java/target/netwants-1.0-SNAPSHOT.jar' antfarm.Run
