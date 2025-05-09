#ifndef COMMAND_PROCESSOR_H
#define COMMAND_PROCESSOR_H

#include <string>
#include <memory>
#include "../interfaces/IBloomFilter.h"
#include "../interfaces/IStorageService.h"
using namespace std;
class CommandProcessor {
private:
    unique_ptr<IBloomFilter> m_bloomFilter;
    unique_ptr<IStorageService> m_storageService;

public:
    CommandProcessor();
    CommandProcessor(std::unique_ptr<IBloomFilter> bloomFilter,
                    unique_ptr<IStorageService> storageService);
    
    string addToBlacklist(const string& url);
    string checkBlacklist(const string& url);
    string deleteFromBlacklist(const string& url);
};

#endif // COMMAND_PROCESSOR_H