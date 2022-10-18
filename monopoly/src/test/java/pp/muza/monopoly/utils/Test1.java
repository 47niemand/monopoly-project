package pp.muza.monopoly.utils;

import org.junit.jupiter.api.Test;

public class Test1 {

    @Test
    public void test1() {
        A a = new A();
        B b = new B();
        A aa = (A) b;
        System.out.println(b.a());
        System.out.println(a.a());
        System.out.println(a.all());
        System.out.println(aa.all());
    }

    public static class A {
        public String a() {
            return "a";
        }
        public String all() {
            return a();
        }
    }

    public static class B extends A {
        @Override
        public String a() {
            return super.a() + "b";
        }
    }

}
