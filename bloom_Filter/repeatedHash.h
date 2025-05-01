#ifndef REPEATEDHASH_H
#define REPEATEDHASH_H

#include <functional>
#include <string>
#include "hashable.h"

class repeatedHash : public hashable {
    
    private:
    size_t m_repeats;
    
    public:
    // Constructors
    repeatedHash();
    repeatedHash(size_t repeats);
    repeatedHash(const repeatedHash& other) = default; // Copy constructor 

    // Destructor
    ~repeatedHash() override = default;

    // implemented the pure virtual function from hashable
    size_t operator()(const std::string& str) const override ;
};

#endif // REPEATEDHASH_H