// factorial
fun factorial(n) {
    if (n <= 1)
        return 1;
    return n * factorial(n - 1);
}

// main
{
    var i = 736;
    print "factorial(" + i + "): " + factorial(i);
}