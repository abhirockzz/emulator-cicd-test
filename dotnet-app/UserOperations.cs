using Microsoft.Azure.Cosmos;

namespace dotnet_app
{
    public class User
    {
        public string? id { get; set; }
        public string? email { get; set; }
        public bool? active { get; set; }


        public override string ToString()
        {
            return $"User{{id='{id}', email='{email}', active={active}}}";
        }
    }

    public static class UserOperations
    {
        public static async Task CreateUserAsync(Container container, User user)
        {
            try
            {
                ItemResponse<User> response = await container.CreateItemAsync(user, new PartitionKey(user.id));
                Console.WriteLine("Created user: " + response.Resource);
            }
            catch (Exception e)
            {
                Console.WriteLine("Error creating user: " + e.Message);
                throw;
            }
        }

        public static async Task<User> ReadUserAsync(Container container, string userId)
        {
            try
            {
                ItemResponse<User> response = await container.ReadItemAsync<User>(userId, new PartitionKey(userId));
                return response.Resource;
            }
            catch (Exception e)
            {
                Console.WriteLine("Error reading user: " + e.Message);
                throw;
            }
        }
    }
}