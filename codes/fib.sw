// main
{
    var n = 10, a = 1, b = 0;
    for (var i = 0; i < n; i = i + 1) {
        var temp = a + b;
        b = a;
        a = temp;
    }
    print "fib(" + n + "):", a;
}