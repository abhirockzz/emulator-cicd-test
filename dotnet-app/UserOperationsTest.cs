using Microsoft.Azure.Cosmos;
using NUnit.Framework;

namespace dotnet_app.Tests
{
    [TestFixture]
    public class UserOperationsTest
    {
        private CosmosClient? client;
        private Database? database;
        private Container? container;

        [OneTimeSetUp]
        public async Task Setup()
        {
            string connectionString = Environment.GetEnvironmentVariable("COSMOSDB_CONNECTION_STRING") ?? throw new InvalidOperationException("COSMOSDB_CONNECTION_STRING environment variable is not set.");

            string databaseName = Environment.GetEnvironmentVariable("COSMOSDB_DATABASE_NAME") ?? throw new InvalidOperationException("COSMOSDB_DATABASE_NAME environment variable is not set.");

            string containerName = Environment.GetEnvironmentVariable("COSMOSDB_CONTAINER_NAME") ?? throw new InvalidOperationException("COSMOSDB_CONTAINER_NAME environment variable is not set.");

            connectionString = connectionString.Replace("http://", "https://");

            CosmosClientOptions options = new()
            {
                HttpClientFactory = () => new HttpClient(new HttpClientHandler()
                {
                    ServerCertificateCustomValidationCallback = HttpClientHandler.DangerousAcceptAnyServerCertificateValidator
                }),
                ConnectionMode = ConnectionMode.Gateway,
            };

            client = new CosmosClient(connectionString, options);
            database = await client.CreateDatabaseIfNotExistsAsync(databaseName);
            container = await database.CreateContainerIfNotExistsAsync(containerName, "/id");

            Console.WriteLine("Created database: " + database.Id);
            Console.WriteLine("Created container: " + container.Id);
        }

        [OneTimeTearDown]
        public async Task Cleanup()
        {
            if (database != null)
            {
                await database.DeleteAsync();
                Console.WriteLine("Deleted database: " + database.Id);
            }
            client?.Dispose();
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

            if (container == null)
            {
                throw new InvalidOperationException("Container is not initialized.");
            }
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

            if (container == null)
            {
                throw new InvalidOperationException("Container is not initialized.");
            }

            Assert.DoesNotThrowAsync(async () => await UserOperations.CreateUserAsync(container, user));

            User readUser = await UserOperations.ReadUserAsync(container, userId);
            Assert.AreEqual(userId, readUser.id);
            Assert.AreEqual("user43@example.com", readUser.email);
            Assert.IsTrue(readUser.active);
        }
    }
}