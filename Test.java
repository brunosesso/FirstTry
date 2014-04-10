/* HAHAHA */

public class Test {

    public static void showUsage() {
        StdOut.println("Utilize um dos seguintes argumentos: BST ou 23T ou RBBST");
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            showUsage();
            return;
        }
        if (args[0].toUpperCase().equals("BST")) {
            BSTd<String, Integer> st = new BSTd<String, Integer>();
            while (!StdIn.isEmpty()) {
                st.put(StdIn.readString(), 0);
                st.draw();
            }
        } else {
            boolean tt = false;
            if (args[0].toUpperCase().equals("23T"))   // árvore 2-3
                tt = true;
            else if (!args[0].toUpperCase().equals("RBBST")) {  // erro
                showUsage();
                return;
            }
            RedBlackBSTd<String, Integer> st = new RedBlackBSTd<String, Integer>();
            while (!StdIn.isEmpty()) {
                st.put(StdIn.readString(), 0);
                st.draw(tt);
            }
        }
    }
}
