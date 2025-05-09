#include "gtest/gtest.h"
#include "../src/bloom_Filter/bloomFilter.h"
#include "../src/bloom_Filter/hashFactory.h"
#include "../src/services/FileStorageService.h"
#include <memory>
#include <functional>

using namespace std;

// Mock hashable class for testing
class MockHashable : public hashable {
public:
    size_t operator()(const string& input) const override {
        return hash<string>{}(input);
    }
};

// Test fixture for bloomFilter
class BloomFilterTest : public ::testing::Test {
protected:
    void SetUp() override {
        hashFunctions.push_back(make_shared<MockHashable>());
        bloomFilterInstance = make_unique<bloomFilter>(10, hashFunctions);
    }

    vector<shared_ptr<hashable>> hashFunctions;
    unique_ptr<bloomFilter> bloomFilterInstance;
};
// test for adding a URL and checking if bloom filter contains it.
TEST_F(BloomFilterTest, AddAndContains) {
    string url = "https://example.com";
    bloomFilterInstance->add(url);
    EXPECT_TRUE(bloomFilterInstance->contains(url));
}
// test for checking if url is inside the real blacklist.
TEST_F(BloomFilterTest, ContainsAbsolutely) {
    string url = "https://example.com";
    bloomFilterInstance->add(url);
    EXPECT_TRUE(bloomFilterInstance->containsAbsolutely(url));
    EXPECT_FALSE(bloomFilterInstance->containsAbsolutely("https://notadded.com"));
}

TEST_F(BloomFilterTest, DoesNotContain) {
    string url = "https://example.com";
    EXPECT_FALSE(bloomFilterInstance->contains(url));
}

TEST_F(BloomFilterTest, GetAndSetBlackList) {
    unordered_set<string> blackList = {"https://example.com", "https://test.com"};
    bloomFilterInstance->setBlackList(blackList);
    const auto& retrievedBlackList = bloomFilterInstance->getBlackList();
    EXPECT_EQ(retrievedBlackList.size(), 2);
    EXPECT_TRUE(retrievedBlackList.find("https://example.com") != retrievedBlackList.end());
    EXPECT_TRUE(retrievedBlackList.find("https://test.com") != retrievedBlackList.end());
}

TEST_F(BloomFilterTest, EmptyString) {
    // Test with an empty string
    bloomFilterInstance->add("");
    EXPECT_TRUE(bloomFilterInstance->contains(""));
    EXPECT_TRUE(bloomFilterInstance->containsAbsolutely(""));
}

TEST_F(BloomFilterTest, VeryLongURL) {
    // Test with an extremely long URL
    string longURL = "https://example.com/";
    for (int i = 0; i < 1000; i++) {
        longURL += "very_long_segment_";
    }
    
    bloomFilterInstance->add(longURL);
    EXPECT_TRUE(bloomFilterInstance->contains(longURL));
    EXPECT_TRUE(bloomFilterInstance->containsAbsolutely(longURL));
}

TEST_F(BloomFilterTest, SpecialCharacters) {
    // Test URLs with special characters
    string specialURL = "https://example.com/?q=test&param=value#fragment+with spaces";
    bloomFilterInstance->add(specialURL);
    EXPECT_TRUE(bloomFilterInstance->contains(specialURL));
}

TEST_F(BloomFilterTest, MultipleURLs) {
    // Test adding multiple URLs
    vector<string> urls = {
        "https://site1.com",
        "https://site2.com",
        "https://site3.com",
        "https://site4.com",
        "https://site5.com"
    };
    
    for (const auto& url : urls) {
        bloomFilterInstance->add(url);
    }
    
    for (const auto& url : urls) {
        EXPECT_TRUE(bloomFilterInstance->contains(url));
    }
}

TEST_F(BloomFilterTest, SimilarURLs) {
    // Test URLs that are very similar but not identical
    bloomFilterInstance->add("https://example.com/path");
    EXPECT_TRUE(bloomFilterInstance->contains("https://example.com/path"));
    EXPECT_FALSE(bloomFilterInstance->containsAbsolutely("https://example.com/path/"));
    EXPECT_FALSE(bloomFilterInstance->containsAbsolutely("https://example.com/Path"));
}

TEST_F(BloomFilterTest, EmptyBloomFilter) {
    // Create a bloom filter with no hash functions
    vector<shared_ptr<hashable>> emptyHashFunctions;
    auto emptyFilter = make_unique<bloomFilter>(10, emptyHashFunctions);
    
    // Adding to a filter with no hash functions should not crash
    emptyFilter->add("https://example.com");
    // Should return false as nothing can be added with no hash functions
    EXPECT_FALSE(emptyFilter->contains("https://example.com"));
}

TEST_F(BloomFilterTest, SmallBloomFilter) {
    // Create a very small bloom filter (size 1)
    auto smallFilter = make_unique<bloomFilter>(1, hashFunctions);
    
    // Add multiple URLs - with only one bit, collisions are guaranteed
    smallFilter->add("https://example1.com");
    smallFilter->add("https://example2.com");
    
    // containsAbsolutely should still work correctly
    EXPECT_TRUE(smallFilter->containsAbsolutely("https://example1.com"));
    EXPECT_TRUE(smallFilter->containsAbsolutely("https://example2.com"));
    EXPECT_FALSE(smallFilter->containsAbsolutely("https://example3.com"));
}