import os
import pytest
from azure.cosmos import CosmosClient, PartitionKey
from user_operations import create_user, read_user, User

@pytest.fixture(scope="module")
def cosmos_container():
    connection_string = os.getenv("COSMOSDB_CONNECTION_STRING")
    database_id = os.getenv("COSMOSDB_DATABASE_NAME")
    container_id = os.getenv("COSMOSDB_CONTAINER_NAME")

    if not connection_string:
        raise ValueError("Please set COSMOSDB_CONNECTION_STRING environment variable.")

    client = CosmosClient.from_connection_string(connection_string)
    database = client.create_database_if_not_exists(id=database_id)
    container = database.create_container_if_not_exists(
        id=container_id,
        partition_key=PartitionKey(path="/id")
    )

    yield container

    client.delete_database(database.id)
    print("Database deleted")

def test_create_user(cosmos_container):
    test_user = User(id="42", email="user42@example.com", active=True)
    
    # Verify that create_user does not raise an exception
    try:
        create_user(cosmos_container, test_user)
        print("User creation test complete")
    except Exception as e:
        pytest.fail(f"create_user raised an exception: {e}")

def test_read_user(cosmos_container):

    test_user_id = "43"
    test_user = User(id=test_user_id, email="user43@example.com", active=True)
    
    try:
        create_user(cosmos_container, test_user)
        print("User creation test complete")
    except Exception as e:
        pytest.fail(f"create_user raised an exception: {e}")

    user = read_user(cosmos_container, test_user_id)
    
    assert test_user_id == user.id
    assert "user43@example.com" == user.email
    assert user.active
    
    print("User read test complete")