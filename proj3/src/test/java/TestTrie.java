import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestTrie {
    private static final String OSM_DB_PATH = "data/berkeley-2018.osm.xml";
    private static GraphDB graph;

    @Before
    public void setUp() throws Exception {
        graph = new GraphDB(OSM_DB_PATH);
    }

    @Test
    public void testContains() {
        for (String name : graph.trie.cleanedNames) {
            assertTrue(name + "not in trie", graph.trie.contains(name));
        }
    }

    @Test
    public void testKeysWithPrefix() {
        for (String s : graph.trie.keysWithPrefix("the")) {
            System.out.println(s);
        }
    }

}
