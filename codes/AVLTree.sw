class TreeNode {
    init(val, height, left, right) {
        this.val = val;
        this.height = height;
        this.left = left;
        this.right = right;
    }
}

fun max(x, y) => (x > y ? x : y);

class AVLTree {
    init() {
        this.root = null;
        this.sz = 0;
    }

    // TC: O(1)
    size() => this.sz;

    // TC: O(1)
    height() => this._height(this.root);

    // TC: O(n)
    printTree() => this._inorder(this.root);

    // TC: O(lg(n))
    insert(val) {
        this.root = this._insert(this.root, val);
    }

    // TC: O(lg(n))
    search(val) => this._search(this.root, val);

    // TC: O(lg(n))
    _search(node, val) {
        if (node == null)
            return false;
        if (node.val == val)
            return true;
        if (node.val < val)
            return this._search(node.right, val);
        return this._search(node.left, val);
    }

    // TC: O(lg(n))
    _insert(node, val) {
        if(node == null)
            return TreeNode(val, 1, null, null);
        if (node.val == val)
            return node;
        if (node.val > val) {
            node.left = this._insert(node.left, val);
            node.height = max(1 + this._height(node.left), this._height(node.right));
        }
        else {
            node.right = this._insert(node.right, val);
            node.height = max(this._height(node.left), 1 + this._height(node.right));
        }
        var bf = this._height(node.left) - this._height(node.right);
        print "BF of " + node.val + " :", bf;
        if (bf < -1) { // right side's height is more
            if (val < node.right.val)
                node.right = this._rightRotate(node.right);
            return this._leftRotate(node);
        }
        if (bf > 1) { // left side's height is more
            if (val > node.left.val)
                node.left = this._leftRotate(node.left);
            return this._rightRotate(node);
        }
        return node; // height is between -1 and 1.
    }

    // TC: O(1)
    _height(node) => node ? node.height : 0;

    // TC: O(1)
    _leftRotate(A) {
        var B = A.right;
        var X = A.left;
        var Y = B ? B.left : null;
        var Z = B ? B.right : null;
        var AHeight = 1 + max(this._height(X), this._height(Y));
        var BHeight = 1 + max(AHeight, this._height(Z));
        B.left = A;
        A.right = Y;
        B.height = BHeight;
        A.height = AHeight;
        return B;
    }

    // TC: O(1)
    _rightRotate(A) {
        var B = A.left;
        var X = B ? B.left : null;
        var Y = B ? B.right : null;
        var Z = A.right;
        var AHeight = 1 + max(this._height(Y), this._height(Z));
        var BHeight = 1 + max(AHeight, this._height(X));
        B.right = A;
        A.left = Y;
        B.height = BHeight;
        A.height = AHeight;
        return B;
    }

    // TC: O(n)
    _inorder(node) {
        if (node == null)
            return;
        this._inorder(node.left);
        print node.val;
        this._inorder(node.right);
    }
}

// main
{
    var a = AVLTree();
    for (var i = 10; i >= 0; --i) {
        a.insert(i);
        a.printTree();
        print "search(5)", a.search(5);
        print "============";
    }
    print a.height();
}