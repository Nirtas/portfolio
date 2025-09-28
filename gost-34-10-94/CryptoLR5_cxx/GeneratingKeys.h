#ifndef GENERATINGKEYS_H
#define GENERATINGKEYS_H
#include <boost/multiprecision/cpp_int.hpp>
#include <boost/multiprecision/cpp_bin_float.hpp>
#include <boost/multiprecision/cpp_dec_float.hpp>
#include <boost/multiprecision/number.hpp>

using namespace boost::multiprecision;


cpp_int procedureC(const cpp_int&, const cpp_int&);

cpp_int calculateF(const cpp_int&, const cpp_int&, const cpp_int&);

std::tuple<cpp_int, cpp_int> procedureB(long long, long long, long long, int);

std::tuple<cpp_int, cpp_int, long long> procedureA(long long, long long, long long, int, bool = false);

cpp_int findSmallestPrimeNumber(int);

bool isPrime(const cpp_int&);

cpp_int nextPrime(const cpp_int&);


#endif //GENERATINGKEYS_H
