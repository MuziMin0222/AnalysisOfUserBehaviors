package Demo;

import java.io.InputStream;

/**
 * @author : 李煌民
 * @date : 2020-01-16 11:53
 **/
public class TestConf {
    public static void main(String[] args) {
        InputStream is = new TestConf().getClass().getClassLoader().getResourceAsStream("commerce.properties");

        System.out.println(is);
    }
}
