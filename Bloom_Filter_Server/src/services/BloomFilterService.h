#ifndef BLOOMFILTERSERVICE_H
#define BLOOMFILTERSERVICE_H
#include <memory>

#include "FileStorageService.h"
#include "../interfaces/IFilterService.h"
#include "../bloom_Filter/bloomFilter.h"
#include "../bloom_Filter/hashFactory.h"

using namespace std;

/**
 * @class BloomFilterService
 * @brief A service that manages a Bloom filter and its storage.
 *
 * This class provides methods to initialize the filter, add URLs to it,
 * remove URLs from it, and check if a URL is present in the filter.
 */
class BloomFilterService : public IFilterService {
private:
    shared_ptr<bloomFilter> m_bloomFilter;
    shared_ptr<FileStorageService> m_storageService;

public:
    BloomFilterService(shared_ptr<bloomFilter> filter, shared_ptr<FileStorageService> storage);
    bool initialize() override;
    bool add(const string& url) override;
    bool remove(const string& url) override;
    bool contains(const string& url) override;
    bool containsAbsolutely(const string& url) override;
};
#endif // BLOOMFILTERSERVICE_H