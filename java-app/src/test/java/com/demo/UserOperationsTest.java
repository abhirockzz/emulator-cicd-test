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

        client = new CosmosClientBuilder()
                .endpoint("https://localhost:8081/")
                .key("C2y6yDjf5/R+ob0N8A7Cgv30VRDJIWEHLM+4QDU5DE2nQ9nDuVTqobD4b8mGGyPMbIZnqyMsEcaGQy67XIw/Jw")
                .gatewayMode()
                .buildClient();

        System.out.println("databaseName === " + databaseName);
        System.out.println("containerName === " + containerName);

        try {
            client.createDatabaseIfNotExists(databaseName);
            database.createContainerIfNotExists(containerName, "/id");
        } catch (Exception e) {
            throw e;
        }

        this.database = client.getDatabase(databaseName);
        this.container = this.database.getContainer(containerName);
    }

    @AfterAll
    public void cleanup() {
        database.delete();
        client.close();
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