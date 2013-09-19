#include "libfibonacci.h"

extern int64_t LB_fib_recursive(int64_t n) {
    return n <= 0 ? 0 : n == 1 ? 1 : LB_fib_recursive(n - 1) + LB_fib_recursive(n - 2);
}

extern int64_t LB_fib_iterative(int64_t n) {
    int64_t previous = -1;
    int64_t result = 1;
    int64_t i;
    int64_t sum;
    for (i = 0; i <= n; i++) {
        sum = result + previous;
        previous = result;
        result = sum;
    }
    return result;
}
