package Demo;

/**
 * @author : 李煌民
 * @date : 2020-01-09 14:48
 **/
public class DemoString {
    public static void main(String[] args) {
        A a = new A();

        System.out.println(a.getStr() + "");
    }
}

class A{
    String str;

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }
}
