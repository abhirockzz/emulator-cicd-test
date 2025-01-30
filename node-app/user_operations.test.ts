import { CosmosClient, Container, Database } from "@azure/cosmos";
import { createUser, readUser, User } from "./user_operations";
import assert from "assert";

const connectionString = process.env.COSMOSDB_CONNECTION_STRING;
const databaseId = process.env.COSMOSDB_DATABASE_NAME;
const containerId = process.env.COSMOSDB_CONTAINER_NAME;

// dummy comment
if (!connectionString) {
    throw new Error("Please set COSMOSDB_CONNECTION_STRING environment variable.");
}

const client = new CosmosClient(connectionString);
let database: Database;
let container: Container;

async function runTests() {
    const testUser: User = {
        id: "42",
        email: "user42@example.com",
        active: true
    };

    try {
        // Create database and container
        database = (await client.databases.createIfNotExists({ id: databaseId })).database;
        container = (await database.containers.createIfNotExists({ id: containerId, partitionKey: "/id" })).container;

        // Test createUser
        try {
            await createUser(container, testUser);
            console.log("User creation test passed!");
        } catch (error) {
            assert.fail("User creation test failed: " + error);
        }

        // Test readUser
        try {
            const user = await readUser(container, testUser.id);
            assert.equal(testUser.id, user.id)
            assert.equal(testUser.email, user.email)
            assert.equal(testUser.active, user.active)
            console.log("User read test passed!");

        } catch (error) {
            assert.fail("User read test failed: " + error);
        }

    } catch (error) {
        assert.fail("User read test failed: " + error);
    } finally {
        // Delete the database
        if (database) {
            await database.delete();
            console.log("database deleted");
        }
    }
}

runTests();