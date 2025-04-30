# ifndef INPUTMANAGER_H
# define INPUTMANAGER_H

#include <string>
#include <vector>
#include <memory>
#include "../bloomFilter/bloom_filter.h"
#include "../bloomFilter/hash_function.h"
#include "../ioHandling/file_manager.h"
using namespace std; 
class inputManager {
    private:
        unique_ptr<BloomFilter> bloomFilter;
        unique_ptr<FileManager> fileManager;

        
    public:
        inputManager(unique_ptr<BloomFilter> bloomFilter,unique_ptr<FileManager> fileManager);
        convertLine(const string& line);
        static unique_ptr<inputManager> initfirstLine(const string& Line);

    private:
        string runAddToBlacklist(const string& url);

        string runCheckBlacklist(const std::string& url);