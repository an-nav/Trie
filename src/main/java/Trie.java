import java.util.LinkedList;
import java.util.Queue;

/**
 * 字典树 Trie
 * @author anvna
 */
public class Trie<Value> {
    // length of ASCII
    private static final int R = 256;
    private Node root;

    private static class Node{
        // java 没有泛型数组
        private Object val;
        private Node[] next = new Node[R];
    }

    public Value get(String key){
        Node node = get(root, key, 0);
        return node == null ? null : (Value) node.val;
    }

    /**
     * 在以 node 为根结点的子树中查询 字符串 key 所对应的 value
     * @param node 查询根节点
     * @param key  查询的字符串 key
     * @param d    查询至第 d 个 字符
     * @return     所查询 key 的末尾字符结点 或 null
     */
    private Node get(Node node, String key, int d){
        if( node == null ){
            return null;
        }
        if( d == key.length() ){
            return node;
        }
        char c = key.charAt(d);
        return get(node.next[c], key, d + 1);
    }

    public void put(String key, Value value){
        root = put(root, key, value, 0);
    }

    /**
     * 在以 node 为根结点的子树中插入 key 与其对应的 value，若 key 已经完整存在则修改 value
     * 若 key 不完成则补全 key
     * @param node  插入的根节点
     * @param key   插入的 key
     * @param value key 对应的 value
     * @param d     插入 key 对应的字符位置
     * @return 当前递归层的 node 结点
     */
    private Node put(Node node, String key, Value value, int d){
        if( node == null ){
            return new Node();
        }
        if( d == key.length() ){
            node.val = value;
            return node;
        }
        char c = key.charAt(d);
        node.next[c] = put(node.next[c], key, value, d + 1);
        return node;
    }

    /**
     * 查询字典树中的所有 key
     * @return iterable[String]
     */
    public Iterable<String> keys(){
        return keysWithPrefix("");
    }

    /**
     * 查询以 prefix 为前缀的所有字符串
     * @param prefix 前缀字符串
     * @return iterable[String]
     */
    public Iterable<String> keysWithPrefix(String prefix){
        Queue<String> queue = new LinkedList<String>();
        collect(get(root, prefix, 0), prefix,queue);
        return queue;
    }

    /**
     * 在以 node 为根节点的子字典树中查找以 pre 为前缀的所有字符串, node 所对应的 key 为 pre 的最后一个字符
     * @param node   查询起始根节点
     * @param pre    前缀字符串
     * @param queue  结果队列
     */
    private void collect(Node node, String pre, Queue<String> queue){
        if( node == null ){
            return;
        }
        if( node.val != null ){
            queue.offer(pre);
        }

        for (char c = 0; c < R; c++){
            collect(node.next[c], pre + c, queue);
        }
    }

    /**
     * 查询所有满足 pattern 模式的字符串
     * @param pattern 通配模式
     * @return iterable[String]
     */
    public Iterable<String> keysThatMatch(String pattern){
        Queue<String> queue = new LinkedList<String>();
        collect(root, "", pattern, queue);
        return queue;
    }

    /**
     *  "." 通配符匹配方法 "." 表示任意一个字符 例如 c.a 匹配诸如 cba ccd 等
     * @param node 匹配开始的根节点
     * @param pre  前缀
     * @param pat  统配模式
     * @param queue 结果队列
     */
    private void collect(Node node, String pre, String pat, Queue<String> queue){
        int d = pre.length();
        if ( node == null ){
            return;
        }
        if ( d == pat.length() && node.val != null ){
            queue.offer(pre);
            return;
        }
        if ( d == pat.length() ){
            return;
        }
        char next = pat.charAt(d);
        for ( char c = 0; c < R; c++ ){
            // 遇到通配符"." 则检查所有 R 个字符；或检查指定的字符 c
            if( next == '.' || next == c ){
                collect(node.next[c], pre + c, pat, queue);
            }
        }
    }

    /**
     * 获取字符串 s 在字典树中的最长前缀
     * @param s 字符串 s
     * @return 最长前缀
     */
    public String getLongestPrefixOf(String s){
        int index = searchLength(root, s, 0, 0);
        return s.substring(0, index);
    }

    /**
     * 在以 node 为根节点的子树中查找 字符串 s 的最长前缀的长度
     * @param node 子树根节点
     * @param s    查找的字符串 s
     * @param d    第 d 个字符
     * @param length  最长前缀的长度
     * @return     最长前缀的长度
     */
    private int searchLength(Node node, String s, int d, int length){
        if ( node == null ){
            return length;
        }
        if ( node.val != null ){
            length = d;
        }
        if ( s.length() == length ){
            return length;
        }
        char c = s.charAt(d);
        return searchLength(node.next[c], s + c, d + 1, length);
    }


    public void delete(String key){
        root = delete(root, key, 0);
    }

    /**
     * 在以 node 为根节点的字典树中删除 key，若 key 的最后一个字符之后还有字符则直接将 val 置为 null 然后返回
     * 若置为 null 后 最后一个字符的所有子链接都为空则将该节点也删除，并递归向上父节点也执行此操作
     * @param node 子树的根节点
     * @param key  待删除的 key
     * @param d    key 的第 d 个字符
     * @return     当前递归层节点
     */
    private Node delete(Node node, String key, int d){
        if ( node == null ){
            return null;
        }
        // 找到目标 key 的结尾
        if ( d == key.length() ){
            node.val = null;
        }else{
            char c = key.charAt(d);
            node.next[c] = delete(node.next[c], key, d + 1);
        }
        // 如果 node 是一个单词的结束则直接返回
        if ( node.val != null ){
            return node;
        }
        // 查询 node 的所有子节点如果有不为 null 的说明不用删除
        for ( char c = 0; c < R; c++ ){
            if (node.next[c] != null){
                return node;
            }
        }
        // node 的所有子节点都为 null 因此将此节点删除
        return null;
    }
}
