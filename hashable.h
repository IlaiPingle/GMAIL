#include <string>

class hashable {
        public:
            // Constructor
            hashable() {}
            
            // Destructor
            virtual ~hashable() {}
            
            // Pure virtual function to be implemented by derived classes
            virtual int hashString(std::string key, int seed) = 0;
            
            // Pure virtual function to be implemented by derived classes
            virtual bool isEqual(std::string key1, std::string key2) = 0;
};