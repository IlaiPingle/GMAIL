#include <gtest/gtest.h>
#include "../src/bloom_Filter/bloomFilter.h"
#include "../src/bloom_Filter/hashFactory.h"

TEST(BloomFilterTest, AddAndContains) {
    auto hashFuncs = hashFactory::createHashFunctions({1, 2});
    bloomFilter filter(10, hashFuncs);
    EXPECT_FALSE(filter.contains("example.com"));
    filter.add("example.com");
    EXPECT_TRUE(filter.contains("example.com"));
    EXPECT_TRUE(filter.containsAbsolutely("example.com"));

}

TEST(BloomFilterTest, Remove) {
    auto hashFuncs = hashFactory::createHashFunctions({1,3});
    bloomFilter filter(10, hashFuncs);
    filter.add("test.com");
    EXPECT_TRUE(filter.containsAbsolutely("test.com"));
    filter.remove("test.com");
    EXPECT_FALSE(filter.containsAbsolutely("test.com"));
}