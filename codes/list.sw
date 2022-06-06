class ListNode {
    init(value, next) {
        this.value = value;
        this.next = next;
    }

    getValue() {
        return this.value;
    }

    setValue(value) {
        this.value = value;
    }

    getNext() {
        return this.next;
    }

    setNext(next) {
        this.next = next;
    }
}

class List {
    init() {
        this.sz = 0;
        this.head = this.tail = null;
    }

    front() => (this.sz == 0 ? null : this.head.getValue());

    back() => (this.sz == 0 ? null : this.tail.getValue());

    push_back(elem) {
        ++this.sz;
        if (this.sz == 1) {
            this.head = this.tail = ListNode(elem, null);
            return;
        }
        this.tail.setNext(ListNode(elem, null));
        this.tail = this.tail.getNext();
    }

    pop_back() {
        if (this.sz <= 0)
            return;
        --this.sz;
        if (this.sz == 0) {
            this.head = this.tail = null;
            return;
        }
        var temp = this.head;
        repeat(this.sz - 1)
            temp = temp.getNext();
        this.tail = temp;
        this.tail.setNext(null);
    }

    push_front(elem) {
        ++this.sz;
        if (this.sz == 1) {
            this.head = this.tail = ListNode(elem, null);
            return;
        }
        this.head = ListNode(elem, this.head);
    }

    pop_front() {
        if (this.sz <= 0)
            return;
        --this.sz;
        if (this.sz == 0) {
            this.head = this.tail = null;
            return;
        }
        this.head = this.head.getNext();
    }

    toString() {
        var res = "";
        var first = true;
        for (var now = this.head; now != null; now = now.getNext()) {
            if (!first)
                res += " ";
            res += now.getValue();
            first = false;
        }
        return "[" + res + "]";
    }
}

// main
{
    var l = List();
    for (var i = 0; i < 10; ++i) {
        l.push_front(i);
        l.push_back(i);
        print l.toString();
    }
    for (var i = 0; i < 10; ++i) {
        l.pop_front();
        print l.toString();
    }
    for (var i = 0; i < 10; ++i) {
        l.pop_back();
        print l.toString();
    }
}