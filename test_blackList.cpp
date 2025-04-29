#include <iostream>
#include "bloomFilter.h"
#include <random>
#include <sstream>
#include <direct.h>

// Function to generate random URLs
std::string generateRandomUrl() {
    static const char alphanum[] =
        "0123456789"
        "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        "abcdefghijklmnopqrstuvwxyz";
    
    std::random_device rd;
    std::mt19937 gen(rd());
    std::uniform_int_distribution<> length_dist(5, 15);
    std::uniform_int_distribution<> char_dist(0, sizeof(alphanum) - 2);
    
    int length = length_dist(gen);
    std::stringstream ss;
    ss << "http://";
    
    for (int i = 0; i < length; ++i) {
        ss << alphanum[char_dist(gen)];
    }
    
    ss << ".com";
    return ss.str();
}

int main() {
    try {
        if (_mkdir("data") != 0 && errno != EEXIST) {
            std::cerr << "Error creating directory" << std::endl;
            return 1;
        }
        /*std::filesystem::path dataDir = "data";
        if (!std::filesystem::exists(dataDir)) {
            std::filesystem::create_directory(dataDir);
        }*/
        // Create a bloom filter
        bloomFilter filter(1000, 5);
        
        // Add some URLs to the blacklist
        filter.add("http://malicious-site1.com");
        filter.add("http://malicious-site2.com");
        filter.add("http://malicious-site3.com");
        
        // Save both the filter and blacklist to files
        std::cout << "Saving filter and blacklist..." << std::endl;
        filter.saveToFile("data/bloom filter.dat");
        filter.saveBlackListToFile("data/blacklist.txt");
        
        // Create a new filter
        bloomFilter newFilter(1000, 5);
        
        // Load the saved data
        std::cout << "Loading filter and blacklist..." << std::endl;
        newFilter.loadFromFile("data/bloom filter.dat");
        newFilter.loadBlackListFromFile("data/blacklist.txt");
        
        // Test if the loaded filter contains the same URLs
        std::vector<std::string> testUrls = {
            "http://malicious-site1.com",
            "http://malicious-site2.com",
            "http://malicious-site3.com",
            "http://innocent-site.com"  // Should not be in the filter
        };
        
        std::cout << "\nTesting loaded filter:" << std::endl;
        for (const auto& url : testUrls) {
            std::cout << "URL: " << url << std::endl;
            std::cout << "  In filter: " << (newFilter.contains(url) ? "Yes" : "No") << std::endl;
            std::cout << "  False positive: " << (newFilter.isFalsePositive(url) ? "Yes" : "No") << std::endl;
        }

        // NEW CODE: Test for false positives
        std::cout << "\n\n=== Testing for False Positives ===" << std::endl;
        
        // Create a small Bloom filter to increase chance of false positives
        bloomFilter smallFilter(50, 3);  // Small size, fewer hash functions
        
        // Add many URLs to increase bit density
        std::cout << "Adding 30 URLs to small filter..." << std::endl;
        std::vector<std::string> addedUrls;
        for (int i = 0; i < 30; ++i) {
            std::string url = "http://added-" + std::to_string(i) + ".com";
            smallFilter.add(url);
            addedUrls.push_back(url);
        }
        
        // Test random URLs until we find a false positive
        std::cout << "Searching for false positives..." << std::endl;
        int testedCount = 0;
        bool foundFalsePositive = false;
        std::string falsePositiveUrl;
        
        while (!foundFalsePositive && testedCount < 1000) {
            std::string testUrl = generateRandomUrl();
            testedCount++;
            
            // Skip if this is a URL we actually added
            bool wasAdded = false;
            for (const auto& url : addedUrls) {
                if (url == testUrl) {
                    wasAdded = true;
                    break;
                }
            }
            
            if (wasAdded) continue;
            
            // Check if it's a false positive
            if (smallFilter.contains(testUrl) && smallFilter.isFalsePositive(testUrl)) {
                foundFalsePositive = true;
                falsePositiveUrl = testUrl;
            }
            
            if (testedCount % 100 == 0) {
                std::cout << "  Tested " << testedCount << " URLs so far..." << std::endl;
            }
        }
        
        if (foundFalsePositive) {
            std::cout << "Found a false positive after testing " << testedCount << " URLs!" << std::endl;
            std::cout << "False positive URL: " << falsePositiveUrl << std::endl;
            std::cout << "Verification:" << std::endl;
            std::cout << "  Contains() returns: " << (smallFilter.contains(falsePositiveUrl) ? "true" : "false") << std::endl;
            std::cout << "  isFalsePositive() returns: " << (smallFilter.isFalsePositive(falsePositiveUrl) ? "true" : "false") << std::endl;
        } else {
            std::cout << "No false positives found after testing " << testedCount << " URLs." << std::endl;
        }
        
        return 0;
    }
    catch (const std::exception& e) {
        std::cerr << "Error: " << e.what() << std::endl;
        return 1;
    }
}