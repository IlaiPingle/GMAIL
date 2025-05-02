#include "gtest/gtest.h"
#include "../src/ioHandling/inputManager.h"
#include "../src/bloom_Filter/bloomFilter.h"
#include "../src/ioHandling/fileManager.h"
#include "../src/bloom_Filter/hashFactory.h"
#include <memory>
#include <string>
#include <vector>
#include <unordered_set>

using namespace std;

class InputManagerTest : public ::testing::Test {
protected:
    unique_ptr<inputManager> manager;

    void SetUp() override {
        vector<size_t> hashInfos = {1, 2, 3}; // Example hash function IDs
        vector<shared_ptr<hashable>> hashFunctions = hashFactory::createHashFunctions(hashInfos);
        unique_ptr<bloomFilter> filter = make_unique<bloomFilter>(1000, hashFunctions);
        unique_ptr<fileManager> fileMgr = make_unique<fileManager>("data/blacklist.txt", "data/bit_array.dat");
        manager = make_unique<inputManager>(move(filter), move(fileMgr));
    }
};

TEST_F(InputManagerTest, ConvertLine_AddToBlacklist_ValidURL) {
    string result = manager->convertLine("1 www.example.com");
    EXPECT_EQ(result, "");
}

TEST_F(InputManagerTest, ConvertLine_AddToBlacklist_MissingURL) {
    string result = manager->convertLine("1");
    EXPECT_EQ(result, "Error: Missing URL");
}

TEST_F(InputManagerTest, ConvertLine_CheckBlacklist_ValidURL) {
    manager->convertLine("1 www.example.com"); // Add to blacklist first
    string result = manager->convertLine("2 www.example.com");
    EXPECT_EQ(result, "true true");
}

TEST_F(InputManagerTest, ConvertLine_CheckBlacklist_NotInBlacklist) {
    string result = manager->convertLine("2 notblacklisted.com");
    EXPECT_EQ(result, "false");
}

TEST_F(InputManagerTest, ConvertLine_InvalidCommand) {
    string result = manager->convertLine("3 example.com");
    EXPECT_EQ(result, "Error: Invalid command");
}

TEST_F(InputManagerTest, ConvertLine_EmptyCommand) {
    string result = manager->convertLine("");
    EXPECT_EQ(result, "");
}


TEST_F(InputManagerTest, URL_Standardization_ThroughPublicAPI) {
    // Test URL standardization indirectly through convertLine
    manager->convertLine("1   www.EXAMPLE.com  "); // Should standardize URL internally
    string result = manager->convertLine("2 www.example.com"); // Lowercase and trimmed version
    EXPECT_EQ(result, "true true");
}


TEST_F(InputManagerTest, AddToBlacklist_ThroughPublicAPI) {
    // Use public convertLine method instead
    string result = manager->convertLine("1 www.example.com");
    EXPECT_EQ(result, "");
    // Verify it was added by checking it
    EXPECT_TRUE(manager->convertLine("2 www.example.com").find("true") != string::npos);
}

// REPLACED: Avoid calling private runCheckBlacklist method
TEST_F(InputManagerTest, CheckBlacklist_EmptyURL_ThroughPublicAPI) {
    string result = manager->convertLine("2 ");
    EXPECT_EQ(result, "false");
}

TEST_F(InputManagerTest, InitFirstLine_ValidInput) {
    string line = "1000 1 2 3";
    unique_ptr<inputManager> newManager = inputManager::initFirstLine(line);
    EXPECT_NE(newManager, nullptr);
}

TEST_F(InputManagerTest, InitFirstLine_InvalidBitArraySize) {
    string line = "invalid_size 1 2 3";
    unique_ptr<inputManager> newManager = inputManager::initFirstLine(line);
    EXPECT_EQ(newManager, nullptr);
}

TEST_F(InputManagerTest, InitFirstLine_InvalidHashFunctions) {
    string line = "1000";
    unique_ptr<inputManager> newManager = inputManager::initFirstLine(line);
    EXPECT_EQ(newManager, nullptr);
}

// Additional edge cases using public API
TEST_F(InputManagerTest, ConvertLine_TrimAndNormalization) {
    manager->convertLine("1 www.EXAMPLE.COM");
    string result = manager->convertLine("2 www.example.com");
    EXPECT_EQ(result, "true true"); // Case-insensitive matching
    
    manager->convertLine("1    www.trailing.spaces   ");
    result = manager->convertLine("2 www.trailing.spaces");
    EXPECT_EQ(result, "true true"); // Whitespace trimming
}

TEST_F(InputManagerTest, ConvertLine_SpecialCharacters) {
    // Test with special characters in URL
    string specialURL = "www.special-chars_123.example.com";
    manager->convertLine("1 " + specialURL);
    string result = manager->convertLine("2 " + specialURL);
    EXPECT_EQ(result, "true true");
}

TEST_F(InputManagerTest, ConvertLine_EmptyURL) {
    // Test with empty URL in command
    string result = manager->convertLine("1 ");
    EXPECT_EQ(result, ""); // No error message expected
    
    result = manager->convertLine("2 ");
    EXPECT_EQ(result, "false"); // Not in blacklist
}

TEST_F(InputManagerTest, ConvertLine_MalformedCommand) {
    string result = manager->convertLine("1example.com"); // No space after command
    EXPECT_EQ(result, "Error: Invalid command");
}