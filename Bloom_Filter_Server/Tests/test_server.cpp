#include <gtest/gtest.h>
#include "../src/networking/Server.h"
#include "../src/services/ApplicationService.h"
#include "../src/services/CommandProcessor.h"
#include "../src/services/BloomFilterService.h"
#include "../src/bloom_Filter/bloomFilter.h"
#include "../src/services/FileStorageService.h"
#include "../src/bloom_Filter/hashFactory.h"
#include <memory>
#include <thread>
#include <chrono>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>

class ServerTest : public ::testing::Test {
protected:
    std::shared_ptr<ApplicationService> appService;
    
    void SetUp() override {
        // Create components needed for ApplicationService
        auto storageService = std::make_shared<FileStorageService>();
        std::vector<size_t> hashIds = {1, 2, 3}; // Simple hash functions
        auto hashFunctions = hashFactory::createHashFunctions(hashIds);
        auto filter = std::make_shared<bloomFilter>(1000, hashFunctions);
        auto filterService = std::make_shared<BloomFilterService>(filter, storageService);
        auto commandProcessor = std::make_shared<CommandProcessor>(filterService);
        
        // Now we can create the ApplicationService with the required CommandProcessor
        appService = std::make_shared<ApplicationService>(commandProcessor);
    }
};

// Test that the server can be constructed without crashing
TEST_F(ServerTest, Construction) {
    Server server(8080, appService);
    // No assertions needed - just testing that construction doesn't throw
}

// Test starting the server (in a separate thread to avoid infinite loop)
TEST_F(ServerTest, StartServer) {
    // Use a high port number to avoid conflicts
    const int TEST_PORT = 9876;
    Server server(TEST_PORT, appService);
    
    // Start server in a separate thread
    std::thread serverThread([&server]() {
        bool result = server.start();
        // This won't normally be reached due to infinite loop
    });
    
    // Give server time to start
    std::this_thread::sleep_for(std::chrono::milliseconds(500));
    
    // Try to connect to the server
    int clientSocket = socket(AF_INET, SOCK_STREAM, 0);
    ASSERT_NE(clientSocket, -1) << "Failed to create client socket";
    
    struct sockaddr_in serverAddr{};
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_port = htons(TEST_PORT);
    serverAddr.sin_addr.s_addr = inet_addr("127.0.0.1");
    
    // Try to connect (should succeed if server is running)
    int connectResult = connect(clientSocket, (struct sockaddr*)&serverAddr, sizeof(serverAddr));
    
    // Clean up the socket
    close(clientSocket);
    
    // Force terminate the server thread since we can't join it
    pthread_t threadId = serverThread.native_handle();
    pthread_cancel(threadId);
    serverThread.detach();
    
    EXPECT_NE(connectResult, -1) << "Failed to connect to server";
}

// Test invalid port behavior
TEST_F(ServerTest, InvalidPort) {
    Server server(-1, appService);
    
    // Expect start() to return false with invalid port
    bool startResult = false;
    std::thread serverThread([&server, &startResult]() {
        startResult = server.start();
    });
    
    // Give it time to attempt starting
    std::this_thread::sleep_for(std::chrono::milliseconds(100));
    
    // If thread is still running, force terminate
    if (serverThread.joinable()) {
        pthread_t threadId = serverThread.native_handle();
        pthread_cancel(threadId);
        serverThread.detach();
    } else {
        serverThread.join();
    }
    
    EXPECT_FALSE(startResult);
}