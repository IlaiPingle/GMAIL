#ifndef BLOOMFILTERSERVICE_H
#define BLOOMFILTERSERVICE_H
#include "../interfaces/IFilterService.h"
#include "../interfaces/IStorageService.h"
#include "../interfaces/IBloomFilter.h"
#include "../bloom_Filter/hashFactory.h"
#include <memory>
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
    shared_ptr<IBloomFilter> m_bloomFilter;
    shared_ptr<IStorageService> m_storageService;
    shared_ptr<hashFactory> m_hashFactory;

public:
    BloomFilterService(shared_ptr<IBloomFilter> filter, shared_ptr<IStorageService> storage);
    bool initialize() override;
    bool add(const string& url) override;
    bool remove(const string& url) override;
};
#endif // BLOOMFILTERSERVICE_H