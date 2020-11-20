import java.util.LinkedList;
import java.util.Queue;

/**
 * 三向单词查找树，解决 R 向单词查找树占用空间太大的问题
 */
public class TST <Value>{
    private Node root;

    private class Node{
        char c;
        Node left, mid, right;
        Value val;

        public Node(char c) {
            this.c = c;
        }
    }

    public Value get(String key){
        Node node = get(root, key, 0);
        return node == null ? null : node.val;
    }

    /**
     * 三向字典树查找
     * @param node
     * @param key
     * @param d
     * @return
     */
    private Node get(Node node, String key, int d){
        if( node == null ){
            return null;
        }
        char c = key.charAt(d);
        if ( c < node.c ){
            return get(node.left, key, d);
        }else if( c > node.c ){
            return get(node.right, key, d);
        }else if(d < key.length() - 1){
            return get(node.mid, key, d + 1);
        }else {
            return node;
        }
    }

    public void put(String key, Value value){
        root = put(root, key, value, 0);
    }

    /**
     * 三向字典树添加方法，根据三向的特征进行添加
     * @param node
     * @param key
     * @param value
     * @param d
     * @return
     */
    private Node put(Node node, String key, Value value, int d){
        char c = key.charAt(d);
        if (node == null){
            node = new Node(c);
        }
        if (c < node.c){
            node.left = put(node.left, key, value, d);
        }else if( c > node.c){
            node.right = put(node.right, key, value, d);
        }else if ( d < key.length() - 1){
            node.mid = put(node.mid, key, value, d + 1);
        }else{
            node.val = value;
        }
        return node;
    }

    public Iterable<String> keys(){
        Queue<String> queue = new LinkedList<String>();
        collect(root, new StringBuilder(), queue);
        return queue;
    }

    public Iterable<String> keysWithPrefix(String prefix){
        Queue<String> queue = new LinkedList<String>();
        Node node = get(root, prefix, 0);
        if (node == null) {
            return queue;
        }
        if (node.val != null){
            queue.offer(prefix);
        }
        collect(node, new StringBuilder(prefix), queue);
        return queue;
    }

    private void collect(Node node, StringBuilder prefix, Queue<String> queue){
        if( node == null ){
            return;
        }
        collect(node.left, prefix, queue);
        if (node.val != null){
            queue.offer(prefix.append(node.c).toString());
        }
        // 只有取中子树的情况下才会选择当前结点的字符
        collect(node.mid, prefix.append(node.c), queue);
        prefix.deleteCharAt(prefix.length() - 1);
        collect(node.right, prefix, queue);
    }

    public Iterable<String> keysThatMath(String pattern){
        Queue<String> queue = new LinkedList<String>();
        collect(root,new StringBuilder(),pattern, queue, 0);
        return queue;
    }

    /**
     * 通配符 ”.“ collect 方法
     * @param node
     * @param prefix
     * @param pattern
     * @param queue
     * @param d
     */
    private void collect(Node node, StringBuilder prefix, String pattern, Queue<String> queue, int d){
        if (node == null){
            return;
        }
        char c = pattern.charAt(d);
        if (c == '.' || c < node.c){
            collect(node.left, prefix, pattern, queue, d);
        }
        if (c == '.' || c == node.c){
            if (d == pattern.length() - 1 && node.val != null){
                queue.offer(prefix.append(node.c).toString());
            }
            if (d < pattern.length() - 1){
                collect(node.mid, prefix.append(c), pattern, queue, d + 1);
                prefix.deleteCharAt(prefix.length() - 1);
            }
        }
        if (c == '.' || c > node.c){
            collect(node.right, prefix, pattern, queue, d);
        }
    }

    public String longestPrefixOf(String s){
        int index = searchLength(root, s, 0, 0) + 1;
        return s.substring(0, index);
    }


    private int searchLength(Node node, String s, int d, int length){
        if (node == null){
            return length;
        }
        if (s.length() == d){
            return length;
        }
        char c = s.charAt(d);
        if (c < node.c){
            return searchLength(node.left, s, d, length);
        }else if (c > node.c){
            return searchLength(node.right, s ,d, length);
        }else{
            if (node.val != null){
                length = d;
            }
            return searchLength(node.mid, s, d + 1, length);
        }
    }

    // public String longestPrefixOf(String query) {
    //     if (query == null) {
    //         throw new IllegalArgumentException("calls longestPrefixOf() with null argument");
    //     }
    //     if (query.length() == 0) return null;
    //     int length = 0;
    //     Node x = root;
    //     int i = 0;
    //     while (x != null && i < query.length()) {
    //         char c = query.charAt(i);
    //         if      (c < x.c) x = x.left;
    //         else if (c > x.c) x = x.right;
    //         else {
    //             i++;
    //             if (x.val != null) length = i;
    //             x = x.mid;
    //         }
    //     }
    //     return query.substring(0, length);
    // }

    public static void main(String[] args) {
        TST<Integer> tst = new TST<Integer>();
        tst.put("app",1);
        tst.put("applic",2);
        tst.put("appl",3);
        System.out.println(tst.longestPrefixOf("application"));
    }
}
