#ifndef COMMAND_PROCESSOR_H
#define COMMAND_PROCESSOR_H

#include <string>
#include <memory>
#include "../interfaces/IBloomFilter.h"
#include "../interfaces/IStorageService.h"

class CommandProcessor {
private:
    std::unique_ptr<IBloomFilter> m_bloomFilter;
    std::unique_ptr<IStorageService> m_storageService;

public:
    CommandProcessor();
    CommandProcessor(std::unique_ptr<IBloomFilter> bloomFilter,
                    std::unique_ptr<IStorageService> storageService);
    
    std::string addToBlacklist(const std::string& url);
    std::string checkBlacklist(const std::string& url);
    std::string deleteFromBlacklist(const std::string& url);
};

#endif // COMMAND_PROCESSOR_H