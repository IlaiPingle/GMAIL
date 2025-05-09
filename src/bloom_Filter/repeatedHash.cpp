#include "repeatedHash.h"
//default constructor
repeatedHash::repeatedHash() : m_repeats(1) {}
// constructor
repeatedHash::repeatedHash(size_t repeats)
    : m_repeats(repeats) {}

// implemnt hush multiple times 
size_t repeatedHash::operator()(const string& input) const {
    hash<string> hasher;
    size_t hash = hasher(input);
    for (size_t i = 1; i < m_repeats; ++i) {
        hash = hasher(to_string(hash));
    }
    return hash;
} 