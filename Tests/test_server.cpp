#include <gtest/gtest.h>
#include "../networking/server.h"

// Test for server initialization
TEST(ServerTest, Initialization) {
    Server server(8080);
    EXPECT_EQ(server.getPort(), 8080);
    EXPECT_TRUE(server.isInitialized());
}
// Test for server initialization
TEST(ServerTest, Initialization) {
    Server server(8080);
    EXPECT_EQ(server.getPort(), 8080);
    EXPECT_TRUE(server.isInitialized());
}

// Test for server shutdown
TEST(ServerTest, Shutdown) {
    Server server(8080);
    server.start();
    EXPECT_TRUE(server.isRunning());

    server.stop();
    EXPECT_FALSE(server.isRunning());
}

// Test for server starting on an invalid port
TEST(ServerTest, InvalidPort) {
    Server server(-1); // Invalid port
    EXPECT_FALSE(server.isInitialized());
}

// Test for starting the server twice
TEST(ServerTest, DoubleStart) {
    Server server(8080);
    server.start();
    EXPECT_TRUE(server.isRunning());

    server.start(); // Attempt to start again
    EXPECT_TRUE(server.isRunning()); // Should still be running without issues
}

// Test for stopping the server twice
TEST(ServerTest, DoubleStop) {
    Server server(8080);
    server.start();
    EXPECT_TRUE(server.isRunning());

    server.stop();
    EXPECT_FALSE(server.isRunning());

    server.stop(); // Attempt to stop again
    EXPECT_FALSE(server.isRunning()); // Should remain stopped
}

// Test for handling multiple clients
TEST(ServerTest, MultipleClients) {
    Server server(8080);
    server.start();
    EXPECT_TRUE(server.isRunning());

    // Simulate multiple clients connecting
    for (int i = 0; i < 10; ++i) {
        EXPECT_TRUE(server.handleClient(i)); // Assuming handleClient simulates a client connection
    }

    server.stop();
    EXPECT_FALSE(server.isRunning());
}

// Test for server behavior with no clients
TEST(ServerTest, NoClients) {
    Server server(8080);
    server.start();
    EXPECT_TRUE(server.isRunning());

    // No clients connected, server should still be running
    EXPECT_TRUE(server.isRunning());

    server.stop();
    EXPECT_FALSE(server.isRunning());
}

// Test for server handling invalid client connections
TEST(ServerTest, InvalidClient) {
    Server server(8080);
    server.start();
    EXPECT_TRUE(server.isRunning());

    EXPECT_FALSE(server.handleClient(-1)); // Invalid client ID

    server.stop();
    EXPECT_FALSE(server.isRunning());
}
