#include "ApplicationConfig.h"
#include <sstream>
#include <fstream>
#include "../services/ApplicationService.h"
#include "../services/BloomFilterService.h"
#include "../services/FileStorageService.h"
#include "../utils/URLValidator.h"
#include "../utils/DefaultURLFormatter.h"
#include "../utils/DefaultURLNormalizer.h"
#include "../utils/DefaultBitVector.h"
#include "../bloom_filter/bloomFilter.h"
#include "../bloom_filter/hashFactory.h"

const string ApplicationConfig::m_configFilePath = "data/config.txt";
/**
 * @brief Configures the application by parsing the configuration line.
 */
shared_ptr<IApplicationService> ApplicationConfig::configure(const string& configLine) {
    size_t bitArraySize = 0;
    string Line = configLine;
    if (configFromFile(Line)) {
       vector<size_t> hashIds = parseHashIds(Line, bitArraySize); 
    }
    vector<size_t> hashIds = parseHashIds(configLine, bitArraySize);
    
    if (hashIds.empty() || bitArraySize == 0) {
        return nullptr;
    }
    return createApplicationService(bitArraySize, hashIds);
}

vector<size_t> ApplicationConfig::parseHashIds(const string& configLine, size_t& bitArraySize) {
    istringstream iss(configLine);
    vector<size_t> hashIds;
    
    if (!(iss >> bitArraySize)) {
        return hashIds; // Empty vector indicates error
    }
    
    size_t hashId;
    while (iss >> hashId) {
        hashIds.push_back(hashId);
    }
    
    return hashIds;
}
/** 
 * @brief Creates the application service with the specified parameters.
 * @param bitArraySize The size of the bit array.
 * @param hashIds The IDs of the hash functions to be used.
 */
shared_ptr<IApplicationService> ApplicationConfig::createApplicationService(
    size_t bitArraySize,
    const vector<size_t>& hashIds) {
        
        // Create URL validator components
        auto urlFormatter = make_shared<DefaultURLFormatter>();
        auto urlNormalizer = make_shared<DefaultURLNormalizer>();
        auto urlValidator = make_shared<URLValidator>(urlFormatter, urlNormalizer);
        
        // Create storage service
        auto storageService = make_shared<FileStorageService>();
        
        // Create bloom filter components
        auto hashFunctions = hashFactory::createHashFunctions(hashIds);
        auto bitVector = make_shared<DefaultBitVector>(bitArraySize);
        auto filter = make_shared<bloomFilter>(bitArraySize, hashFunctions, bitVector);
        
        // Create filter service
        auto filterService = make_shared<BloomFilterService>(filter, storageService);
        
        // Create and initialize application service
        auto appService = make_shared<ApplicationService>(filterService, storageService, urlValidator);
        appService->initialize("");  // We've already configured everything
        
        return appService;
    }
    
    

    bool ApplicationConfig::configFromFile(string& configLine) {
        ifstream inFile(m_configFilePath);
        if (!inFile) {
            return false;
        }
        getline(inFile, configLine);
        inFile.close();
        return !configLine.empty();
    }

    bool ApplicationConfig::saveConfigLine(const string& configLine) {
        ofstream outFile(m_configFilePath);
        if (!outFile) {
            return false;
        }
        outFile << configLine;
        outFile.close();
        return true;
    }