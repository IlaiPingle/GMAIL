#include "../ioHandling/inputManager.h"
#include "../bloom_filter/bloomFilter.h"
#include "../services/FileStorageService.h"
#include "../bloom_filter/hashFactory.h"
#include <sstream>
InputManager::InputManager() : m_commandProcessor(nullptr){}

InputManager::InputManager(unique_ptr<CommandProcessor> commandProcessor) {
    if (commandProcessor) {
        m_commandProcessor = move(commandProcessor);
    } else {
        m_commandProcessor = make_unique<CommandProcessor>();
    }
}
    

string InputManager::processCommand(const string& command) {
    // Validate command format
    if (command.empty()) {
        return "400 Bad Request";
    }

    istringstream iss(command);
    string commandType;
    iss >> commandType;

    // Extract URL if present
    string url;
    getline(iss >> ws, url);

    // Process command
    if (commandType == "POST") {
        return m_commandProcessor->addToBlacklist(url);
    }
    else if (commandType == "GET") {
        return m_commandProcessor->checkBlacklist(url);
    }
    else if (commandType == "DELETE") {
        return m_commandProcessor->deleteFromBlacklist(url);
    }
    else {
        return "400 Bad Request";
    }
}
unique_ptr <InputManager> InputManager::createFromConfig(const string& configLine) {
    istringstream iss(configLine);
    size_t bitArraySize;
    vector <size_t> hashInfos;
    if (!(iss >> bitArraySize)) {
        return nullptr; // Invalid size
    }
    size_t hashId;
    while (iss >> hashId) {
        hashInfos.push_back(hashId);
    }
    if (hashInfos.empty()) {
        return nullptr; // Invalid hash function info
    }
    try{
        auto hashFunctions = hashFactory::createHashFunctions(hashInfos);
        unique_ptr<IBloomFilter> filter = make_unique<bloomFilter>(bitArraySize, hashFunctions);
        unique_ptr<IStorageService> storageService = make_unique<FileStorageService>();
        vector<bool> bits = filter->getBitArray();
        unordered_set<string> blackList;
        storageService->initializeFilter(bits, blackList);
        filter->setBitArray(bits);
        filter->setBlackList(blackList);
        unique_ptr<CommandProcessor> commandProcessor = make_unique<CommandProcessor>(move(filter), move(storageService));
        return make_unique<InputManager>(move(commandProcessor)); // Return the initialized InputManager
    }
    catch (const std::exception& e) {
        return nullptr; // Initialization failed
    }

    
}
    

