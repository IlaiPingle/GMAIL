#include "hashFactory.h"
#include "repeatedHash.h"
#include <sstream>

std::pair<size_t, std::vector<std::shared_ptr<hashable>>>
HashFactory::createFromConfigLine(const std::string& line) {
    std::istringstream iss(line);
    size_t arraySize = 0, numFunctions = 0;
    iss >> arraySize >> numFunctions;

    std::vector<std::shared_ptr<hashable>> hashFunctions;
    size_t reps;
    for (size_t i = 0; i < numFunctions && iss >> reps; ++i) {
        hashFunctions.push_back(std::make_shared<repeatedHash>(reps, arraySize));
    }

    return { arraySize, hashFunctions };
}
