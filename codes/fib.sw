fun fib(n) {
    if (n <= 1)
        return n;
    return fib(n - 1) + fib(n - 2);
}

// main
{
    for (var i = 0; i < 100; i = i + 1) {
        print "fib(" + i + "): " + fib(i); 
    }
}