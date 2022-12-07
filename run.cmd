mvn compile package -f java/pom.xml
java -cp "java/processing4/core.jar;java/target/*" antfarm.Run