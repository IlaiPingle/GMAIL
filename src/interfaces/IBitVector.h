#ifndef IBITVECTOR_H
#define IBITVECTOR_H
#include <vector>
#include <cstddef>

class IBitVector {
public:
    virtual ~IBitVector() = default;
    virtual void set(size_t index) = 0;
    virtual bool get(size_t index) const = 0;
    virtual size_t size() const = 0;
    virtual std::vector<bool> getVector() const = 0;
    virtual void setVector(const std::vector<bool>& bits) = 0;
};
#endif // IBITVECTOR_H