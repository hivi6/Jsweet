// TC: O(n)
fun fact(n) => n <= 0 ? 1 : n * fact(n - 1);

// TC: O(2^n)
fun fib(n) => n <= 1 ? n : fib(n - 1) + fib(n - 2);

// TC: O(n)
fun sum(n) => n <= 0 ? 0 : n + sum(n - 1);

fun sumFn(n, fn) {
    var res = 0;
    for (var i = 1; i <= n; i++) {
        res += fn(i);
    }
    return res;
}

// TC: O(n)
fun pow(x, y) => y == 0 ? 1 : x * pow(x, y - 1);

// TC: O(lg(n))
fun fastpow(x, y) => (y == 0 ? 1 : (y % 2 == 0 ? fastpow(x * x, y / 2) : fastpow(x, y - 1) * x));

// main
{
    for (var i = 0; i <= 10; ++i) {
        print fact, "fact(" + i + "): " + fact(i);
        print fib, "fib(" + i + "): " + fib(i);
        print sum, "sum(" + i + "): " + sum(i);
        print pow, "pow(" + i + ", " + 12 + "): " + pow(i, 12);
        print fastpow, "fastpow(" + i + ", " + 12 + "): " + fastpow(i, 12);
        print "==========================";
    }
}