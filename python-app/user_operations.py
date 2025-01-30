from azure.cosmos import ContainerProxy

class User:
    def __init__(self, id: str, email: str, active: bool):
        self.id = id
        self.email = email
        self.active = active

    def to_dict(self):
        return {
            "id": self.id,
            "email": self.email,
            "active": self.active
        }

def create_user(container: ContainerProxy, user: User):
    try:
        container.create_item(body=user.to_dict())
        print("Created user:", user.to_dict())
    except Exception as e:
        print("Error creating user", e)
        raise e

def read_user(container: ContainerProxy, user_id: str) -> User:
    try:
        item_response = container.read_item(item=user_id, partition_key=user_id)
        return User(item_response["id"], item_response["email"], item_response["active"])
    except Exception as e:
        print("Error reading user", e)
        raise e