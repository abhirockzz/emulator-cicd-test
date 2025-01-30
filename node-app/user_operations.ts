import { Container } from "@azure/cosmos";

type User = {
    id: string;
    email: string;
    active: boolean;
}

async function createUser(container: Container, user: User): Promise<void> {
    try {
        const { resource: createdUser } = await container.items.create(user);
        console.log("Created user", createdUser);
    } catch (err) {
        console.error("Error creating user", err);
        throw err;
    }
}

async function readUser(container: Container, id: string): Promise<User> {
    try {
        const { resource: user } = await container.item(id, id).read<User>();
        if (!user) {
            throw new Error(`User with id ${id} not found`);
        }
        return user;
    } catch (err) {
        console.error("Error reading user", err);
        throw err;
    }
}

export { User, createUser, readUser };