package com.demo;

import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.models.*;
import com.azure.cosmos.util.CosmosPagedIterable;

public class Demo {
    // static final String EMULATOR_ENDPOINT = "https://localhost:8081";
    // static final String EMULATOR_KEY = "C2y6yDjf5/R+ob0N8A7Cgv30VRDJIWEHLM+4QDU5DE2nQ9nDuVTqobD4b8mGGyPMbIZnqyMsEcaGQy67XIw/Jw==";


    public static void main(String[] args) throws Exception {

        System.out.println("running cosmos db java sdk example...");

        String connString = System.getenv("COSMOSDB_CONNECTION_STRING");

        if (connString == null) {
            throw new Exception("COSMOSDB_CONNECTION_STRING env variable is not set");
        }

        System.out.println("connString === "+connString);


        String endpoint = null;
        String key = null;

        // Parse the connection string
        String[] parts = connString.split(";");
        for (String part : parts) {
            if (part.startsWith("AccountEndpoint=")) {
                endpoint = part.substring("AccountEndpoint=".length());
            } else if (part.startsWith("AccountKey=")) {
                key = part.substring("AccountKey=".length());
            }
        }

        System.out.println("endpoint === "+endpoint);
        System.out.println("key === "+key);


        CosmosClient client = null;

        try {
            client = new CosmosClientBuilder()
                    .endpoint(endpoint)
                    .key(key)
                    .gatewayMode() //this is important for M1 emulator. without it, this won't work
                    .buildClient();

        } catch (Exception e) {
            System.out.println("create client object failed: "+ e);
            throw e;
        }

        CosmosDatabaseResponse createDBResponse = null;
        try {
            createDBResponse = client.createDatabaseIfNotExists("demodb_javasdk1");
        } catch (Exception e) {
            System.out.println("create database failed: "+ e.getMessage());
            throw e;
        }

        CosmosDatabase database = client.getDatabase(createDBResponse.getProperties().getId());
        System.out.println("created database: "+ database.getId());

        CosmosContainerResponse createContainerResponse = null;
        try {
             createContainerResponse = database.createContainerIfNotExists("democontainer1", "/country");
        } catch (Exception e) {
            System.out.println("create container failed: "+ e.getMessage());
            throw e;
        }

        CosmosContainer container = database.getContainer(createContainerResponse.getProperties().getId());
        System.out.println("created container: "+ container.getId());

        CosmosItemResponse<User> item = null;
        try {
            item = container.createItem(new User("foo2@bar.com", "foo2", "india"));
            System.out.println("created item:: "+ item.getItem());

        } catch (Exception e) {
            System.out.println("create item failed: "+ e.getMessage());
            throw e;
        }

        String partitionKeyCountry = "india";
        CosmosPagedIterable<User> users = container.readAllItems(new PartitionKeyBuilder().add(partitionKeyCountry).build(), User.class);

        System.out.println("printing list of users...");

        for (User user: users) {
            System.out.println(user);
        }

    }

}
