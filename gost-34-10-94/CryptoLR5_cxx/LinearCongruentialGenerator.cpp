#include "LinearCongruentialGenerator.h"
#include "Utils.h"

LinearCongruentialGenerator::LinearCongruentialGenerator(long long b, long long seed, long long c, long long m) {
    this->b = b;
    this->seed = seed;
    this->c = c;
    this->m = m;
}

long long LinearCongruentialGenerator::getRandValue() {
    long long value = mod(b * seed + c, m);
    seed = value;
    return value;
}
