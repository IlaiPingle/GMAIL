#ifndef HASHABLE_H
#define HASHABLE_H
#include <string>

class hashable {
    public:
        // Destructor
        virtual ~hashable() = default;         
        // function that must be implemented by derived classes
        // take input string and return a hash value
        virtual size_t operator()(const std::string& str) const = 0;
               
};
#endif // HASHABLE_H