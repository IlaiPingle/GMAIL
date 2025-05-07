#include "../ioHandling/inputManager.h"
#include "../bloom_Filter/bloomFilter.h"
#include "../services/FileStorageService.h"
#include "../bloom_Filter/hashFactory.h"
#include <sstream>
enum class Command {
    Add = 1,
    Check = 2,
    Delete = 3
};
InputManager::InputManager() : m_commandProcessor(nullptr){}

InputManager::InputManager(unique_ptr<CommandProcessor> commandProcessor) 
    : m_commandProcessor(move(commandProcessor)) {}

string InputManager::processCommand(const string& line) {
    istringstream iss(line);
    int command;
    
    if (line.empty()) {
        return ""; // Invalid command format
    }
        
    if (!(iss >> command)) {
       return "Error: Invalid command"; // Invalid command
    }

    // Extract URL and trim leading whitespace
    string url;
    getline(iss >> ws, url);
        
    switch (command){
        case static_cast<int>(Command::Add): {
            return m_commandProcessor->addToBlacklist(url);
        }
        case static_cast<int>(Command::Check): {
            return m_commandProcessor->checkBlacklist(url);
        }
        /*case static_cast<int>(Command::Delete): {
            return m_commandProcessor->deleteFromBlacklist(url);
        }*/
        default:
            return "Error: Unknown command"; // Unknown command
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
    

