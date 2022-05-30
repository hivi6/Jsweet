fun repeatFn(len, fn) {
    for (var i = 0; i < len; i = i + 1) {
        fn(i, len);
    }
}

// main
{
    repeatFn (10, fun (i, len) {
        print "Hello world", i, len;
    });
}