#ifndef BLOOMFILTERSERVICE_H
#define BLOOMFILTERSERVICE_H
#include "../interfaces/IFilterService.h"
#include "../interfaces/IStorageService.h"
#include "../interfaces/IBloomFilter.h"
#include <memory>

class BloomFilterService : public IFilterService {
private:
    std::shared_ptr<IBloomFilter> m_bloomFilter;
    std::shared_ptr<IStorageService> m_storageService;

public:
    BloomFilterService(std::shared_ptr<IBloomFilter> filter, std::shared_ptr<IStorageService> storage);
    bool initialize();
    bool add(const std::string& url) override;
    bool contains(const std::string& url) override;
    bool containsAbsolutely(const std::string& url) override;
};
#endif // BLOOMFILTERSERVICE_H