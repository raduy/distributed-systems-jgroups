Add:
-Djava.net.preferIPv4Stack=true
to JVM params when chat cannot connect.

Install protobuf-compiler on Ubuntu:
1. Run: sudo apt-get install protobuf-compiler libprotobuf-dev


Generate classess:
1. Go to /src/main/resources
2. Run: protoc chat.proto --java_out=../../../src/main/java

1. Go to /src/main/java
2. Add required jars here
3. Run: javac -Xlint:deprecation -cp .:jgroups-3.6.2.Final.jar:protobuf-java-2.6.1.jar pl/edu/agh/dsrg/sr/chat/*/*.java
4. Run java -Djava.net.preferIPv4Stack=true -cp jgroups-3.6.2.Final.jar:protobuf-java-2.6.1.jar:. pl.edu.agh.dsrg.sr.chat.ChatApp
