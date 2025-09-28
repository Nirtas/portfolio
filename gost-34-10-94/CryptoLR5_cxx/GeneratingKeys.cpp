#include "GeneratingKeys.h"
#include "LinearCongruentialGenerator.h"
#include "Utils.h"
#include <map>

cpp_int procedureC(const cpp_int &p, const cpp_int &q) {
    cpp_int f = 0;
    cpp_int d = 1;
    do {
        f = calculateF(d, p, q);
        d += 1;
    } while (f == 1);
    return f;
}

cpp_int calculateF(const cpp_int &d, const cpp_int &p, const cpp_int &q) {
    if (q != 0) {
        return powm(d, (p - 1) / q, p);
    } else {
        throw std::invalid_argument("q must not be equal to 0");
    }
}

std::tuple<cpp_int, cpp_int> procedureB(long long b, long long x0, long long c, int modulePow) {
    namespace mp = boost::multiprecision;

    cpp_int q = 0, Q = 0;
    long long y0 = 0, y1 = 0;
    std::tie(std::ignore, q, y0) = procedureA(b, x0, c, modulePow, true);
    std::tie(Q, std::ignore, y1) = procedureA(b, y0, c, modulePow);

    int bitLength = 1024;
    long long one = 1;
    long long module = one << modulePow;

    cpp_int p = 0;

    LinearCongruentialGenerator generatorLC = LinearCongruentialGenerator(b, y1, c, module);

    int yCount = (modulePow == 16) ? 64 : 32;
    std::vector<long long> y(yCount + 1);
    y[0] = y1;

    while (true) {
    step3:
        for (int i = 0; i < yCount; i++) {
            y[i + 1] = generatorLC.getRandValue();
        }

        cpp_int Y = 0;
        for (int i = 0; i < yCount; i++) {
            Y += cpp_int(y[i]) * mp::pow(cpp_int(2), modulePow * i);
        }

        y[0] = y[yCount];

        cpp_bin_float_100 firstPartOfN = cpp_bin_float_100(
            mp::ceil(
                cpp_dec_float_100((mp::pow(cpp_int(2), bitLength - 1) / (q * Q)))
            )
        );

        cpp_bin_float_100 secondPartOfN = cpp_bin_float_100(
            mp::floor(
                cpp_dec_float_100(mp::pow(cpp_int(2), bitLength - 1) * Y / (q * Q * mp::pow(cpp_int(2), bitLength)))
            )
        );

        cpp_int N = static_cast<cpp_int>(firstPartOfN + secondPartOfN);

        if (N % cpp_int(2) == 1) {
            N += 1;
        }

        cpp_int k = 0;

        while (true) {
            p = q * Q * (N + k) + 1;
            if (p > pow(cpp_int(2), bitLength)) {
                goto step3;
            }
            if (powm(cpp_int(2), q * Q * (N + k), p) == 1 &&
                powm(cpp_int(2), q * (N + k), p) != 1
            ) {
                goto exit;
            } else {
                k += 2;
            }
        }
    }
exit:
    return {p, q};
}

std::tuple<cpp_int, cpp_int, long long> procedureA(long long b, long long x0, long long c, int modulePow, bool flagReturnFirstY) {
    namespace mp = boost::multiprecision;
    int bitLength = 512;
    long long one = 1;
    long long module = one << modulePow;

    std::vector<int> t;
    t.push_back(bitLength);

    int index = 0;

    while (t[index] > modulePow) {
        t.push_back(static_cast<int>(floor(t[index] / 2.0)));
        index++;
    }

    std::vector<cpp_int> p(t.size());
    p[index] = findSmallestPrimeNumber(t[index]);

    int m = index - 1;

    int rm = 0;

    std::vector<long long> y;
    y.push_back(x0);
    LinearCongruentialGenerator generatorLC = LinearCongruentialGenerator(b, y[0], c, module);
    long long y0 = 0;

step5:
    while (m >= 0) {
        rm = static_cast<int>(ceil(static_cast<double>(t[m]) / modulePow));
        while (true) {
        step6:
            y.resize(1);

            for (int i = 0; i < rm; i++) {
                y.push_back(generatorLC.getRandValue());
            }

            cpp_int Ym = 0;
            for (int i = 0; i < rm; i++) {
                Ym += cpp_int(y[i]) * mp::pow(cpp_int(2), modulePow * i);
            }

            y0 = y[0];
            y[0] = y[rm];

            cpp_bin_float_100 firstPartOfN = cpp_bin_float_100(
                mp::ceil(
                    cpp_dec_float_100(mp::pow(cpp_int(2), t[m] - 1)) /
                    cpp_dec_float_100(p[m + 1])
                )
            );

            cpp_bin_float_100 secondPartOfN = cpp_bin_float_100(
                mp::floor(
                    cpp_dec_float_100(mp::pow(cpp_int(2), t[m] - 1) * Ym) /
                    cpp_dec_float_100(p[m + 1] * mp::pow(cpp_int(2), modulePow * rm))
                )
            );

            cpp_int N = static_cast<cpp_int>(firstPartOfN + secondPartOfN);

            if (N % cpp_int(2) == 1) {
                N += 1;
            }

            cpp_int k = 0;

            while (true) {
                p[m] = p[m + 1] * (N + k) + 1;
                if (p[m] > mp::pow(cpp_int(2), t[m])) {
                    goto step6;
                }
                if (myPowm(cpp_int(2), p[m + 1] * cpp_int(N + k), p[m]) == 1 &&
                    myPowm(cpp_int(2), cpp_int(N + k), p[m]) != 1
                ) {
                    m--;
                    goto step5;
                } else {
                    k += 2;
                }
            }
        }
    }

    if (flagReturnFirstY) {
        return {p[0], p[1], y0};
    } else {
        return {p[0], p[1], y[0]};
    }
}

std::map<int, cpp_int> smallestPrimeNumbers = {
    std::pair{16, cpp_int("32771")},
    std::pair{32, cpp_int("2147483659")},
    std::pair{64, cpp_int("9223372036854775837")},
    std::pair{128, cpp_int("170141183460469231731687303715884105757")},
    std::pair{256, cpp_int("57896044618658097711785492504343953926634992332820282019728792003956564820063")},
    std::pair{
        512,
        cpp_int(
            "6703903964971298549787012499102923063739682910296196688861780721860882015036773488400937149083451713845015929093243025426876941405973284973216824503042159")
    },
    std::pair{
        1024,
        cpp_int(
            "89884656743115795386465259539451236680898848947115328636715040578866337902750481566354238661203768010560056939935696678829394884407208311246423715319737062188883946712432742638151109800623047059726541476042502884419075341171231440736956555270413618581675255342293149119973622969239858152417678164812112069763")
    }
};

cpp_int findSmallestPrimeNumber(const int bitLength) {
    if (smallestPrimeNumbers.find(bitLength) != smallestPrimeNumbers.end()) {
        return smallestPrimeNumbers.find(bitLength)->second;
    }
    std::string string = "1";
    for (int i = 1; i < bitLength; i++) {
        string += "0";
    }
    return nextPrime(1 << bitLength);
}

bool isPrime(const cpp_int &number) {
    if (number == 2 || number == 3)
        return true;

    if (number % 2 == 0 || number % 3 == 0)
        return false;

    int divisor = 6;
    while (divisor * divisor - 2 * divisor + 1 <= number) {
        if (number % (divisor - 1) == 0)
            return false;

        if (number % (divisor + 1) == 0)
            return false;

        divisor += 6;
    }

    return true;
}

cpp_int nextPrime(const cpp_int &N) {
    cpp_int a = N;
    while (!isPrime(++a)) {
    }
    return a;
}
