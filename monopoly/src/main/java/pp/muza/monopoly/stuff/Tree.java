package pp.muza.monopoly.stuff;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author dmytromuza
 */
public class Tree<T> {

    private final Map<T, TreeNode<T>> nodes = new HashMap<>();
    private final TreeNode<T> root = new TreeNode<>(null);

    public Collection<TreeNode<T>> getNodes() {
        return Collections.unmodifiableCollection(nodes.values());
    }

    public TreeNode<T> getRoot() {
        return root;
    }

    public boolean delete(T value) {
        if (value == null) {
            return false;
        }
        TreeNode<T> node = nodes.remove(value);
        if (node == null) {
            return false;
        }
        node.delete();
        return true;
    }

    public void insert(T parent, T value) {
        if (value == null) {
            return;
        }
        TreeNode<T> node = nodes.get(value);
        TreeNode<T> parentNode = parent == null ? root : nodes.get(parent);
        if (parentNode == null) {
            // create new parent
            parentNode = root.addChild(parent);
            nodes.put(parent, parentNode);
        }
        if (node == null) {
            // create new node
            node = parentNode.addChild(value);
            nodes.put(value, node);
        } else {
            // move node to new parent
            node.move(parentNode);
        }
    }

    public TreeNode<T> find(T value) {
        return nodes.get(value);
    }

    public int size() {
        return nodes.size();
    }

    public void print() {
        root.print(root, -1, this::toString);
    }

    public void print(Function<T, String> toString) {
        root.print(root, -1, toString);
    }

    private String toString(T t) {
        if (t == null) {
            return "null";
        }
        return t.toString();
    }

    public static class TreeNode<K> {

        private final K value;
        private final Collection<TreeNode<K>> children = new ArrayList<>();
        private TreeNode<K> parent;

        TreeNode(K value) {
            this.value = value;
            this.parent = null;
        }

        void delete() {
            if (parent != null) {
                parent.children.remove(this);
                parent.children.addAll(children);
            }
            for (TreeNode<K> child : children) {
                child.parent = parent;
            }
        }

        void move(TreeNode<K> parentNode) {
            // check if node is already a child of the branch
            TreeNode<K> node = parentNode;
            while (node != null) {
                if (node == this) {
                    throw new RuntimeException("Can't move node to its child");
                }
                node = node.parent;
            }
            if (parent != null) {
                parent.children.remove(this);
            }
            parent = parentNode;
            parentNode.children.add(this);
        }

        TreeNode<K> addChild(K value) {
            TreeNode<K> node = new TreeNode<>(value);
            children.add(node);
            node.parent = this;
            return node;
        }

        public K getValue() {
            return value;
        }

        public K getParent() {
            return parent == null ? null : parent.value;
        }

        public int level() {
            int level = -1;
            TreeNode<K> node = this;
            while (node.parent != null) {
                level++;
                node = node.parent;
            }
            return level;
        }

        @Override
        public String toString() {
            if (value == null) {
                return "null";
            }
            return value.toString();
        }

        public Collection<TreeNode<K>> getChildren() {
            return Collections.unmodifiableCollection(children);
        }

        void print(TreeNode<K> root, int i, Function<K, String> toString) {
            if (i >= 0) {
                System.out.print("\t".repeat(i));
                System.out.println(toString.apply(root.value));
            }
            for (TreeNode<K> child : root.children) {
                print(child, child.level(), toString);
            }
        }

        public List<K> path() {
            List<K> path = new ArrayList<>();
            TreeNode<K> node = this;
            while (node != null) {
                if (node.value != null) {
                    path.add(node.value);
                } else {
                    path.add(null);
                }
                node = node.parent;
            }
            Collections.reverse(path);
            return Collections.unmodifiableList(path);
        }
    }
}
