/* Adaptação do arquivo BST.java do SW, para permitir visualização */

import java.util.NoSuchElementException;
import java.awt.Font;

public class BSTd<Key extends Comparable<Key>, Value> {
    private Node root;
    private Key lastKey;

    private class Node {
        private Key key;
        private Value val;
        private Node left, right;
        private int N;
        private double x, y;
        public Node(Key key, Value val, int N) {
            this.key = key;
            this.val = val;
            this.N = N;
        }
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public int size() {
        return size(root);
    }

    private int size(Node x) {
        if (x == null)
            return 0;
        else
            return x.N;
    }

    public boolean contains(Key key) {
        return get(key) != null;
    }

    public Value get(Key key) {
        return get(root, key);
    }

    private Value get(Node x, Key key) {
        if (x == null)
            return null;
        int cmp = key.compareTo(x.key);
        if (cmp < 0)
            return get(x.left, key);
        else if (cmp > 0)
            return get(x.right, key);
        else
            return x.val;
    }

    public void put(Key key, Value val) {
        lastKey = key;
        if (val == null) {
            delete(key);
            return;
        }
        root = put(root, key, val);
        assert check();
    }

    private Node put(Node x, Key key, Value val) {
        if (x == null)
            return new Node(key, val, 1);
        int cmp = key.compareTo(x.key);
        if (cmp < 0)
            x.left  = put(x.left,  key, val);
        else if (cmp > 0)
            x.right = put(x.right, key, val);
        else
            x.val   = val;
        x.N = 1 + size(x.left) + size(x.right);
        return x;
    }

    public void deleteMin() {
        if (isEmpty())
            throw new NoSuchElementException("Symbol table underflow");
        root = deleteMin(root);
        assert check();
    }

    private Node deleteMin(Node x) {
        if (x.left == null)
            return x.right;
        x.left = deleteMin(x.left);
        x.N = size(x.left) + size(x.right) + 1;
        return x;
    }

    public void deleteMax() {
        if (isEmpty())
            throw new NoSuchElementException("Symbol table underflow");
        root = deleteMax(root);
        assert check();
    }

    private Node deleteMax(Node x) {
        if (x.right == null)
            return x.left;
        x.right = deleteMax(x.right);
        x.N = size(x.left) + size(x.right) + 1;
        return x;
    }

    public void delete(Key key) {
        root = delete(root, key);
        assert check();
    }

    private Node delete(Node x, Key key) {
        if (x == null)
            return null;
        int cmp = key.compareTo(x.key);
        if (cmp < 0)
            x.left  = delete(x.left,  key);
        else if (cmp > 0)
            x.right = delete(x.right, key);
        else {
            if (x.right == null)
                return x.left;
            if (x.left  == null)
                return x.right;
            Node t = x;
            x = min(t.right);
            x.right = deleteMin(t.right);
            x.left = t.left;
        }
        x.N = size(x.left) + size(x.right) + 1;
        return x;
    }

    public Key min() {
        if (isEmpty())
            return null;
        return min(root).key;
    }

    private Node min(Node x) {
        if (x.left == null)
            return x;
        else
            return min(x.left);
    }

    public Key max() {
        if (isEmpty())
            return null;
        return max(root).key;
    }

    private Node max(Node x) {
        if (x.right == null)
            return x;
        else
            return max(x.right);
    }

    public Key floor(Key key) {
        Node x = floor(root, key);
        if (x == null)
            return null;
        else
            return x.key;
    }

    private Node floor(Node x, Key key) {
        if (x == null)
            return null;
        int cmp = key.compareTo(x.key);
        if (cmp == 0)
            return x;
        if (cmp <  0)
            return floor(x.left, key);
        Node t = floor(x.right, key);
        if (t != null)
            return t;
        else
            return x;
    }

    public Key ceiling(Key key) {
        Node x = ceiling(root, key);
        if (x == null)
            return null;
        else
            return x.key;
    }

    private Node ceiling(Node x, Key key) {
        if (x == null)
            return null;
        int cmp = key.compareTo(x.key);
        if (cmp == 0)
            return x;
        if (cmp < 0) {
            Node t = ceiling(x.left, key);
            if (t != null)
                return t;
            else
                return x;
        }
        return ceiling(x.right, key);
    }

    public Key select(int k) {
        if (k < 0 || k >= size())
            return null;
        Node x = select(root, k);
        return x.key;
    }

    private Node select(Node x, int k) {
        if (x == null)
            return null;
        int t = size(x.left);
        if (t > k)
            return select(x.left,  k);
        else if (t < k)
            return select(x.right, k - t - 1);
        else
            return x;
    }

    public int rank(Key key) {
        return rank(key, root);
    }

    private int rank(Key key, Node x) {
        if (x == null)
            return 0;
        int cmp = key.compareTo(x.key);
        if (cmp < 0)
            return rank(key, x.left);
        else if (cmp > 0)
            return 1 + size(x.left) + rank(key, x.right);
        else
            return size(x.left);
    }

    public Iterable<Key> keys() {
        return keys(min(), max());
    }

    public Iterable<Key> keys(Key lo, Key hi) {
        Queue<Key> queue = new Queue<Key>();
        keys(root, queue, lo, hi);
        return queue;
    }

    private void keys(Node x, Queue<Key> queue, Key lo, Key hi) {
        if (x == null)
            return;
        int cmplo = lo.compareTo(x.key);
        int cmphi = hi.compareTo(x.key);
        if (cmplo < 0)
            keys(x.left, queue, lo, hi);
        if (cmplo <= 0 && cmphi >= 0)
            queue.enqueue(x.key);
        if (cmphi > 0)
            keys(x.right, queue, lo, hi);
    }

    public int size(Key lo, Key hi) {
        if (lo.compareTo(hi) > 0)
            return 0;
        if (contains(hi))
            return rank(hi) - rank(lo) + 1;
        else
            return rank(hi) - rank(lo);
    }

    public int height() {
        return height(root);
    }
    private int height(Node x) {
        if (x == null)
            return -1;
        return 1 + Math.max(height(x.left), height(x.right));
    }

    public Iterable<Key> levelOrder() {
        Queue<Key> keys = new Queue<Key>();
        Queue<Node> queue = new Queue<Node>();
        queue.enqueue(root);
        while (!queue.isEmpty()) {
            Node x = queue.dequeue();
            if (x == null)
                continue;
            keys.enqueue(x.key);
            queue.enqueue(x.left);
            queue.enqueue(x.right);
        }
        return keys;
    }

    private boolean check() {
        if (!isBST())
            StdOut.println("Not in symmetric order");
        if (!isSizeConsistent())
            StdOut.println("Subtree counts not consistent");
        if (!isRankConsistent())
            StdOut.println("Ranks not consistent");
        return isBST() && isSizeConsistent() && isRankConsistent();
    }

    private boolean isBST() {
        return isBST(root, null, null);
    }

    private boolean isBST(Node x, Key min, Key max) {
        if (x == null)
            return true;
        if (min != null && x.key.compareTo(min) <= 0)
            return false;
        if (max != null && x.key.compareTo(max) >= 0)
            return false;
        return isBST(x.left, min, x.key) && isBST(x.right, x.key, max);
    }

    private boolean isSizeConsistent() {
        return isSizeConsistent(root);
    }
    private boolean isSizeConsistent(Node x) {
        if (x == null)
            return true;
        if (x.N != size(x.left) + size(x.right) + 1)
            return false;
        return isSizeConsistent(x.left) && isSizeConsistent(x.right);
    }

    private boolean isRankConsistent() {
        for (int i = 0; i < size(); i++)
            if (i != rank(select(i)))
                return false;
        for (Key key : keys())
            if (key.compareTo(select(rank(key))) != 0)
                return false;
        return true;
    }


    private final int h = 600;
    private final int w = 600;
    private double radius;
    private double lineHeight, columnWidth;
    private boolean init = false;

    public void draw() {
        if (!init) {
            StdDraw.setCanvasSize(w, h);
            StdDraw.setXscale(0, w);
            StdDraw.setYscale(0, h);
            init = true;
        } else
            StdDraw.clear();
        lineHeight = h * 1.0 / (height() + 1);
        columnWidth = w * 1.0 / (size());
        radius = Math.min(25.0, Math.min(0.4 * lineHeight, 0.4 * columnWidth));
        StdDraw.setFont(new Font(Font.SANS_SERIF, Font.PLAIN,
                                 (int)(radius * 16 / 25)));
        calculatePosition(root, height());
        drawTree(root);
    }

    private void calculatePosition(Node node, int level) {
        if (node == null)
            return;
        node.x = (rank(node.key) + 0.5) * columnWidth;
        node.y = (level + 0.5) * lineHeight;
        calculatePosition(node.left, level - 1);
        calculatePosition(node.right, level - 1);
    }

    public void drawTree(Node node) {
        if (node.key.equals(lastKey)) {
            double pr = StdDraw.getPenRadius();
            StdDraw.setPenRadius(3 * pr);
            StdDraw.circle(node.x, node.y, radius);
            StdDraw.setPenRadius(pr);
        }
        StdDraw.circle(node.x, node.y, radius);
        StdDraw.text(node.x, node.y, node.key.toString());
        if (node.left != null) {
            drawTree(node.left);
            double d = Math.sqrt((node.left.y - node.y) * (node.left.y - node.y)
                                 + (node.left.x - node.x) * (node.left.x - node.x));
            double dx = radius * (node.left.x - node.x) / d;
            double dy = radius * (node.left.y - node.y) / d;
            StdDraw.line(node.x + dx, node.y + dy,
                         node.left.x - dx, node.left.y - dy);
        }
        if (node.right != null) {
            drawTree(node.right);
            double d = Math.sqrt((node.right.y - node.y) * (node.right.y - node.y)
                                + (node.right.x - node.x) * (node.right.x - node.x));
            double dx = radius * (node.right.x - node.x) / d;
            double dy = radius * (node.right.y - node.y) / d;
            StdDraw.line(node.x + dx, node.y + dy,
                         node.right.x - dx, node.right.y - dy);
        }
    }

    public static void main(String[] args) {
        BSTd<String, Integer> st = new BSTd<String, Integer>();
        for (int i = 0; !StdIn.isEmpty(); i++) {
            String key = StdIn.readString();
            st.put(key, i);
        }
        for (String s : st.levelOrder())
            StdOut.println(s + " " + st.get(s));
        StdOut.println();
        for (String s : st.keys())
            StdOut.println(s + " " + st.get(s));
    }
}
