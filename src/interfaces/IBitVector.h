#ifndef IBITVECTOR_H
#define IBITVECTOR_H
#include <vector>
#include <cstddef>

/**
 * @brief Interface for a Bit Vector.
 * This interface defines the basic operations for a bit vector, including setting,
 * getting, and managing bits in the vector.
 */
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