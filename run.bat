call mvn compile package shade:shade -f java/pom.xml
java -cp "java/processing4/core.jar;java/target/*" antfarm.Run