#include "ApplicationConfig.h"



const string ApplicationConfig::m_configFilePath = "data/config.txt";
/**
* @brief Configures the application by parsing the configuration line.
*/
shared_ptr<ApplicationService> ApplicationConfig::configure(const string& configLine) {
    size_t bitArraySize = 0;
    string Line = configLine;
    vector<size_t> hashIds ;
    bool createdFromFile;
    if (configFromFile(Line)) {
        hashIds = parseHashIds(Line, bitArraySize);
        createdFromFile = true; 
    } 
    else {    
        hashIds = parseHashIds(configLine, bitArraySize);
        saveConfigLine(configLine);
        createdFromFile = false;
    }
    if (hashIds.empty() || bitArraySize == 0) {
        return nullptr;
    }
    return createApplicationService(bitArraySize, hashIds,createdFromFile); // Create application service
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
shared_ptr<ApplicationService> ApplicationConfig::createApplicationService(
    size_t bitArraySize, const vector<size_t>& hashIds,const bool& createdfromFile) {
        // Create storage service
        auto storageService = make_shared<FileStorageService>();
        
        // Create bloom filter components
        auto hashFunctions = hashFactory::createHashFunctions(hashIds);
        auto filter = make_shared<bloomFilter>(bitArraySize, hashFunctions);
        
        // Create filter service
        auto filterService = make_shared<BloomFilterService>(filter, storageService);
        bool tryCreat = filterService->initialize();
        if (createdfromFile && !tryCreat) {
            return nullptr; // Error in initializing filter service
        }
        // create command processor
        auto commandProcessor = make_shared<CommandProcessor>(filterService);
        
        // Create and initialize application service
        auto appService = make_shared<ApplicationService>(commandProcessor);
    
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