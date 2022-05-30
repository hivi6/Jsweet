fun fact(n) => n <= 0 ? 1 : n * fact(n - 1);

fun fib(n) => n <= 1 ? n : fib(n - 1) + fib(n - 2);

fun sum(n) => n <= 0 ? 0 : n + sum(n - 1);

// main
{
    var show = fun (fn, i) {
        print fn, "fn(" + i + "): " + fn(i);
    };

    for (var i = 0; i < 10; ++i) {
        show(fact, i);
        show(fib, i);
        show(sum, i);
    }
}