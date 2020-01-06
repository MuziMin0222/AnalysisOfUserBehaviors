package Demo;

import java.sql.*;

/**
 * @author : 李煌民
 * @date : 2020-01-05 15:04
 * 测试连接集群中的mysql
 **/
public class JdbcDemo {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");

        String URL = "jdbc:mysql://bd1:3306/hive";
        String user = "root";
        String passwd = "root";
        Connection conn = DriverManager.getConnection(URL, user, passwd);

        Statement stat = conn.createStatement();

        ResultSet rs = stat.executeQuery("show tables");

        System.out.println(rs);

        while (rs.next()){
            System.out.println(rs.getString(1));
        }
    }
}
