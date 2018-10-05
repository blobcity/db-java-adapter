# db-java-adapter

Adapter used to perform operations on the [BlobCity Database](http://blobcity.com) through Java programs.

## Connecting to BlobCity Database
```java
Credentials.init("localhost:10111","root","root","datastore-name");
```
All on-premise deployments of BlobCity ship with a default super admin user with username `root` and password `root`. The adapter connects to the webservice hosted by a running database, that starts on port 10111. If you are running the Java program from the same machine as the database, you can use `localhost:10111` for connecting or else use the appropriate IP of the server on which the database can be connected to along with the public port number. 

More information on using the adapter can be found at: https://blobcity.github.io/db-java-adapter/

