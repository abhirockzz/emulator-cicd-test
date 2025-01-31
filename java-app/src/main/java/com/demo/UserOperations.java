package com.demo;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.models.PartitionKey;
import com.azure.cosmos.models.CosmosItemResponse;

public class UserOperations {

    public static class User {
        private String id;
        private String email;
        private boolean active;

        public User() {}

        public User(String id, String email, boolean active) {
            this.id = id;
            this.email = email;
            this.active = active;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        @Override
        public String toString() {
            return "User{" +
                    "id='" + id + '\'' +
                    ", email='" + email + '\'' +
                    ", active=" + active +
                    '}';
        }
    }

    public static void createUser(CosmosContainer container, User user) throws Exception {
        try {
            CosmosItemResponse<User> response = container.createItem(user);
            System.out.println("Created user: " + response.getItem());
        } catch (Exception e) {
            System.out.println("Error creating user: " + e.getMessage());
            throw e;
        }
    }

    public static User readUser(CosmosContainer container, String userId) throws Exception {
        try {
            CosmosItemResponse<User> response = container.readItem(userId, new PartitionKey(userId), User.class);
            return response.getItem();
        } catch (Exception e) {
            System.out.println("Error reading user: " + e.getMessage());
            throw e;
        }
    }
}