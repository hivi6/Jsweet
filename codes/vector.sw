class Vector {
    init() {
        this.cap = 0; // capacity          
        this.sz = 0;  // size of the Vector
        this.elems = array(0); // elements of the Vector
    }

    size() => this.sz; // get size of the Vector

    capacity() => this.cap; 

    get(i) => (0 <= i and i < this.sz ? this.elems.get(i) : null);

    set(i, elem) => (0 <= i and i < this.sz ? this.elems.set(i, elem) : null);

    front() => (this.sz ? this.get(0) : null);

    back() => (this.sz ? this.get(this.sz - 1) : null);

    assign(n, elem) {
        if (n < 0)
            return;
        this.cap = this.sz = n;
        this.elems = array(this.sz);
        for (var i = 0; i < this.size(); ++i)
            this.set(i, elem);
    }

    assignFn(n, fn) {
        if (n < 0)
            return;
        this.cap = this.sz = n;
        this.elems = array(this.sz);
        for (var i = 0; i < this.size(); ++i)
            this.set(i, fn(i));
    }

    assignV(v) {
        this.assignFn(v.size(), fun(i) => v.get(i));
    }

    push_back(elem) {
        ++this.sz;   
        if (this.cap < this.sz) {
            // change the capacity to 2* the size
            this.cap = this.sz * 2;
            
            // build a new array with the given capacity
            var temp = array(this.cap);

            // copy all the elements to this new array and this is the elems array now
            for (var i = 0; i < this.elems.length; ++i)
                temp.set(i, this.elems.get(i));
            this.elems = temp;
        }
        // put the element in the end
        this.elems.set(this.sz - 1, elem);
    }

    pop_back() {
        if (this.sz == 0)
            return;
        --this.sz;
    }

    push_front(elem) {
        // first push_back
        this.push_back(elem);

        // shift the elems by one to right and put the last element to the first
        var temp = this.back();
        for (var i = this.size() - 2; i >= 0; --i)
            this.set(i + 1, this.get(i));
        this.set(0, temp);
    }

    pop_back() {
        if (this.sz == 0)
            return;
        // shift left by one
        for (var i = 0; i < this.size() - 1; ++i)
            this.set(i, this.get(i + 1));
        --this.sz;
    }

    slice(lower, upper) { // slice the vector [lower, upper)
        if (lower > upper)
            return Vector();
        var res = Vector();
        res.assign(upper - lower, 0);
        for (var i = lower; i < upper; ++i)
            res.set(i - lower, this.get(i));
        return res;
    }

    copy() {
        return this.slice(0, this.size());
    }

    toString() {
        var res = "";
        for (var i = 0; i < this.size(); ++i) {
            if (i != 0)
                res += " ";
            res += "" + this.get(i);
        }
        return "[" + res + "]";
    }
}

// implimenting merge sort
fun merge_sort(v, l, r) {
    if (l >= r - 1)
        return;
    var mid = l + (r - l) / 2;
    merge_sort(v, l, mid);
    merge_sort(v, mid, r);

    // merge left and right side
    var left = v.slice(l, mid);
    var right = v.slice(mid, r);
    var i = 0, j = 0, k = l;
    while (i < left.size() and j < right.size()) {
        if (left.get(i) < right.get(j)) {
            v.set(k, left.get(i));
            ++i;
        } else {
            v.set(k, right.get(j));
            ++j;
        }
        ++k;
    }
    while (i < left.size()) {
        v.set(k, left.get(i));
        ++i;
        ++k;
    }
    while (j < right.size()) {
        v.set(k, right.get(j));
        ++j;
        ++k;
    }
}

// Testing
{
    var a = Vector();
    var n = 100000;
    a.assignFn(n, fun(i) =>  (n - i));
    var c = clock();
    merge_sort(a, 0, a.size());
    print clock() - c;
}