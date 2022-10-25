package pp.muza.monopoly.model.pieces.actions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import pp.muza.monopoly.stuff.Tree;
import pp.muza.monopoly.model.Player;

class ActionTest {


    final int ONE = 1;
    final int TWO = 2;
    final int THREE = 3;


    @Test
    @SuppressWarnings("unchecked")
    void testRules() throws JsonProcessingException {
        // rule 1: all actions in the package should have a static method create() with 0, 1, 2 or 3 parameters
        // rule 2: invocation of the method create() should return an instance of the class
        // rule 3: all constructors should be protected or package-private
        // rule 4: all accessors should have the same action type as the parent class
        // rule 5: If the actions specify more than one card, the second and subsequent ones must inherit the class of the first one

        Map<Class<? extends BaseActionCard>, Object> map = new LinkedHashMap<>();
        Map<Class<? extends BaseActionCard>, List<Class<? extends BaseActionCard>>> classHierarchy = new LinkedHashMap<>();
        Tree<Class<?>> tree = new Tree<>();

        Collection<Class<?>> classes = Arrays.stream(Action.values())
                .flatMap(b -> b.getClassList().stream())
                .collect(Collectors.toSet());

        for (Class<?> clazz : classes) {
            if (clazz.getSuperclass() != Object.class) {
                tree.insert(clazz.getSuperclass(), clazz);
            }
        }

        for (Action value : Action.values()) {

            List<Class<? extends BaseActionCard>> classList = value.getClassList();
            for (Class<? extends BaseActionCard> clazz : classList) {

                List<Class<? extends BaseActionCard>> classPath = classHierarchy.putIfAbsent(clazz, new ArrayList<>());
                if (classPath != null) {
                    throw new RuntimeException("Class " + clazz + " is already in the hierarchy");
                }
                classPath = classHierarchy.get(clazz);
                Class<? extends BaseActionCard> node = clazz;
                while (node.getSuperclass() != null) {
                    node = (Class<? extends BaseActionCard>) node.getSuperclass();
                    if (Modifier.isAbstract(node.getModifiers())) {
                        break;
                    }
                    classPath.add(node);
                }
                classHierarchy.put(clazz, classPath);

                List<Constructor<?>> publicConstructors = Arrays.stream(clazz.getDeclaredConstructors()).filter(c -> Modifier.isPublic(c.getModifiers())).collect(Collectors.toList());
                assertEquals(0, publicConstructors.size(), "Class " + clazz + " : all constructors should be protected or package-private, but found " + publicConstructors);

                List<Method> methods = Arrays.stream(clazz.getMethods()).filter(m -> m.getName().equals("create") && m.getDeclaringClass() == clazz).collect(Collectors.toList());
                try {
                    if (methods.size() == 1) {
                        Object o = null;
                        try {
                            Method method = methods.get(0);
                            Type[] types = method.getGenericParameterTypes();
                            if (types.length == 0) {
                                o = method.invoke(null);
                            } else if (types.length == 1) {
                                if (types[0] == Chance.class) {
                                    o = method.invoke(null, Chance.GET_OUT_OF_JAIL_FREE);
                                } else {
                                    o = method.invoke(null, ONE);
                                }
                            } else if (types.length == 2) {
                                if (types[1] == Player.class) {
                                    o = method.invoke(null, ONE, new Player(String.valueOf(TWO)));
                                } else {
                                    o = method.invoke(null, ONE, TWO);
                                }
                            } else if (types.length == 3) {
                                if (types[0] == Player.class) {
                                    o = method.invoke(null, new Player(String.valueOf(ONE)), TWO, THREE);
                                } else if (types[1] == Player.class) {
                                    o = method.invoke(null, ONE, new Player(String.valueOf(TWO)), THREE);
                                } else if (types[2] == Player.class) {
                                    o = method.invoke(null, ONE, TWO, new Player(String.valueOf(THREE)));
                                } else {
                                    o = method.invoke(null, ONE, TWO, THREE);
                                }
                            }
                            if (o == null) {
                                throw new RuntimeException("Unsupported  parameters: " + "Class: " + clazz.getName() + ", Method: " + method.getName() + ", Types: " + Arrays.toString(types));
                            }
                            map.put(clazz, o);
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to invoke method create() for class " + clazz, e);
                        }

                    } else if (methods.size() > 1) {
                        throw new RuntimeException("More than one method with name of(): " + clazz.getName());
                    } else if (Modifier.isAbstract(clazz.getModifiers())) {
                        // abstract class
                        continue;
                    } else {
                        throw new RuntimeException("No method with name create(): " + clazz.getName());
                    }
                } catch (Exception e) {
                    fail(e);
                }
            }
        }

        Function<Class<?>, String> test = c -> {
            // check rule 4 and 5
            StringBuilder sb = new StringBuilder();
            sb.append(c.getSimpleName());
            List<Class<?>> path = tree.find(c).path();
            BaseActionCard o = (BaseActionCard) map.get(c);
            if (o != null) {
                assertFalse(
                        path.stream()
                                .map(x -> (BaseActionCard) map.get(x))
                                .filter(Objects::nonNull)
                                .anyMatch(x -> x.getAction() != o.getAction()),
                        "Class " + c + " : all accessors should have the same action type as the parent class");
                sb.append(" : ").append(o);
                Class<?> root = path.get(1);
                assertTrue(root.isAssignableFrom(o.getClass()), "Class " + c + " : second and subsequent classes should inherit the first class :" + root);
            } else {
                sb.append(" : no instance");
            }
            return sb.toString();
        };
        tree.print(test);

        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        System.out.println(mapper.writeValueAsString(map));
    }

}



