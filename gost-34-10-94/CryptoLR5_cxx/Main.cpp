/*
	Вариант 19. Реализовать алгоритм электронной цифровой подписи ГОСТ Р 34.10-94.
	При постановке подписи использовать самостоятельно реализованную хеш-функцию MD4.

	Алгоритмически важные элементы, такие как тесты простоты, расширенный алгоритм Евклида, быстрое возведение в
	степень, нахождение первообразного корня, нахождение мультипликативного обратного по модулю и т.д должны быть
	реализованы самостоятельно.

	В системе должна быть реализована возможность выбора длины ключа шифрования.

	Ключи в системе должны генерироваться автоматически: случайным образом или по паролю.

	В работе можно использовать готовые реализации библиотеки больших чисел (числа больше, чем long int).
*/

#define _CRT_SECURE_NO_WARNINGS

#include <vector>
#include <iostream>
#include <random>
#include "MD4.h"
#include "GeneratingKeys.h"
#include "Utils.h"

cpp_int p = 0;
cpp_int q = 0;
cpp_int a = 0;
cpp_int y = 0;

void generateKeys(int pBitLength, int modulePow) {
    std::cout << "KEYS GENERATING" << std::endl;

    std::random_device rd;
    std::mt19937_64 eng(rd());
    std::uniform_int_distribution<long long> distr;

    long long x0 = 0;
    long long c = 0;
    cpp_int p1 = 0, q1 = 0;

    if (modulePow == 16) {
        long long pow2_16 = 65536;
        x0 = 1 + distr(eng) % pow2_16;
        do {
            c = 1 + distr(eng) % pow2_16;
        } while (c % 2 == 0);
        if (pBitLength == 512) {
            std::tie(p, q, std::ignore) = procedureA(19381, x0, c, 16);
        } else if (pBitLength == 1024) {
            std::tie(p, q) = procedureB(19381, x0, c, 16);
        }
    } else if (modulePow == 32) {
        long long pow2_32 = 4294967296;
        x0 = 1 + distr(eng) % pow2_32;
        do {
            c = 1 + distr(eng) % pow2_32;
        } while (c % 2 == 0);
        if (pBitLength == 512) {
            std::tie(p, q, std::ignore) = procedureA(97781173, x0, c, 32);
        } else if (pBitLength == 1024) {
            std::tie(p, q) = procedureB(97781173, x0, c, 32);
        }
    }

    std::cout << "x0: " << std::hex << x0 << std::dec << std::endl;
    std::cout << "c: " << std::hex << c << std::dec << std::endl;

    a = procedureC(p, q);
}

cpp_int randBigInteger(const cpp_int& maxValue) {
    std::random_device rd;
    std::mt19937_64 eng(rd());
    std::uniform_int_distribution<long long> distr;

    int bitLength = 1 + distr(eng) % msb(maxValue);
    std::string string = to_string(maxValue);
    cpp_int bigInteger = 0;
    do {
        bigInteger = 0;
        for (int i = 0; i < bitLength; i++) {
            bigInteger = (bigInteger << 1) + distr(eng);
        }
    } while (bigInteger >= q);
    return bigInteger;
}

std::tuple<cpp_int, cpp_int> sign(const cpp_int& x, const cpp_int& hash) {
    std::cout << "SIGNING" << std::endl;
    cpp_int h = hash;
    if (h % q == 0) {
        h = 1;
    }

    cpp_int k = 0;
    cpp_int r1 = 0;
    cpp_int r2 = 0;
    cpp_int s = 0;

    do {
        do {
            k = randBigInteger(q);
            r1 = myPowm(a, k, p);
            r2 = r1 % q;
            std::cout << "k: " << std::hex << k << std::dec << std::endl;
        } while (r2 == 0);
        s = (x * r2 + k * h) % q;
    } while (s == 0);

    return {r2, s};
}

cpp_int calculatePublicKey(const cpp_int& x) {
    return myPowm(a, x, p);
}

cpp_int toBigInteger(const std::vector<unsigned int> &vector) {
    cpp_int result = 0;
    for (unsigned int element: vector) {
        result = (result << 32) + element;
    }
    return result;
}

bool verify(const std::string& message, const std::tuple<cpp_int, cpp_int>& sign) {
    std::cout << "VERIFICATION" << std::endl;

    cpp_int r2 = std::get<0>(sign);
    cpp_int s = std::get<1>(sign);

    if (!(0 < r2 && r2 < q) || !(0 < s && s < q)) {
        std::cout << "Invalid size" << std::endl;
        return false;
    }

    cpp_int hash = toBigInteger(MD4().calculateHash(message));
    if (hash % q == 0) {
        hash = 1;
    }

    cpp_int v = myPowm(hash, q - 2, q);
    cpp_int z1 = (s * v) % q;
    cpp_int z2 = ((q - r2) * v) % q;
    cpp_int u = (myPowm(a, z1, p) * myPowm(y, z2, p)) % p % q;

    std::cout << "v: " << std::hex << v << std::dec << std::endl;
    std::cout << "z1: " << std::hex << z1 << std::dec << std::endl;
    std::cout << "z2: " << std::hex << z2 << std::dec << std::endl;
    std::cout << "u: " << std::hex << u << std::dec << std::endl;

    return r2 == u;
}

int main() {
    char choice = '0';
    int pBitLength = 0;
    do {
        std::cout << "p bit length. Enter 1 - 512, 2 - 1024: " << std::endl;
        std::cin >> choice;
        switch (choice) {
            case '1': {
                pBitLength = 512;
                break;
            }
            case '2': {
                pBitLength = 1024;
                break;
            }
            default: {
                pBitLength = 0;
                break;
            }
        }
    } while (pBitLength == 0);

    int modulePow = 0;
    do {
        std::cout << "module pow. Enter 1 - 16, 2 - 32: " << std::endl;
        std::cin >> choice;
        switch (choice) {
            case '1': {
                modulePow = 16;
                break;
            }
            case '2': {
                modulePow = 32;
                break;
            }
            default: {
                modulePow = 0;
                break;
            }
        }
    } while (modulePow == 0);

    std::cout << std::endl;

    generateKeys(pBitLength, modulePow);
    std::cout << "p: " << std::hex << p << std::dec << std::endl;
    std::cout << "q: " << std::hex << q << std::dec << std::endl;
    std::cout << "a: " << std::hex << a << std::dec << std::endl;

    std::cout << std::endl;

    cpp_int x = cpp_int("0x123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0");
    std::cout << "x: " << std::hex << x << std::dec << std::endl;

    y = calculatePublicKey(x);
    std::cout << "y: " << std::hex << y << std::dec << std::endl;

    std::cout << std::endl;

    std::cout << "Enter your message: " << std::endl;
    std::string message;
    std::getline(std::cin >> std::ws, message);

    cpp_int hash = toBigInteger(MD4().calculateHash(message));
    std::cout << std::hex << hash << std::dec << std::endl;

    std::cout << std::endl;

    cpp_int r2 = 0, s = 0;
    std::tie(r2, s) = sign(x, hash);
    r2 = 1;
    //s = 1;
    std::cout << "sign: r2 = " << std::hex << r2 << ", s = " << s << std::dec << std::endl;

    std::cout << std::endl;

    std::cout << std::boolalpha << verify(message, {r2, s}) << std::endl;

    return 0;
}
