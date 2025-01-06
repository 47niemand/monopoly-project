package pp.muza.stuff;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * TreeSet implementation
 * <p>
 * It is a tree with unique values, each node can have multiple children.
 * <p>
 * - Each node can be accessed by value ({@link #find(Object)}).
 * - You can insert new value to the root ({@link #insert(Object)}) or to the
 * parent ({@link #insert(Object, Object)}). Parent node will be created if it
 * is not found.
 * - You can delete node by value ({@link #delete(Object)}).
 * <p>
 * TreeNodes are immutable, you cannot change their values.
 * the only way to change the tree is to insert or delete nodes.
 *
 * @param <T> the type of the value
 * @author dmytromuza
 */
public class TreeSet<T> implements Set<T> {

    private static final String NULL_STR = "null";
    private final Map<T, TreeNode<T>> nodes = new HashMap<>();
    private final TreeNode<T> root = new TreeNode<>(null);

    /**
     * Return all nodes in the tree
     *
     * @return collection of nodes
     */
    public Collection<TreeNode<T>> getNodes() {
        return Collections.unmodifiableCollection(nodes.values());
    }

    /**
     * Get the root node of the tree. It is a node with null value.
     * Children of the root node are the root nodes of the trees.
     *
     * @return root node
     */
    public TreeNode<T> getRoot() {
        return root;
    }

    /**
     * Delete node by value
     *
     * @param value the value to delete
     * @return true if the node was deleted
     * @throws NullPointerException if value is null
     */
    public boolean delete(T value) {
        if (value == null) {
            throw new NullPointerException();
        }
        TreeNode<T> node = nodes.remove(value);
        if (node == null) {
            return false;
        }
        node.delete();
        return true;
    }

    /**
     * Insert a new value to the root of the tree, if value already exists then move
     * to the root.
     *
     * @param value the value to insert, cannot be null
     * @return true if node was inserted or moved
     * @throws NullPointerException if value is null
     */
    public boolean insert(T value) {
        return insert(null, value);
    }

    /**
     * Insert a new value to the parent, if parent is null then insert to the root.
     * If parent is not null and not found, then create a new parent.
     * If value already exists, then move to the parent.
     *
     * @param parent the parent of the value can be null
     * @param value  the value to insert, cannot be null
     * @return true if node was inserted or moved
     * @throws NullPointerException     if value is null
     * @throws IllegalArgumentException if value is already a child of the parent
     **/
    public boolean insert(T parent, T value) {
        boolean result;
        if (value == null) {
            throw new NullPointerException();
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
            result = true;
        } else {
            // move node to new parent
            result = node.move(parentNode);
        }
        return result;
    }

    /**
     * Find node by value
     *
     * @param value the value to find, can be null to find root node
     * @return node or null if not found
     */
    public TreeNode<T> find(T value) {
        if (value == null) {
            return root;
        }
        return nodes.get(value);
    }

    /**
     * Get size of the tree
     *
     * @return size of the tree
     */
    public int size() {
        return nodes.size();
    }

    /**
     * Print tree to the console
     */
    public void print() {
        root.print(root, -1, this::toString);
    }

    /**
     * Print tree to the console
     *
     * @param toString function to convert value to string
     */
    public void print(Function<T, String> toString) {
        root.print(root, -1, toString);
    }

    private String toString(T t) {
        if (t == null) {
            return NULL_STR;
        }
        return t.toString();
    }

    @Override
    public boolean isEmpty() {

        return nodes.isEmpty();
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @Override
    public boolean contains(Object o) {
        return nodes.containsKey(o);
    }

    @Override
    public Iterator<T> iterator() {
        return nodes.keySet().iterator();
    }

    @Override
    public Object[] toArray() {
        return nodes.keySet().toArray();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public <T> T[] toArray(T[] a) {
        return nodes.keySet().toArray(a);
    }

    @Override
    public boolean add(T e) {
        return insert(e);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean remove(Object o) {
        return delete((T) o);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean containsAll(Collection<?> c) {
        return nodes.keySet().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return c.stream().map(this::insert).reduce(true, (a, b) -> a && b);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return c.stream().map(this::remove).reduce(true, (a, b) -> a && b);
    }

    @Override
    public void clear() {
        nodes.clear();
        root.children.clear();
    }

    /**
     * Tree node
     */
    public static class TreeNode<K> {

        private final K value;
        private final Collection<TreeNode<K>> children = new ArrayList<>();
        private TreeNode<K> parent;

        private TreeNode(K value) {
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

        boolean move(TreeNode<K> parentNode) {
            // check if node is already a parent of the branch
            if (parentNode == this.parent) {
                return false;
            }
            // check if node is a child of the branch
            TreeNode<K> node = parentNode;
            while (node != null) {
                if (node == this) {
                    throw new IllegalArgumentException("Can't move node to its child");
                }
                node = node.parent;
            }
            if (parent != null) {
                parent.children.remove(this);
            }
            parent = parentNode;
            parentNode.children.add(this);
            return true;
        }

        TreeNode<K> addChild(K value) {
            TreeNode<K> node = new TreeNode<>(value);
            children.add(node);
            node.parent = this;
            return node;
        }

        /**
         * Get value of the node
         *
         * @return value
         */
        public K getValue() {
            return value;
        }

        /**
         * Get value of the parent node
         *
         * @return value of the parent node or null if parent node is null
         */
        public K getParent() {
            return parent == null ? null : parent.value;
        }

        /**
         * Get level of the node
         *
         * @return level of the node
         */
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
                return NULL_STR;
            }
            return value.toString();
        }

        /**
         * Get children values of the node
         *
         * @return children values
         */
        public List<K> getChildren() {
            return children.stream().map(x -> x.value).collect(Collectors.toUnmodifiableList());
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

        /**
         * Get path from the root to the node
         *
         * @return path
         */
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
