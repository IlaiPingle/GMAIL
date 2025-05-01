#include "gtest/gtest.h"
#include "../bloom_Filter/bloomFilter.h"
#include "../bloom_Filter/hashFactory.h"
#include "../ioHandling/inputManager.h"
#include "../ioHandling/fileManager.h"
#include <memory>
#include <string>

using namespace std;

class InputManagerTest : public ::testing::Test {
protected:
    void SetUp() override {
        // Set up mock or test dependencies if needed
        vector<size_t> hashInfos = {3, 5, 7};
        vector<shared_ptr<hashable>> hashFunctions = hashFactory::createHashFunctions(hashInfos);
        bloomFilterPtr = make_unique<bloomFilter>(100, hashFunctions);
        fileManagerPtr = make_unique<fileManager>("test_bloom.txt", "test_bitArray.txt");
        inputManagerPtr = make_unique<inputManager>(move(bloomFilterPtr), move(fileManagerPtr));
    }

    unique_ptr<bloomFilter> bloomFilterPtr;
    unique_ptr<fileManager> fileManagerPtr;
    unique_ptr<inputManager> inputManagerPtr;
};

TEST_F(InputManagerTest, ConvertLine_AddToBlacklist) {
    string command = "1 example.com";
    string result = inputManagerPtr->convertLine(command);
    EXPECT_EQ(result, "");
    EXPECT_TRUE(inputManagerPtr->runCheckBlacklist("example.com") == "True true");
}

TEST_F(InputManagerTest, ConvertLine_CheckBlacklist_True) {
    inputManagerPtr->convertLine("1 example.com");
    string command = "2 example.com";
    string result = inputManagerPtr->convertLine(command);
    EXPECT_EQ(result, "True true");
}

TEST_F(InputManagerTest, ConvertLine_CheckBlacklist_False) {
    string command = "2 nonexisting.com";
    string result = inputManagerPtr->convertLine(command);
    EXPECT_EQ(result, "False");
}

TEST_F(InputManagerTest, InitFirstLine_ValidInput) {
    string initLine = "100 3 5 7";
    unique_ptr<inputManager> newInputManager = inputManager::initFirstLine(initLine);
    EXPECT_NE(newInputManager, nullptr);
}

TEST_F(InputManagerTest, InitFirstLine_InvalidBitArraySize) {
    string initLine = "invalid_size 3 5 7";
    unique_ptr<inputManager> newInputManager = inputManager::initFirstLine(initLine);
    EXPECT_EQ(newInputManager, nullptr);
}

TEST_F(InputManagerTest, InitFirstLine_InvalidHashFunctions) {
    string initLine = "100";
    unique_ptr<inputManager> newInputManager = inputManager::initFirstLine(initLine);
    EXPECT_EQ(newInputManager, nullptr);
}

TEST_F(InputManagerTest, RunAddToBlacklist) {
    string url = "example.com";
    string result = inputManagerPtr->runAddToBlacklist(url);
    EXPECT_EQ(result, "");
    EXPECT_TRUE(inputManagerPtr->runCheckBlacklist(url) == "True true");
}

TEST_F(InputManagerTest, RunCheckBlacklist_TrueFalse) {
    string url = "example.com";
    inputManagerPtr->runAddToBlacklist(url);
    EXPECT_EQ(inputManagerPtr->runCheckBlacklist(url), "True true");
}

TEST_F(InputManagerTest, RunCheckBlacklist_False) {
    string url = "nonexisting.com";
    EXPECT_EQ(inputManagerPtr->runCheckBlacklist(url), "False");
}

TEST_F(InputManagerTest, EmptyURL) {
    // Test with empty URL
    string command = "1 ";
    string result = inputManagerPtr->convertLine(command);
    EXPECT_EQ(result, "");
    
    // Check the empty URL
    EXPECT_EQ(inputManagerPtr->runCheckBlacklist(""), "False");
}

TEST_F(InputManagerTest, MalformedCommand) {
    // Invalid command number
    string command = "3 example.com";
    string result = inputManagerPtr->convertLine(command);
    EXPECT_EQ(result, "Error: Invalid command");
    
    // Command without URL
    command = "1";
    result = inputManagerPtr->convertLine(command);
    EXPECT_EQ(result, "Error: Missing URL");
}

TEST_F(InputManagerTest, URLWithSpecialCharacters) {
    // URL with special characters
    string url = "test-site_123.co.uk/page?param=value&other=123";
    inputManagerPtr->runAddToBlacklist(url);
    EXPECT_EQ(inputManagerPtr->runCheckBlacklist(url), "True true");
}

TEST_F(InputManagerTest, VeryLongURL) {
    // Test with a very long URL (close to typical length limits)
    string longURL = "subdomain.";
    for (int i = 0; i < 10; i++) {
        longURL += "reallylongdomainname";
    }
    longURL += ".com/path/to/resource?param1=value1&param2=value2";
    
    inputManagerPtr->runAddToBlacklist(longURL);
    EXPECT_EQ(inputManagerPtr->runCheckBlacklist(longURL), "True true");
}

TEST_F(InputManagerTest, LeadingTrailingWhitespace) {
    // Test with whitespace
    string url = "example.org";
    inputManagerPtr->runAddToBlacklist(url);
    
    // Check with leading/trailing whitespace
    EXPECT_EQ(inputManagerPtr->runCheckBlacklist("  example.org"), "True true");
    EXPECT_EQ(inputManagerPtr->runCheckBlacklist("example.org  "), "True true");
    EXPECT_EQ(inputManagerPtr->runCheckBlacklist("  example.org  "), "True true");
}

TEST_F(InputManagerTest, CaseSensitivity) {
    // Test case sensitivity
    string url = "Example.Com";
    inputManagerPtr->runAddToBlacklist(url);
    
    // These should match if case-insensitive, fail if case-sensitive
    EXPECT_EQ(inputManagerPtr->runCheckBlacklist("example.com"), "True true");
    EXPECT_EQ(inputManagerPtr->runCheckBlacklist("EXAMPLE.COM"), "True true");
}

TEST_F(InputManagerTest, MultipleAdditions) {
    // Add multiple URLs
    vector<string> urls = {"site1.com", "site2.net", "site3.org", "site4.io"};
    
    for (const auto& url : urls) {
        inputManagerPtr->runAddToBlacklist(url);
    }
    
    // Check all were added
    for (const auto& url : urls) {
        EXPECT_EQ(inputManagerPtr->runCheckBlacklist(url), "True true");
    }
    
    // Check a non-added URL
    EXPECT_EQ(inputManagerPtr->runCheckBlacklist("different-site.com"), "False");
}

TEST_F(InputManagerTest, InitFirstLine_EdgeSizes) {
    // Test with minimum size
    string minSizeInit = "1 3 5 7";
    unique_ptr<inputManager> minSizeManager = inputManager::initFirstLine(minSizeInit);
    EXPECT_NE(minSizeManager, nullptr);
    
    // Test with very large size
    string largeSizeInit = "1000000 3 5 7";
    unique_ptr<inputManager> largeSizeManager = inputManager::initFirstLine(largeSizeInit);
    EXPECT_NE(largeSizeManager, nullptr);
}