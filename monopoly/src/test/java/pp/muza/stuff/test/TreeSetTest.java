package pp.muza.stuff.test;

import org.junit.jupiter.api.Test;

import pp.muza.stuff.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

class TreeSetTest {

    // TODO: implement tests

    @Test
    void test() {
        TreeSet<String> tree = new TreeSet<>();

        // test insert

        assertEquals(0, tree.getNodes().size());
        assertTrue(tree.insert("root1"));
        assertEquals(1, tree.getNodes().size(), "node must be added");
        assertTrue(tree.insert("root2"));
        assertEquals(2, tree.getNodes().size(), "node must be added");
        assertFalse(tree.insert("root1"), "duplicate value must not be added");
        assertEquals(2, tree.getNodes().size(), "duplicate node must not be added");
        assertTrue(tree.insert("root1", "child1"));
        assertEquals(3, tree.getNodes().size());
        assertTrue(tree.insert("root1", "child2"));
        assertEquals(4, tree.getNodes().size());
        assertFalse(tree.insert("root1", "child1"), "duplicate value must not be added");
        assertEquals(4, tree.getNodes().size(), "duplicate node must not be added");
        assertNotNull(tree.find("root1"), "root1 must be found");
        assertNotNull(tree.find("root2"), "root1 must be found");
        assertEquals(2, tree.find("root1").getChildren().size(), "root1 must have 2 children");
        assertEquals(0, tree.find("root2").getChildren().size(), "root2 does not have children");
        assertEquals("root1", tree.find("child1").getParent(), "child1 must have root1 as parent");
        assertEquals("root1", tree.find("child2").getParent(), "child2 must have root1 as parent");

        // test move
        tree.insert("child2", "child3");
        assertEquals(5, tree.getNodes().size());

        assertTrue(tree.insert("root2", "child2"));
        assertEquals(5, tree.getNodes().size());

        assertEquals(1, tree.find("root1").getChildren().size(), "root1 must have 1 children");
        assertEquals(1, tree.find("root2").getChildren().size(), "root2 must have 1 children");
        assertEquals("root1", tree.find("child1").getParent(), "child1 must have root1 as parent");
        assertEquals("root2", tree.find("child2").getParent(), "child2 must have root2 as parent");

        assertTrue(tree.insert("child2"), "move child2 to root");
        assertNull(tree.find("child2").getParent(), "child2 must have no parent");
        assertTrue(tree.insert("child1", "child3"), "move child3 to child1");
        assertEquals("child1", tree.find("child3").getParent(), "child3 must have child1 as parent");

        // setups
        tree.insert("root1", "child1");
        tree.insert("root2", "child2");
        tree.insert("child2", "child3");
        assertFalse(tree.delete("child4"), "child4 does not exist");
        assertEquals(5, tree.getNodes().size(), "child4 does not exist");
        assertEquals("child2", tree.find("child3").getParent(), "child3 must have child2 as parent");

        // test delete
        tree.delete("child2");
        assertEquals("root2", tree.find("child3").getParent(), "child1 must have root1 as parent");

    }

    @Test
    void found() {
        TreeSet<String> tree = new TreeSet<>();
        assertNotNull(tree.getRoot(), "Root must not be null");
        assertEquals(tree.getRoot(), tree.find(null), "Root can be found by null");

        tree.insert("root1");
        tree.insert("root1", "child1");
        tree.insert("root1", "child2");
        assertThrows(NullPointerException.class, () -> {
            tree.insert(null, null);
        });

        assertEquals(tree.getRoot(), tree.find(null), "Root can be found by null");
    }

    @Test
    void insert1() {
        TreeSet<String> tree = new TreeSet<>();
        tree.insert(null, "root");
        tree.insert("child1.1.1", "child1.1.1.2");
        tree.insert("child1.1.1", "child1.1.1.1");
        tree.insert("child1.1", "child1.1.2");
        tree.insert("child1.1", "child1.1.1");
        tree.insert("child1", "child1.2");
        tree.insert("child1", "child1.1");
        tree.insert("root", "child2");
        tree.insert("root", "child1");
        tree.insert("superRoot", "child1");
        tree.insert(null, "superRoot");
        tree.print();
        System.out.println("#");
        tree.find("superRoot").getChildren().forEach(System.out::println);
        System.out.println("##");
        tree.insert("child2", "superRoot");
        tree.print();
    }

    @Test
    void insert2() {
        TreeSet<String> tree = new TreeSet<>();
        tree.insert("root", "child1");
        tree.insert("root", "child2");
        tree.insert("child1", "child1.1");
        tree.insert("child1", "child1.2");
        tree.insert("child1.1", "child1.1.1");
        tree.insert("child1.1", "child1.1.2");
        tree.insert("child1.1.1", "child1.1.1.1");
        tree.insert("child1.1.1", "child1.1.1.2");
        tree.insert(null, "root");
        tree.insert(null, "root");
        tree.print();
    }

    @Test
    void delete1() {
        TreeSet<String> tree = new TreeSet<>();
        tree.insert("root", "child1");
        tree.insert("root", "child2");
        tree.insert("child1", "child1.1");
        tree.insert("child1", "child1.2");
        tree.insert("child1.1", "child1.1.1");
        tree.insert("child1.1", "child1.1.2");
        tree.insert("child1.1.1", "child1.1.1.1");
        tree.insert("child1.1.1", "child1.1.1.2");
        tree.print();
        System.out.println("#");
        tree.delete("root");
        tree.insert("child1.1.1.2", "root");
        tree.print();
        System.out.println("#");
        tree.delete("child1");
        tree.delete("child2");
        tree.print();
    }

    @Test
    void miscTest() {
        TreeSet<String> tree = new TreeSet<>();
        // can't insert null, should throw NPE
        assertThrows(NullPointerException.class, () -> {
            tree.insert(null, null);
        });
        tree.insert(null, "first");

    }

}