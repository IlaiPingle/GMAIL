#ifndef APPLICATIONCONFIG_H
#define APPLICATIONCONFIG_H
#include <memory>
#include <string>
#include <vector>
#include "../interfaces/IApplicationService.h"
using namespace std;

class ApplicationConfig {
public:
    // Parse configuration and set up the application
    static shared_ptr<IApplicationService> configure(const string& configLine);
    
private:
    // Helper methods for configuration steps
    static vector<size_t> parseHashIds(const string& configLine, size_t& bitArraySize);
    static shared_ptr<IApplicationService> createApplicationService(
        size_t bitArraySize,
        const vector<size_t>& hashIds);
};
#endif // APPLICATIONCONFIG_H