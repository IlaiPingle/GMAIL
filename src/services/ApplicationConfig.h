#ifndef APPLICATIONCONFIG_H
#define APPLICATIONCONFIG_H
#include <memory>
#include <string>
#include <vector>
#include "../interfaces/IApplicationService.h"

class ApplicationConfig {
public:
    // Parse configuration and set up the application
    static std::shared_ptr<IApplicationService> configure(const std::string& configLine);
    
private:
    // Helper methods for configuration steps
    static std::vector<size_t> parseHashIds(const std::string& configLine, size_t& bitArraySize);
    static std::shared_ptr<IApplicationService> createApplicationService(
        size_t bitArraySize,
        const std::vector<size_t>& hashIds);
};
#endif // APPLICATIONCONFIG_H