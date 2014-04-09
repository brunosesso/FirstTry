/* Adaptação do arquivo RedBlackBST.java do SW, para permitir visualização */

import java.util.NoSuchElementException;
import java.awt.Font;

public class RedBlackBSTd<Key extends Comparable<Key>, Value> {

    private static final boolean RED   = true;
    private static final boolean BLACK = false;

    private Node root;
    private Key lastKey;

    private class Node {
        private Key key;
        private Value val;
        private Node left, right;
        private boolean color;
        private int N;
        private double x, y;
        public Node(Key key, Value val, boolean color, int N) {
            this.key = key;
            this.val = val;
            this.color = color;
            this.N = N;
        }
    }

    private boolean isRed(Node x) {
        if (x == null)
            return false;
        return (x.color == RED);
    }

    private int size(Node x) {
        if (x == null)
            return 0;
        return x.N;
    }

    public int size() {
        return size(root);
    }

    public boolean isEmpty() {
        return root == null;
    }

    public Value get(Key key) {
        return get(root, key);
    }

    private Value get(Node x, Key key) {
        while (x != null) {
            int cmp = key.compareTo(x.key);
            if (cmp < 0)
                x = x.left;
            else if (cmp > 0)
                x = x.right;
            else
                return x.val;
        }
        return null;
    }

    public boolean contains(Key key) {
        return (get(key) != null);
    }

    private boolean contains(Node x, Key key) {
        return (get(x, key) != null);
    }

    public void put(Key key, Value val) {
        lastKey = key;
        root = put(root, key, val);
        root.color = BLACK;
        assert check();
    }

    private Node put(Node h, Key key, Value val) {
        if (h == null)
            return new Node(key, val, RED, 1);
        int cmp = key.compareTo(h.key);
        if (cmp < 0)
            h.left  = put(h.left,  key, val);
        else if (cmp > 0)
            h.right = put(h.right, key, val);
        else
            h.val   = val;
        if (isRed(h.right) && !isRed(h.left))
            h = rotateLeft(h);
        if (isRed(h.left)  &&  isRed(h.left.left))
            h = rotateRight(h);
        if (isRed(h.left)  &&  isRed(h.right))
            flipColors(h);
        h.N = size(h.left) + size(h.right) + 1;
        return h;
    }

    /*************************************************************************
     *  Red-black deletion
     *************************************************************************/

    // delete the key-value pair with the minimum key
    public void deleteMin() {
        if (isEmpty())
            throw new NoSuchElementException("BST underflow");
        if (!isRed(root.left) && !isRed(root.right))
            root.color = RED;
        root = deleteMin(root);
        if (!isEmpty())
            root.color = BLACK;
        assert check();
    }

    private Node deleteMin(Node h) {
        if (h.left == null)
            return null;
        if (!isRed(h.left) && !isRed(h.left.left))
            h = moveRedLeft(h);
        h.left = deleteMin(h.left);
        return balance(h);
    }

    public void deleteMax() {
        if (isEmpty())
            throw new NoSuchElementException("BST underflow");
        if (!isRed(root.left) && !isRed(root.right))
            root.color = RED;
        root = deleteMax(root);
        if (!isEmpty())
            root.color = BLACK;
        assert check();
    }

    private Node deleteMax(Node h) {
        if (isRed(h.left))
            h = rotateRight(h);
        if (h.right == null)
            return null;
        if (!isRed(h.right) && !isRed(h.right.left))
            h = moveRedRight(h);
        h.right = deleteMax(h.right);
        return balance(h);
    }

    public void delete(Key key) {
        if (!contains(key)) {
            System.err.println("symbol table does not contain " + key);
            return;
        }
        if (!isRed(root.left) && !isRed(root.right))
            root.color = RED;
        root = delete(root, key);
        if (!isEmpty())
            root.color = BLACK;
        assert check();
    }

    private Node delete(Node h, Key key) {
        assert contains(h, key);
        if (key.compareTo(h.key) < 0)  {
            if (!isRed(h.left) && !isRed(h.left.left))
                h = moveRedLeft(h);
            h.left = delete(h.left, key);
        } else {
            if (isRed(h.left))
                h = rotateRight(h);
            if (key.compareTo(h.key) == 0 && (h.right == null))
                return null;
            if (!isRed(h.right) && !isRed(h.right.left))
                h = moveRedRight(h);
            if (key.compareTo(h.key) == 0) {
                Node x = min(h.right);
                h.key = x.key;
                h.val = x.val;
                h.right = deleteMin(h.right);
            } else
                h.right = delete(h.right, key);
        }
        return balance(h);
    }

    private Node rotateRight(Node h) {
        assert(h != null) && isRed(h.left);
        Node x = h.left;
        h.left = x.right;
        x.right = h;
        x.color = x.right.color;
        x.right.color = RED;
        x.N = h.N;
        h.N = size(h.left) + size(h.right) + 1;
        return x;
    }

    private Node rotateLeft(Node h) {
        assert(h != null) && isRed(h.right);
        Node x = h.right;
        h.right = x.left;
        x.left = h;
        x.color = x.left.color;
        x.left.color = RED;
        x.N = h.N;
        h.N = size(h.left) + size(h.right) + 1;
        return x;
    }

    private void flipColors(Node h) {
        assert(h != null) && (h.left != null) && (h.right != null);
        assert(!isRed(h) &&  isRed(h.left) &&  isRed(h.right))
        || (isRed(h)  && !isRed(h.left) && !isRed(h.right));
        h.color = !h.color;
        h.left.color = !h.left.color;
        h.right.color = !h.right.color;
    }

    private Node moveRedLeft(Node h) {
        assert(h != null);
        assert isRed(h) && !isRed(h.left) && !isRed(h.left.left);
        flipColors(h);
        if (isRed(h.right.left)) {
            h.right = rotateRight(h.right);
            h = rotateLeft(h);
        }
        return h;
    }

    private Node moveRedRight(Node h) {
        assert(h != null);
        assert isRed(h) && !isRed(h.right) && !isRed(h.right.left);
        flipColors(h);
        if (isRed(h.left.left))
            h = rotateRight(h);
        return h;
    }

    private Node balance(Node h) {
        assert(h != null);
        if (isRed(h.right))
            h = rotateLeft(h);
        if (isRed(h.left) && isRed(h.left.left))
            h = rotateRight(h);
        if (isRed(h.left) && isRed(h.right))
            flipColors(h);
        h.N = size(h.left) + size(h.right) + 1;
        return h;
    }

    public int height() {
        return height(root);
    }
    private int height(Node x) {
        if (x == null)
            return -1;
        return 1 + Math.max(height(x.left), height(x.right));
    }

    public Key min() {
        if (isEmpty())
            return null;
        return min(root).key;
    }

    private Node min(Node x) {
        assert x != null;
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
        assert x != null;
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
        if (cmp < 0)
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
        if (cmp > 0)
            return ceiling(x.right, key);
        Node t = ceiling(x.left, key);
        if (t != null)
            return t;
        else
            return x;
    }

    public Key select(int k) {
        if (k < 0 || k >= size())
            return null;
        Node x = select(root, k);
        return x.key;
    }

    private Node select(Node x, int k) {
        assert x != null;
        assert k >= 0 && k < size(x);
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

    private boolean check() {
        if (!isBST())
            StdOut.println("Not in symmetric order");
        if (!isSizeConsistent())
            StdOut.println("Subtree counts not consistent");
        if (!isRankConsistent())
            StdOut.println("Ranks not consistent");
        if (!is23())
            StdOut.println("Not a 2-3 tree");
        if (!isBalanced())
            StdOut.println("Not balanced");
        return isBST() && isSizeConsistent() && isRankConsistent() && is23()
               && isBalanced();
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

    private boolean is23() {
        return is23(root);
    }
    private boolean is23(Node x) {
        if (x == null)
            return true;
        if (isRed(x.right))
            return false;
        if (x != root && isRed(x) && isRed(x.left))
            return false;
        return is23(x.left) && is23(x.right);
    }

    private boolean isBalanced() {
        int black = 0;
        Node x = root;
        while (x != null) {
            if (!isRed(x))
                black++;
            x = x.left;
        }
        return isBalanced(root, black);
    }

    private boolean isBalanced(Node x, int black) {
        if (x == null)
            return black == 0;
        if (!isRed(x))
            black--;
        return isBalanced(x.left, black) && isBalanced(x.right, black);
    }



    private final int h = 600;
    private final int w = 600;
    private double radius;
    private int treeHeight, columns;
    private double lineHeight, columnWidth;
    private boolean init = false;

    private int blackHeight() {
        return height(root);
    }
    private int blackHeight(Node x) {
        if (x == null)
            return -1;
        return (isRed(x) ? 0 : 1) + Math.max(height(x.left), height(x.right));
    }


    public void draw(boolean twothree) {
        if (!init) {
            StdDraw.setCanvasSize(w, h);
            StdDraw.setXscale(0, w);
            StdDraw.setYscale(0, h);
            init = true;
        } else
            StdDraw.clear();
        treeHeight = blackHeight();
        lineHeight = h * 1.0 / (treeHeight + 1);
        columnWidth = w * 1.0 / (size());
        radius = Math.min(25.0, Math.min(0.4 * lineHeight, 0.4 * columnWidth));
        StdDraw.setFont(new Font(Font.SANS_SERIF, Font.PLAIN,
                                 (int)(radius * 16 / 25)));
        if (twothree) {
            calculate23Position(root, treeHeight);
            draw23Tree(root);
        } else {
            calculatePosition(root, treeHeight);
            drawRBTree(root);
        }
    }

    private void calculatePosition(Node node, int level) {
        if (node == null)
            return;
        node.x = (rank(node.key) + 0.5) * columnWidth;
        node.y = (level + 0.5) * lineHeight;
        calculatePosition(node.left, level - 1);
        calculatePosition(node.right, level - 1);
    }

    private void calculate23Position(Node node, int level) {
        if (node == null)
            return;
        if (isRed(node.left)) {
            if (node.left.right != null) {
                node.x = (rank(node.left.right.key) + 0.5) * columnWidth;
                if (!isRed(node.left.right.left))
                    node.x += radius;
            } else
                node.x = (rank(node.key) + rank(node.left.key) + 1)
                    * columnWidth / 2;
        } else
            node.x = (rank(node.key) + 0.5) * columnWidth;
        node.y = (level + 0.5) * lineHeight;
        if (node.left != null && isRed(node.left)) {
            calculate23Position(node.left.left, level - 1);
            calculate23Position(node.left.right, level - 1);
            node.left.x = node.x - 2 * radius;
            node.left.y = node.y;
        } else
            calculate23Position(node.left,  level - 1);
        calculate23Position(node.right, level - 1);
    }

    public void drawRBTree(Node node) {
        if (isRed(node))
            StdDraw.setPenColor(255, 0, 0);
        if (node.key.equals(lastKey)) {
            double pr = StdDraw.getPenRadius();
            StdDraw.setPenRadius(3 * pr);
            StdDraw.circle(node.x, node.y, radius);
            StdDraw.setPenRadius(pr);
        }
        StdDraw.circle(node.x, node.y, radius);
        StdDraw.text(node.x, node.y, node.key.toString());
        StdDraw.setPenColor(0, 0, 0);
        if (node.left != null) {
            drawRBTree(node.left);
            double d = Math.sqrt((node.left.y - node.y) * (node.left.y - node.y)
                                 + (node.left.x - node.x) * (node.left.x - node.x));
            double dx = radius * (node.left.x - node.x) / d;
            double dy = radius * (node.left.y - node.y) / d;
            if (isRed(node.left))
                StdDraw.setPenColor(255, 0, 0);
            StdDraw.line(node.x + dx, node.y + dy,
                         node.left.x - dx, node.left.y - dy);
            StdDraw.setPenColor(0, 0, 0);
        }
        if (node.right != null) {
            drawRBTree(node.right);
            double d = Math.sqrt((node.right.y - node.y) * (node.right.y - node.y)
                                 + (node.right.x - node.x) * (node.right.x - node.x));
            double dx = radius * (node.right.x - node.x) / d;
            double dy = radius * (node.right.y - node.y) / d;
            if (isRed(node.right))
                StdDraw.setPenColor(255, 0, 0);
            StdDraw.line(node.x + dx, node.y + dy,
                         node.right.x - dx, node.right.y - dy);
            StdDraw.setPenColor(0, 0, 0);
        }
    }

    public void draw23Tree(Node node) {
        double pr = StdDraw.getPenRadius();
        if (node == null)
            return;
        if (node.key.equals(lastKey))
            StdDraw.setPenRadius(3 * pr);
        if (isRed(node.left)) {
            StdDraw.arc(node.x, node.y, radius, -90, 90);
            StdDraw.line(node.x, node.y + radius, node.x - radius, node.y + radius);
            StdDraw.line(node.x, node.y - radius, node.x - radius, node.y - radius);
            StdDraw.setPenRadius(pr);
            draw23Tree(node.left);
            draw23Tree(node.right);
        } else if (isRed(node)) {
            StdDraw.arc(node.x, node.y, radius, 90, 270);
            StdDraw.line(node.x, node.y + radius, node.x + radius, node.y + radius);
            StdDraw.line(node.x, node.y - radius, node.x + radius, node.y - radius);
            StdDraw.setPenRadius(pr);
        } else {
            StdDraw.circle(node.x, node.y, radius);
            StdDraw.setPenRadius(pr);
        }
        if (node.left != null && !isRed(node.left)) {
            draw23Tree(node.left);
            double d = Math.sqrt((node.left.y - node.y) * (node.left.y - node.y)
                                 + (node.left.x - node.x) * (node.left.x - node.x));
            double dx = radius * (node.left.x - node.x) / d;
            double dy = radius * (node.left.y - node.y) / d;
            StdDraw.line(node.x + dx, node.y + dy,
                         node.left.x - dx, node.left.y - dy);
        }
        if (node.right != null) {
            double x1 = node.right.x;
            double y1 = node.right.y;
            double x0 = node.x;
            double y0 = node.y;
            int c0 = 1;
            int c1 = 1;
            if (isRed(node.right.left)) {
                x1 -= radius;
                y1 += radius;
                c1 = 0;
            }
            if (isRed(node)) {
                x0 += radius;
                y0 -= radius;
                c0 = 0;
            }
            draw23Tree(node.right);
            double d = Math.sqrt((node.right.y - node.y) * (node.right.y - node.y)
                                + (node.right.x - node.x) * (node.right.x - node.x));
            double dx = radius * (node.right.x - node.x) / d;
            double dy = radius * (node.right.y - node.y) / d;
            StdDraw.line(x0 + c0 * dx, y0 + c0 * dy,
                         x1 - c1 * dx, y1 - c1 * dy);
        }
        StdDraw.text(node.x, node.y, node.key.toString());
    }

    public static void main(String[] args) {
        RedBlackBSTd<String, Integer> st = new RedBlackBSTd<String, Integer>();
        for (int i = 0; !StdIn.isEmpty(); i++) {
            String key = StdIn.readString();
            st.put(key, i);
        }
        for (String s : st.keys())
            StdOut.println(s + " " + st.get(s));
        StdOut.println();
    }
}
