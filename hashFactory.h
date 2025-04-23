#ifndef HASHFACTORY_H
#define HASHFACTORY_H

#include "hashable.h"
#include <vector>
#include <memory>
#include <string>

class HashFactory {
public:
    static std::pair<size_t, std::vector<std::shared_ptr<hashable>>>
    createFromConfigLine(const std::string& line);
};

#endif 