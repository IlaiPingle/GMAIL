#ifndef DEFAULTBITVECTOR_H
#define DEFAULTBITVECTOR_H
#include "../interfaces/IBitVector.h"
#include <vector>

class DefaultBitVector : public IBitVector {
private:
    std::vector<bool> m_bits;

public:
    DefaultBitVector(size_t size);
    void set(size_t index) override;
    bool get(size_t index) const override;
    size_t size() const override;
    std::vector<bool> getVector() const override;
    void setVector(const std::vector<bool>& bits) override;
};
#endif // DEFAULTBITVECTOR_H