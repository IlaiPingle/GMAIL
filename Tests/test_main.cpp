#include <gtest/gtest.h>
#include "../src/ioHandling/inputManager.h"
#include "../src/utils/URLValidator.h"
#include <string>
#include <sstream>

TEST(InputManagerTests, SplitRequest_EmptyCommand_ReturnsFalse) {
    std::string command = "";
    std::string url;
    bool result = InputManager::splitRequest(command, url);
    EXPECT_FALSE(result);
}

TEST(InputManagerTests, SplitRequest_InvalidCommand_ReturnsFalse) {
    std::string command = "INVALID"; // No URL part
    std::string url;
    bool result = InputManager::splitRequest(command, url);
    EXPECT_FALSE(result);
}

TEST(InputManagerTests, SplitRequest_InvalidURL_ReturnsFalse) {
    std::string command = "POST invalid-url";
    std::string url;
    bool result = InputManager::splitRequest(command, url);
    EXPECT_FALSE(result);
}

TEST(InputManagerTests, SplitRequest_ValidCommand_ReturnsTrue) {
    std::string command = "POST http://example.com";
    std::string url;
    bool result = InputManager::splitRequest(command, url);
    EXPECT_TRUE(result);
    EXPECT_EQ(command, "POST");
    EXPECT_EQ(url, "http://example.com");
}

TEST(InputManagerTests, SplitRequest_ValidCommandWithSpace_ReturnsTrue) {
    std::string command = "GET   http://example.com";
    std::string url;
    bool result = InputManager::splitRequest(command, url);
    EXPECT_TRUE(result);
    EXPECT_EQ(command, "GET");
    EXPECT_EQ(url, "http://example.com");
}

TEST(InputManagerTests, SplitRequest_ValidDeleteCommand_ReturnsTrue) {
    std::string command = "DELETE http://example.com";
    std::string url;
    bool result = InputManager::splitRequest(command, url);
    EXPECT_TRUE(result);
    EXPECT_EQ(command, "DELETE");
    EXPECT_EQ(url, "http://example.com");
}

TEST(InputManagerTests, SplitRequest_MissingURL_ReturnsFalse) {
    std::string command = "DELETE ";
    std::string url;
    bool result = InputManager::splitRequest(command, url);
    EXPECT_FALSE(result);
}

TEST(InputManagerTests, SplitRequest_NoSpaceBeforeURL_ReturnsFalse) {
    std::string command = "DELETEhttp://example.com";
    std::string url;
    bool result = InputManager::splitRequest(command, url);
    EXPECT_FALSE(result);
}

TEST(InputManagerTests, SplitRequest_URLWithPath_ReturnsTrue) {
    std::string command = "GET http://example.com/path/to/resource";
    std::string url;
    bool result = InputManager::splitRequest(command, url);
    EXPECT_TRUE(result);
    EXPECT_EQ(command, "GET");
    EXPECT_EQ(url, "http://example.com/path/to/resource");
}

TEST(InputManagerTests, SplitRequest_URLWithPortNumber_ReturnsTrue) {
    std::string command = "POST http://example.com:8080";
    std::string url;
    bool result = InputManager::splitRequest(command, url);
    EXPECT_TRUE(result);
    EXPECT_EQ(command, "POST");
    EXPECT_EQ(url, "http://example.com:8080");
}