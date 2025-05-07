#include <iostream>
#include <memory>
#include <sstream>
#include "src/services/ApplicationService.h"
#include "src/services/BloomFilterService.h"
#include "src/services/FileStorageService.h"
#include "src/utils/URLValidator.h"
#include "src/ioHandling/InputProcessor.h"
#include "src/bloom_Filter/bloomFilter.h"
#include "src/bloom_Filter/hashFactory.h"
#include "src/interfaces/IApplicationService.h"

int main() {
    try {
        // Create dependencies
        std::shared_ptr<IURLValidator> urlValidator = std::make_shared<URLValidator>();
        std::shared_ptr<IStorageService> storageService = std::make_shared<FileStorageService>();
        // Read initial configuration
        std::string configLine;
        std::getline(std::cin, configLine);
        std::istringstream iss(configLine);
        size_t bitArraySize;
        std::vector<size_t> hashIds;
        if (!(iss >> bitArraySize)) {
            std::cerr << "Invalid size" << std::endl;
            return 1; // Invalid size
        }
        size_t hashId;
        while (iss >> hashId) {
            hashIds.push_back(hashId);
        }
        if (hashIds.empty()) {
            std::cerr << "No hash functions specified" << std::endl;
            return 1; // Invalid hash function info
        }
        auto hashFunctions = hashFactory::createHashFunctions(hashIds);
        auto filter = std::make_shared<bloomFilter>(bitArraySize, hashFunctions);
        std::shared_ptr<IFilterService> filterService = std::make_shared<BloomFilterService>(
        std::move(filter),
        storageService
        );
        std::shared_ptr<IApplicationService> appService = std::make_shared<ApplicationService>(
        filterService,
        storageService,
        urlValidator
    );
    if (!appService->initialize(configLine)) {
        std::cerr << "Failed to initialize application service" << std::endl;
        return 1; // Initialization failed
    }
    // Create input processor
    InputProcessor processor(appService);
    // Process commands
    std::string line;
    while (std::getline(std::cin, line)) {
        if (line.empty()) continue;
        
        std::string result = processor.processCommandLine(line);
        if (!result.empty()) {
            std::cout << result << std::endl;
        }
    }
    return 0;
}
catch (const std::exception& e) {
        std::cerr << "Error: " << e.what() << std::endl;
        return 1; // Exception occurred
    }
}