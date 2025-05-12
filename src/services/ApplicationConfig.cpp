#include "ApplicationConfig.h"
#include <sstream>
#include "../services/ApplicationService.h"
#include "../services/BloomFilterService.h"
#include "../services/FileStorageService.h"
#include "../utils/URLValidator.h"
#include "../utils/DefaultURLFormatter.h"
#include "../utils/DefaultURLNormalizer.h"
#include "../utils/DefaultBitVector.h"
#include "../bloom_Filter/bloomFilter.h"
#include "../bloom_Filter/hashFactory.h"

std::shared_ptr<IApplicationService> ApplicationConfig::configure(const std::string& configLine) {
    size_t bitArraySize = 0;
    std::vector<size_t> hashIds = parseHashIds(configLine, bitArraySize);
    
    if (hashIds.empty() || bitArraySize == 0) {
        return nullptr;
    }
    
    return createApplicationService(bitArraySize, hashIds);
}

std::vector<size_t> ApplicationConfig::parseHashIds(const std::string& configLine, size_t& bitArraySize) {
    std::istringstream iss(configLine);
    std::vector<size_t> hashIds;
    
    if (!(iss >> bitArraySize)) {
        return hashIds; // Empty vector indicates error
    }
    
    size_t hashId;
    while (iss >> hashId) {
        hashIds.push_back(hashId);
    }
    
    return hashIds;
}

std::shared_ptr<IApplicationService> ApplicationConfig::createApplicationService(
    size_t bitArraySize,
    const std::vector<size_t>& hashIds) {
    
    // Create URL validator components
    auto urlFormatter = std::make_shared<DefaultURLFormatter>();
    auto urlNormalizer = std::make_shared<DefaultURLNormalizer>();
    auto urlValidator = std::make_shared<URLValidator>(urlFormatter, urlNormalizer);
    
    // Create storage service
    auto storageService = std::make_shared<FileStorageService>();
    
    // Create bloom filter components
    auto hashFunctions = hashFactory::createHashFunctions(hashIds);
    auto bitVector = std::make_shared<DefaultBitVector>(bitArraySize);
    auto filter = std::make_shared<bloomFilter>(bitArraySize, hashFunctions, bitVector);
    
    // Create filter service
    auto filterService = std::make_shared<BloomFilterService>(filter, storageService);
    
    // Create and initialize application service
    auto appService = std::make_shared<ApplicationService>(filterService, storageService, urlValidator);
    appService->initialize("");  // We've already configured everything
    
    return appService;
}