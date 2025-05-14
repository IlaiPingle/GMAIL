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
    std::string result = inputManager.processCommand("1 http://wwwexample.com");
    EXPECT_EQ(result, "201 Created");
}

TEST(InputManagerTests, ProcessCommand_ValidGetCommand_ReturnsSuccess) {
    auto commandProcessor = std::make_unique<CommandProcessor>();
    InputManager inputManager(std::move(commandProcessor));
    inputManager.processCommand("1 http://example.com"); // Add to blacklist
    std::string result = inputManager.processCommand("2 http://example.com");
    EXPECT_EQ(result, "200 OK");
}

TEST(InputManagerTests, ProcessCommand_ValidDeleteCommand_ReturnsSuccess) {
    auto commandProcessor = std::make_unique<CommandProcessor>();
    InputManager inputManager(std::move(commandProcessor));
    inputManager.processCommand("1 http://example.com"); // Add to blacklist
    std::string result = inputManager.processCommand("3 http://example.com");
    EXPECT_EQ(result, "204 No Content");
}

TEST(InputManagerTests, ProcessCommand_DeleteNonExistentURL_ReturnsNotFound) {
    auto commandProcessor = std::make_unique<CommandProcessor>();
    InputManager inputManager(std::move(commandProcessor));
    std::string result = inputManager.processCommand("3 http://nonexistent.com");
    EXPECT_EQ(result, "404 Not Found");
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
    inputManager.processCommand("1 http://example.com"); // Add to blacklist
    std::string result = inputManager.processCommand("3 http://example.com"); // Delete from blacklist
    EXPECT_EQ(result, "204 No Content");
}

TEST(InputManagerTests, ProcessCommand_DeleteFromBlacklist_URLNotInBlacklist_ReturnsNotFound) {
    auto commandProcessor = std::make_unique<CommandProcessor>();
    InputManager inputManager(std::move(commandProcessor));
    std::string result = inputManager.processCommand("3 http://notinblacklist.com"); // Attempt to delete non-existent URL
    EXPECT_EQ(result, "404 Not Found");
}

TEST(InputManagerTests, ProcessCommand_DeleteFromBlacklist_EmptyURL_ReturnsBadRequest) {
    auto commandProcessor = std::make_unique<CommandProcessor>();
    InputManager inputManager(std::move(commandProcessor));
    std::string result = inputManager.processCommand("3 "); // Attempt to delete with empty URL
    EXPECT_EQ(result, "400 Bad Request");
}

TEST(InputManagerTests, ProcessCommand_DeleteFromBlacklist_InvalidCommandFormat_ReturnsBadRequest) {
    auto commandProcessor = std::make_unique<CommandProcessor>();
    InputManager inputManager(std::move(commandProcessor));
    std::string result = inputManager.processCommand("3http://example.com"); // Invalid format (missing space)
    EXPECT_EQ(result, "400 Bad Request");
}