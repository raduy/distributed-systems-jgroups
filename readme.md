Add:
-Djava.net.preferIPv4Stack=true
to JVM params when chat cannot connect.

Install protobuf-compiler on Ubuntu:
1. Run: sudo apt-get install protobuf-compiler libprotobuf-dev


Generate classess:
1. Go to /src/main/resources
2. Run: protoc chat.proto --java_out=../../../src/main/java