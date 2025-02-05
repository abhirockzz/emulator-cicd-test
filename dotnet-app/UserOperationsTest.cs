using Microsoft.Azure.Cosmos;
using NUnit.Framework;
using System;
using System.Threading.Tasks;

namespace dotnet_app.Tests
{
    [TestFixture]
    public class UserOperationsTest
    {
        private CosmosClient client;
        private Database database;
        private Container container;

        [OneTimeSetUp]
        public async Task Setup()
        {
            string connectionString = Environment.GetEnvironmentVariable("COSMOSDB_CONNECTION_STRING");
            string databaseName = Environment.GetEnvironmentVariable("COSMOSDB_DATABASE_NAME");
            string containerName = Environment.GetEnvironmentVariable("COSMOSDB_CONTAINER_NAME");

            Console.WriteLine("connectionString: " + connectionString);
            Console.WriteLine("databaseName: " + databaseName);
            Console.WriteLine("containerName: " + containerName);


            client = new CosmosClient(connectionString);
            database = await client.CreateDatabaseIfNotExistsAsync(databaseName);
            container = await database.CreateContainerIfNotExistsAsync(containerName, "/id");

            Console.WriteLine("Created database: " + database.Id);
            Console.WriteLine("Created container: " + container.Id);
        }

        [OneTimeTearDown]
        public async Task Cleanup()
        {
            await database.DeleteAsync();
            client.Dispose();
            Console.WriteLine("Deleted database: " + database.Id);
        }

        [Test]
        public async Task TestCreateUser()
        {
            User user = new()
            {
                id = Guid.NewGuid().ToString(),
                email = "user42@example.com",
                active = true
            };

            Assert.DoesNotThrowAsync(async () => await UserOperations.CreateUserAsync(container, user));
        }

        [Test]
        public async Task TestReadUser()
        {
            string userId = Guid.NewGuid().ToString();

            User user = new()
            {
                id = userId,
                email = "user43@example.com",
                active = true
            };

            await UserOperations.CreateUserAsync(container, user);

            User readUser = await UserOperations.ReadUserAsync(container, userId);
            Assert.AreEqual(userId, readUser.id);
            Assert.AreEqual("user43@example.com", readUser.email);
            Assert.IsTrue(readUser.active);
        }
    }
}