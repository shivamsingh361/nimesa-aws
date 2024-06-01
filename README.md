## Pre-requisits:
java 17, postman

## Steps:
1. Clone repo
2. Add AWS clientID and clientSecretId in application.properties
3. mvn clean install
4. start the server (port:8080, make sure no other application is running on this port else change port)
5. import `Nimesa Tech.postman_collection.json` file in postman as collection
6. execute each endpoint, in case of UUID responses, user it in next request ..
