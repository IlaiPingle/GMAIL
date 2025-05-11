#ifndef BLOOMFILTERSERVICE_H
#define BLOOMFILTERSERVICE_H
#include "../interfaces/IFilterService.h"
#include "../interfaces/IStorageService.h"
#include "../interfaces/IBloomFilter.h"
#include <memory>
using namespace std;
class BloomFilterService : public IFilterService {
private:
    shared_ptr<IBloomFilter> m_bloomFilter;
    shared_ptr<IStorageService> m_storageService;

public:
    BloomFilterService(shared_ptr<IBloomFilter> filter, shared_ptr<IStorageService> storage);
    bool initialize() override;
    bool add(const string& url) override;
    bool contains(const string& url) override;
    bool containsAbsolutely(const string& url) override;
    bool remove(const string& url) override;
};
#endif // BLOOMFILTERSERVICE_H