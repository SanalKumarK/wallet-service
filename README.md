WALLET MICROSERVICE
Build a simple wallet microservice running on the JVM that manages credit/debit
transactions on behalf of players.

Spring Boot 2.2.6.RELEASE is used to design the REST API.

Database used is in memory db h2.
Used following model to design the schema for wallet-service.
//TODO insert object model image.

Java Docs are available in the wallet-service/doc/index.html. 

To run the service please follow the below steps.
1) Clone the repository from the following link.
//Add clone command here.
2) Navigate to the wallet-service/target folder
3) Open a terminal, and issue the following command.
    a) java -jar wallet-0.0.1.SHANSHOT.jar
    b) Wait untill the services are up and running.

To test the service,     
1) To create a player, issue the following POST request with respective data.

    curl --header "Content-Type: application/json" \
    --request POST \
    --data '{ "playerId": "alice.bob",
          	  "firstName": "Alice",
              "lastName": "Bob",
              "age": 30,
              "status": "ACTIVE",
              "account": {
              	"accountNumber": "1234567890123456",
              	"balance": "1000",
              	"currency":"USD"
              }}'\
    localhost:8080/player   

2) To get the details of a player, 
    curl localhost:8080/player?id={playerID}
    eg: curl localhost:8080/player?id=alice.bob

3) To check the balance of a player,
    curl localhost:8080/wallet/balance?id={playerID}
    eg: curl localhost:8080/wallet/balance?id=alice.bob
 
4) To save a transaction to wallet service, 
     curl --header "Content-Type: application/json" \
         --request POST \
         --data '{ "transactionId":"1",
                 	"type": "CREDIT",
                 	"accountNumber":"123456789",
                    "date": "2020-05-02T13:40:00.343+0000",
                    "amount": 300 }'\
         localhost:8080/wallet/transaction         

5) To get the history of transactions for a player,
    curl localhost:8080/wallet/transaction?id={playerId}&start={startTime}&end={endTime} 
    eg: curl localhost:8080/wallet/transaction?id=sanal.kumar&start=2020-05-02T13:40:00Z&end=2020-05-05T21:41:54Z
              
Please use the attached post man collection for query the services defined in wallet-service.

Please run the "Test Plan.jmx" using Apache Jmeter for running the performance test on the wallet-service.