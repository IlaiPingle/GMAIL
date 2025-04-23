
#include "repeatedHash.h"
#include <functional>
// constructor
repeatedHash::repeatedHash(size_t reps, size_t size)
    : m_repeats(reps), m_arraySize(size) {}

// implemnt hush multiple times 
size_t repeatedHash::operator()(const std::string& input) const {
    std::hash<std::string> hasher;
    size_t hash = hasher(input);
    for (size_t i = 1; i < m_repeats; ++i) {
        hash = hasher(std::to_string(hash));
    }
    return hash % m_arraySize;
}