#include "DefaultBitVector.h"

DefaultBitVector::DefaultBitVector(size_t size) : m_bits(size, false) {}

void DefaultBitVector::set(size_t index) {
    if (index < m_bits.size()) {
        m_bits[index] = true;
    }
}

bool DefaultBitVector::get(size_t index) const {
    if (index < m_bits.size()) {
        return m_bits[index];
    }
    return false;
}

size_t DefaultBitVector::size() const {
    return m_bits.size();
}

vector<bool> DefaultBitVector::getVector() const {
    return m_bits;
}

void DefaultBitVector::setVector(const vector<bool>& bits) {
    m_bits = bits;
}