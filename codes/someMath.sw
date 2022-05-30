fun fact(n) {
    var res = 1;
    for (var i = 1; i <= n; ++i)
        res *= i;
    return res;
}

fun fib(n) {
    if (n <= 1)
        return n;
    return fib(n - 1) + fib(n - 2);
}

// main
{
    var show = fun (fn, i) => fn + "(" + i + "): " + fn(i);   
    for (var i = 0; i < 10; ++i)
        print show(fact, i);
}