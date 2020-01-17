package Demo;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author : 李煌民
 * @date : 2020-01-16 11:53
 **/
public class TestConf {
    public static void main(String[] args) throws IOException {
        Properties prop = new Properties();
        prop.load(new FileInputStream("D:\\code\\workspace_IdeaUi\\AnalysisOfUserBehaviors\\src\\main\\resources\\demo.properties"));

        System.out.println(prop.get("abc") + "--------------------");

        System.out.println(prop.get("bcd") + "++++++++++++++++");
    }
}
