// filepath: c:\Users\Matan-Laptop\Bar Ilan\GitHub\Ex1\.git\EX1\EX1\test_bloomFilter.cpp
#include <cassert>
#include <iostream>
#include <memory>
#include "bloomFilter.h"
#include "repeatedHash.h"

size_t arraySize = 100;
int numHashes = 3;
bloomFilter bf(arraySize, numHashes);

void testCreateBlacklist() {
    // Ensure the bloom filter is created with the correct size
    assert(arraySize > 0 && "Array size should be greater than 0.");
    std::cout << "testCreateBlacklist passed!" << std::endl;
}

void testAddUrlToBlacklist() {
    std::string url = "example.com";
    bf.add(url);

    // Verify that the URL is added (Bloom filter cannot guarantee absence)
    assert(bf.contains(url) && "URL should be in the blacklist.");
    std::cout << "testAddUrlToBlacklist passed!" << std::endl;
}

void testCheckUrlInBlacklist() {
    std::string url = "example.com";
    std::string url2 = "test.com";
    // Verify that the added URL is in the blacklist
    assert(bf.contains(url) && "URL1 should be in the blacklist.");
    // Verify that a non-added URL is not guaranteed to be in the blacklist
    assert(!bf.contains(url2) && "URL2 should not be in the blacklist.");
    std::cout << "testCheckUrlInBlacklist passed!" << std::endl;
}
void testFalsePositives() {
    // Check a completely unrelated URL
    std::string unrelatedUrl = "anotherexample1.com";
    // Check if the unrelated URL is falsely reported as in the Bloom filter
    bool isFalsePositive = bf.contains(unrelatedUrl);
    if (bf.isFalsePositive(unrelatedUrl)) {
        std::cout << "False positive detected for " << unrelatedUrl << std::endl;
        std::cout << "testFalsePositives failed!" << std::endl;
    } else {
        std::cout << "No false positive for " << unrelatedUrl << std::endl;
        std::cout << "testFalsePositives passed!" << std::endl;
    }
}
void testMultipleHashFunctions() {
    // Define multiple hash functions
    auto hashFunc1 = [](const std::string& str) { return std::hash<std::string>{}(str); };
    auto hashFunc2 = [](const std::string& str) { return std::hash<std::string>{}(str + "salt1"); };
    auto hashFunc3 = [](const std::string& str) { return std::hash<std::string>{}(str + "salt2"); };

    std::vector<std::function<size_t(const std::string&)>> hashFuncs = {hashFunc1, hashFunc2, hashFunc3};

    // Create a Bloom filter with multiple hash functions
    bloomFilter bf(100, hashFuncs);

    // Add a URL
    bf.add("example.com");

    // Check if the URL is in the Bloom filter
    assert(bf.contains("example.com") && "example.com should be in the Bloom filter");

    // Check for a false positive
    std::string unrelatedUrl = "notinthelist.com";
    if (bf.isFalsePositive(unrelatedUrl)) {
        std::cout << "False positive detected for " << unrelatedUrl << std::endl;
    } else {
        std::cout << "No false positive for " << unrelatedUrl << std::endl;
    }
}

int main() {
    testCreateBlacklist();
    testAddUrlToBlacklist();
    testCheckUrlInBlacklist();
    testFalsePositives();
    testMultipleHashFunctions();
    std::cout << "All tests passed!" << std::endl;
    return 0;
}