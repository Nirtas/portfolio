#ifndef LINEARCONGRUENTIALGENERATOR_H
#define LINEARCONGRUENTIALGENERATOR_H
//#include <vector>


class LinearCongruentialGenerator {
private:
    long long b;
    long long seed;
    long long c;
    long long m;
public:
    LinearCongruentialGenerator(long long, long long, long long, long long);
    long long getRandValue();
};



#endif //LINEARCONGRUENTIALGENERATOR_H
