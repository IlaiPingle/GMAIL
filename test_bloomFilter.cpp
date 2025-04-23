#include <cassert>
#include <iostream>
#include <fstream>
#include "bloomFilter.h"
#include "repeatedHash.h"

// Test saving the Bloom filter to a file
void testSaveToFile() {
    int arraySize = 100;
    int numHashes = 3;
    bloomFilter bf(arraySize, numHashes);
    std::string testFile = "test_bloom_filter_save.bin";

    // Add data to the Bloom filter
    bf.add("example.com");
    bf.add("test.com");

    // Save to file
    bf.saveToFile(testFile);

    // Verify file exists
    std::ifstream file(testFile, std::ios::binary);
    assert(file.good() && "File should exist after saving.");
    file.close();

    // Cleanup
    std::remove(testFile.c_str());
    std::cout << "testSaveToFile passed!" << std::endl;
}

// Test loading the Bloom filter from a file
void testLoadFromFile() {
    int arraySize = 100;
    int numHashes = 3;
    bloomFilter bf(arraySize, numHashes);
    std::string testFile = "test_bloom_filter_load.bin";

    // Add data and save
    bf.add("example.com");
    bf.add("test.com");
    bf.saveToFile(testFile);

    // Load into a new Bloom filter
    bloomFilter bfLoaded(arraySize, numHashes);
    bfLoaded.loadFromFile(testFile);

    // Verify data
    assert(bfLoaded.contains("example.com") && "Loaded Bloom filter should contain 'example.com'.");
    assert(bfLoaded.contains("test.com") && "Loaded Bloom filter should contain 'test.com'.");
    assert(!bfLoaded.contains("notadded.com") && "Loaded Bloom filter should not contain 'notadded.com'.");

    // Cleanup
    std::remove(testFile.c_str());
    std::cout << "testLoadFromFile passed!" << std::endl;
}

// Test saving and loading an empty Bloom filter
void testSaveAndLoadEmptyFilter() {
    int arraySize = 100;
    int numHashes = 3;
    bloomFilter bf(arraySize, numHashes);
    std::string testFile = "test_bloom_filter_empty.bin";

    // Save empty Bloom filter
    bf.saveToFile(testFile);

    // Load into a new Bloom filter
    bloomFilter bfLoaded(arraySize, numHashes);
    bfLoaded.loadFromFile(testFile);

    // Verify no data
    assert(!bfLoaded.contains("example.com") && "Empty Bloom filter should not contain 'example.com'.");

    // Cleanup
    std::remove(testFile.c_str());
    std::cout << "testSaveAndLoadEmptyFilter passed!" << std::endl;
}

int main() {
    testSaveToFile();
    testLoadFromFile();
    testSaveAndLoadEmptyFilter();
    std::cout << "All tests passed!" << std::endl;
    return 0;
}