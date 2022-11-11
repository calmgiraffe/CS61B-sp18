import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

public class Trie {

    // Inner class for Node in the trie
    private static class Node {
        private boolean isKey;
        private final HashMap<Character, Node> next;

        private Node(boolean isKey) {
            this.isKey = isKey;
            this.next = new HashMap<>(32);
        }
    }

    // Instance variables and constructor for trie
    private final Node root;
    protected Set<String> cleanedNames;
    private final HashMap<String, String> fullNames;

    public Trie() {
        this.root = new Node(false);
        this.cleanedNames = new HashSet<>();
        this.fullNames = new HashMap<>();
    }

    /** Add a string to the trie after cleaning it up, leaving only spaces and lowercase. */
    public void add(String s) {
        // traverse tree, putting mappings in next Node
        String originalString = String.valueOf(s);
        s = GraphDB.cleanString(s);

        // immediately return if length 0
        if (s.length() == 0) {
            return;
        }
        Node curr = root;
        for (int i = 0; i < s.length() - 1; i += 1) {
            char ch = s.charAt(i);

            // made new Node if none already there
            if (curr.next.get(ch) == null) {
                curr.next.put(ch, new Node(false));
            }
            curr = curr.next.get(ch);
        }
        // Make the last char the key. If no node, make a new one. Else, make isKey = true
        char lastCh = s.charAt(s.length() - 1);
        if (curr.next.get(lastCh) == null) {
            curr.next.put(lastCh, new Node(true));
        } else {
            curr.next.get(lastCh).isKey = true;
        }
        fullNames.put(s, originalString);
    }

    public boolean contains(String s) {
        s = GraphDB.cleanString(s);
        Node curr = root;
        for (int i = 0; i < s.length() - 1; i += 1) {
            char ch = s.charAt(i);

            // immediately return false if no match
            if (curr.next.get(ch) == null) {
                return false;
            }
            // else, make next node curr
            curr = curr.next.get(ch);
        }
        // if there is a mapping for the last char AND is it a key, return true
        char lastCh = s.charAt(s.length() - 1);
        if (curr.next.get(lastCh) != null) {
            return curr.next.get(lastCh).isKey;
        }
        return false;
    }


    private void collect(String s, List<String> keys, Node n) {
        if (n.isKey) {
            keys.add(s);
        }
        for (char c : n.next.keySet()) {
            collect(s + c, keys, n.next.get(c));
        }
    }

    /** Given an input string s, return a list of all the strings that have s as the prefix. */
    public List<String> keysWithPrefix(String s) {
        // New empty lists where strings will be stored
        ArrayList<String> keys = new ArrayList<>();
        ArrayList<String> values = new ArrayList<>();

        // Navigate to last char of s in the trie
        s = GraphDB.cleanString(s);
        Node curr = root;
        for (int i = 0; i < s.length(); i += 1) {
            char ch = s.charAt(i);

            // immediately break if no match
            if (curr.next.get(ch) == null) {
                break;
            }
            curr = curr.next.get(ch);
        }
        /* Note: ketSet() returns empty list if no keys
         * Iterate through keys (chars) and recursively add to s. */
        for (char c : curr.next.keySet()) {
            collect(s + c, keys, curr.next.get(c));
        }

        for (String key : keys) {
            values.add(fullNames.get(key));
        }
        return values;
    }
}
