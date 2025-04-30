#ifndef REPEATEDHASH_H
#define REPEATEDHASH_H


#include <string>
#include "hashable.h"

class repeatedHash : public hashable {
    
    private:
    size_t m_repeats;
    
    public:
    // Constructor
    repeatedHash(size_t repeats); 

    // Destructor
    ~repeatedHash() override = default;

    // implemented the pure virtual function from hashable
    size_t operator()(const std::string& str) const override ;
};

#endif // REPEATEDHASH_H