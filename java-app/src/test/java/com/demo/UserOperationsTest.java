package com.demo;

import com.azure.cosmos.*;
import com.azure.cosmos.models.*;
import org.junit.jupiter.api.*;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserOperationsTest {

    private CosmosClient client;
    private CosmosDatabase database;
    private CosmosContainer container;

    @BeforeAll
    public void setup() {
        String connectionString = System.getenv("COSMOSDB_CONNECTION_STRING");
        String databaseName = System.getenv("COSMOSDB_DATABASE_NAME");
        String containerName = System.getenv("COSMOSDB_CONTAINER_NAME");

        String endpoint = connectionString.split(";")[0].split("=")[1];
        System.out.println("endpoint 1 === " + endpoint);

        endpoint = "https://" + endpoint.split("://")[1];
        System.out.println("endpoint 2 === " + endpoint);

        String key = connectionString.split(";")[1].split("=")[1];
        System.out.println("key === " + key);

        try {
            this.client = new CosmosClientBuilder()
                    .endpoint(endpoint)
                    .key(key)
                    .gatewayMode()
                    .buildClient();

        } catch (Exception e) {
            System.out.println("create client object failed: " + e);
            throw e;
        }

        CosmosDatabaseResponse createDBResponse = null;
        try {
            createDBResponse = client.createDatabaseIfNotExists(databaseName);
        } catch (Exception e) {
            System.out.println("create database failed: " + e.getMessage());
            throw e;
        }

        this.database = client.getDatabase(createDBResponse.getProperties().getId());
        System.out.println("created database: " + database.getId());

        CosmosContainerResponse createContainerResponse = null;
        try {
            createContainerResponse = database.createContainerIfNotExists(containerName, "/id");
        } catch (Exception e) {
            System.out.println("create container failed: " + e.getMessage());
            throw e;
        }

        this.container = this.database.getContainer(createContainerResponse.getProperties().getId());
        System.out.println("created container: " + container.getId());
    }

    @AfterAll
    public void cleanup() {
        this.database.delete();
        client.close();
        System.out.println("deleted database: " + this.database.getId());

    }

    @Test
    public void testCreateUser() {
        UserOperations.User user = new UserOperations.User(UUID.randomUUID().toString(), "user42@example.com", true);
        assertDoesNotThrow(() -> UserOperations.createUser(this.container, user));
    }

    @Test
    public void testReadUser() {
        String userId = UUID.randomUUID().toString();
        UserOperations.User user = new UserOperations.User(userId, "user43@example.com", true);
        assertDoesNotThrow(() -> UserOperations.createUser(this.container, user));

        UserOperations.User readUser = assertDoesNotThrow(() -> UserOperations.readUser(container, userId));
        assertEquals(userId, readUser.getId());
        assertEquals("user43@example.com", readUser.getEmail());
        assertTrue(readUser.isActive());
    }
}