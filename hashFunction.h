#include <string>
#include "hashable.h"

class hashFunction : virtual hashable {
    private:
        // Private members and methods
    public:

    hashFunction();
    
    int hushString(std::string key, int seed); 
        
};