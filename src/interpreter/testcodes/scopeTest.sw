var x = "global";
print x;
{
  var x = "inside";
  print x, x = "print";
}
print x;