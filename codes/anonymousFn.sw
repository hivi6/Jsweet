fun whichFn(fn) {
  print fn;
}

whichFn(fun (b) {
 print b;
});

fun named(a) { print a; }
whichFn(named);
//
// <fn>
// <fn named>