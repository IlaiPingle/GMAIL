#include <gtest/gtest.h>
#include "../src/ioHandling/inputManager.h"
#include "../src/services/CommandProcessor.h"

TEST(InputManagerTests, ProcessCommand_EmptyCommand_ReturnsBadRequest) {
    InputManager inputManager;
    std::string result = inputManager.processCommand("");
    EXPECT_EQ(result, "400 Bad Request");
}

TEST(InputManagerTests, ProcessCommand_InvalidCommand_ReturnsBadRequest) {
    InputManager inputManager;
    std::string result = inputManager.processCommand("INVALID_COMMAND");
    EXPECT_EQ(result, "400 Bad Request");
}

TEST(InputManagerTests, ProcessCommand_ValidPostCommand_ReturnsSuccess) {
    auto commandProcessor = std::make_unique<CommandProcessor>();
    InputManager inputManager(std::move(commandProcessor));
    std::string result = inputManager.processCommand("POST http://wwwexample.com");
    EXPECT_EQ(result, "200 Ok");  // Adjust expected response based on actual implementation
}

TEST(InputManagerTests, ProcessCommand_ValidGetCommand_ReturnsSuccess) {
    auto commandProcessor = std::make_unique<CommandProcessor>();
    InputManager inputManager(std::move(commandProcessor));
    inputManager.processCommand("POST http://example.com"); // Add to blacklist
    std::string result = inputManager.processCommand("GET http://example.com");
    EXPECT_TRUE(result.find("200 Ok") != std::string::npos);  // Check response contains "200 Ok"
}

TEST(InputManagerTests, ProcessCommand_ValidDeleteCommand_ReturnsSuccess) {
    auto commandProcessor = std::make_unique<CommandProcessor>();
    InputManager inputManager(std::move(commandProcessor));
    inputManager.processCommand("POST http://example.com"); // Add to blacklist
    std::string result = inputManager.processCommand("DELETE http://example.com");
    EXPECT_EQ(result, "200 Ok");  // Adjust expected response based on actual implementation
}

TEST(InputManagerTests, ProcessCommand_DeleteNonExistentURL_ReturnsNotFound) {
    auto commandProcessor = std::make_unique<CommandProcessor>();
    InputManager inputManager(std::move(commandProcessor));
    std::string result = inputManager.processCommand("DELETE http://nonexistent.com");
    EXPECT_EQ(result, "404 Not Found");  // Adjust if needed
}

TEST(InputManagerTests, CreateFromConfig_InvalidConfigLine_ReturnsNullptr) {
    std::string invalidConfig = "INVALID_CONFIG";
    auto inputManager = InputManager::createFromConfig(invalidConfig);
    EXPECT_EQ(inputManager, nullptr);
}

TEST(InputManagerTests, CreateFromConfig_ValidConfigLine_ReturnsInputManager) {
    std::string validConfig = "1000 1 2 3";
    auto inputManager = InputManager::createFromConfig(validConfig);
    EXPECT_NE(inputManager, nullptr);
}

TEST(InputManagerTests, ProcessCommand_DeleteFromBlacklist_ValidURL_ReturnsSuccess) {
    auto commandProcessor = std::make_unique<CommandProcessor>();
    InputManager inputManager(std::move(commandProcessor));
    inputManager.processCommand("POST http://example.com"); // Add to blacklist
    std::string result = inputManager.processCommand("DELETE http://example.com"); // Delete from blacklist
    EXPECT_EQ(result, "200 Ok");  // Adjust expected response based on actual implementation
}

TEST(InputManagerTests, ProcessCommand_DeleteFromBlacklist_URLNotInBlacklist_ReturnsNotFound) {
    auto commandProcessor = std::make_unique<CommandProcessor>();
    InputManager inputManager(std::move(commandProcessor));
    std::string result = inputManager.processCommand("DELETE http://notinblacklist.com"); // Attempt to delete non-existent URL
    EXPECT_EQ(result, "404 Not Found");  // Adjust if needed
}

TEST(InputManagerTests, ProcessCommand_DeleteFromBlacklist_EmptyURL_ReturnsBadRequest) {
    auto commandProcessor = std::make_unique<CommandProcessor>();
    InputManager inputManager(std::move(commandProcessor));
    std::string result = inputManager.processCommand("DELETE "); // Attempt to delete with empty URL
    EXPECT_EQ(result, "400 Bad Request");
}

TEST(InputManagerTests, ProcessCommand_DeleteFromBlacklist_InvalidCommandFormat_ReturnsBadRequest) {
    auto commandProcessor = std::make_unique<CommandProcessor>();
    InputManager inputManager(std::move(commandProcessor));
    std::string result = inputManager.processCommand("DELETEhttp://example.com"); // Invalid format (missing space)
    EXPECT_EQ(result, "400 Bad Request");
}