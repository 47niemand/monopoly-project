package pp.muza.monopoly.stuff.test;

import org.junit.jupiter.api.Test;
import pp.muza.monopoly.stuff.Tree;

class BaseTreeNodeTest {

    // TODO: implement tests

    @Test
    void insert1() {
        Tree<String> tree = new Tree<>();
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
        tree.insert("child2", "superRoot");
        tree.print();
    }

    @Test
    void insert2() {
        Tree<String> tree = new Tree<>();
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
        Tree<String> tree = new Tree<>();
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
}