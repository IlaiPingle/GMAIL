# ifndef INPUTMANAGER_H
# define INPUTMANAGER_H

#include <string>
#include <vector>
#include <memory>
#include "../bloom_Filter/bloomFilter.h"
#include "../bloom_Filter/hashable.h"
#include "../ioHandling/fileManager.h"
using namespace std; 
class inputManager {
    private:
        unique_ptr<bloomFilter> m_bloomFilter;
        unique_ptr<fileManager> m_fileManager;
        string runAddToBlacklist(const string& url);
        string runCheckBlacklist(const std::string& url);
        
    public:
        inputManager(unique_ptr<bloomFilter> bloomFilter,unique_ptr<fileManager> fileManager);
        string convertLine(const string& line);
        static unique_ptr<inputManager> initfirstLine(const string& Line);
        ~inputManager();     
};
#endif // INPUTMANAGER_H