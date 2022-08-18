class TreeNode {
    init(val, left, right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }
}

class BinaryTree {
    init(X) {
        this.root = null;
        this.sz = 0;
        if (X != null) {
            for (var i = 0; i < X.length; ++i)
                this.insert(X.get(i));
        }
    }

    size() => this.sz;

    insert(val) {
        ++this.sz;
        this.root = this._insert(this.root, val);
    }

    _insert(node, val) {
        if (node == null)
            return TreeNode(val, null, null);
        if (node.val >= val) {
            node.left = this._insert(node.left, val);
        } else {
            node.right = this._insert(node.right, val);
        }
        return node;
    }

    printTree() {
        this._preorder(this.root);
    }

    _preorder(node) {
        if (node == null) return;
        print node.val;
        this._preorder(node.left);
        this._preorder(node.right);
    }
}

// main
{
    var b = BinaryTree(null);
    b.insert(5);
    b.insert(7);
    b.insert(3);
    b.insert(8);
    b.insert(4);
    b.insert(6);
    b.insert(2);
    b.printTree();
    print b.size();
}