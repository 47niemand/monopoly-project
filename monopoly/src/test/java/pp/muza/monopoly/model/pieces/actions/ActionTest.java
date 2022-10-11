package pp.muza.monopoly.model.pieces.actions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import pp.muza.monopoly.model.Player;

class ActionTest {


    final int ONE = 1;
    final int TWO = 2;
    final int THREE = 3;


    @Test
    @SuppressWarnings("unchecked")
    public void testRules() throws JsonProcessingException {
        // rule: all actions in the package should have a static method of() with 0, 1, 2 or 3 parameters
        // rule: invocation of the method of() should return an instance of the class
        // rule: all constructors should be protected or package-private
        // rule: all accessors should have the same action type as the parent class

        Map<Class<? extends BaseActionCard>, Object> map = new LinkedHashMap<>();
        Map<Class<? extends BaseActionCard>, List<Class<? extends BaseActionCard>>> classHierarchy = new LinkedHashMap<>();

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

                List<Method> methods = Arrays.stream(clazz.getMethods()).filter(m -> m.getName().equals("of") && m.getDeclaringClass() == clazz).collect(Collectors.toList());
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
                                } else {
                                    o = method.invoke(null, ONE, TWO, THREE);
                                }
                            }
                            if (o == null) {
                                throw new RuntimeException("Unsupported  parameters: " + "Class: " + clazz.getName() + ", Method: " + method.getName() + ", Types: " + Arrays.toString(types));
                            }
                            map.put(clazz, o);
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to invoke method of() for class " + clazz, e);
                        }

                    } else if (methods.size() > 1) {
                        throw new RuntimeException("More than one method with name of(): " + clazz.getName());
                    } else {
                        throw new RuntimeException("No method with name of(): " + clazz.getName());
                    }
                } catch (Exception e) {
                    fail(e.getMessage());
                }
            }
        }

        for (Map.Entry<Class<? extends BaseActionCard>, List<Class<? extends BaseActionCard>>> classListEntry : classHierarchy.entrySet()) {
            Class<?> clazz = classListEntry.getKey();
            BaseActionCard o = (BaseActionCard) map.get(clazz);
            for (Class<?> aClass : classListEntry.getValue()) {
                BaseActionCard o1 = (BaseActionCard) map.get(aClass);
                if (o1 != null) {
                    assertEquals(o1.getAction(), o.getAction(), "Class " + clazz + " : all accessors should have the same action type as the parent class");
                }
            }
        }

        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        System.out.println(mapper.writeValueAsString(map));
    }


}

