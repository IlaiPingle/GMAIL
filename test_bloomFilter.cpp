#include <cassert>
#include <iostream>
#include <fstream>
#include "bloomFilter.h"

// Test saving the Bloom filter to a file
void testSaveToFile() {
    // Arrange
    int arraySize = 100;
    int numHashes = 3;
    bloomFilter bf(arraySize, numHashes);
    std::string testFile = "test_bloom_filter_save.bin";

    // Add some data to the Bloom filter
    bf.add("example.com");
    bf.add("test.com");

    // Act
    bf.saveToFile(testFile);

    // Assert
    std::ifstream file(testFile, std::ios::binary);
    assert(file.good() && "File should exist after saving.");
    file.close();

    // Cleanup
    std::remove(testFile.c_str());
    std::cout << "testSaveToFile passed!" << std::endl;
}

// Test loading the Bloom filter from a file
void testLoadFromFile() {
    // Arrange
    int arraySize = 100;
    int numHashes = 3;
    bloomFilter bf(arraySize, numHashes);
    std::string testFile = "test_bloom_filter_load.bin";

    // Add some data to the Bloom filter and save it
    bf.add("example.com");
    bf.add("test.com");
    bf.saveToFile(testFile);

    // Create a new Bloom filter and load the data
    bloomFilter bfLoaded(arraySize, numHashes);
    bfLoaded.loadFromFile(testFile);

    // Act & Assert
    assert(bfLoaded.contains("example.com") && "Loaded Bloom filter should contain 'example.com'.");
    assert(bfLoaded.contains("test.com") && "Loaded Bloom filter should contain 'test.com'.");
    assert(!bfLoaded.contains("notadded.com") && "Loaded Bloom filter should not contain 'notadded.com'.");

    // Cleanup
    std::remove(testFile.c_str());
    std::cout << "testLoadFromFile passed!" << std::endl;
}

// Test saving and loading an empty Bloom filter
void testSaveAndLoadEmptyFilter() {
    // Arrange
    int arraySize = 100;
    int numHashes = 3;
    bloomFilter bf(arraySize, numHashes);
    std::string testFile = "test_bloom_filter_empty.bin";

    // Save the empty Bloom filter
    bf.saveToFile(testFile);

    // Create a new Bloom filter and load the data
    bloomFilter bfLoaded(arraySize, numHashes);
    bfLoaded.loadFromFile(testFile);

    // Act & Assert
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