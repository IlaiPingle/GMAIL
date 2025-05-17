#ifndef APPLICATIONCONFIG_H
#define APPLICATIONCONFIG_H
#include <memory>
#include <string>
#include <vector>
#include <sstream>
#include <fstream>
#include "ApplicationService.h"
#include "BloomFilterService.h"
#include "FileStorageService.h"

#include "../bloom_Filter/bloomFilter.h"


using namespace std;

/**
* @class ApplicationConfig
* @brief A class responsible for configuring the application.
* it parses the configuration line, creates the necessary services from file if it exists, or creates them from the command line.
*/
class ApplicationConfig {
    public:
    // Parse configuration and set up the application
    static shared_ptr<ApplicationService> configure(const string& configLine);
    
    private: 
    
    static const string m_configFilePath;
    // Helper methods for configuration steps
    static vector<size_t> parseHashIds(const string& configLine, size_t& bitArraySize);
    static shared_ptr<ApplicationService> createApplicationService(
        size_t bitArraySize,
        const vector<size_t>& hashIds, const bool& createdfromFile); 
    static bool configFromFile(string& configLine) ;
    static bool saveConfigLine(const string& configLine);
    };
    #endif // APPLICATIONCONFIG_H