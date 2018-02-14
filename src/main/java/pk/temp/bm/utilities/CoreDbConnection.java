package pk.temp.bm.utilities;

import org.apache.log4j.Logger;
import org.springframework.core.env.Environment;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class CoreDbConnection {

    private static final Logger logger = Logger.getLogger(CoreDbConnection.class);

    public static Connection createMysqlConnection(String server, String port, String dbName, String userName, String password) throws SQLException {
        String properties = "rewriteBatchedStatements=true";
        String connectionURL =  "jdbc:mysql://" + server
                + ":" + port + "/" + dbName + "?"
                + properties;
        return DriverManager.getConnection(connectionURL,userName,password);
    }

    public static Connection createSqlServerConnection(String server, String port, String dbName, String userName, String password) throws Exception{
        String url = "jdbc:sqlserver://"+server+":"+port+";DatabaseName="+dbName;
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return DriverManager.getConnection(url,userName,password);
    }

    public static Connection getBmDbConnection() throws Exception{
        Environment environment = ApplicationContextHolder.getContext().getEnvironment();
        String server = environment.getProperty("talend.host_db");
        String port = environment.getProperty("talend.port_db");
        String userName = environment.getProperty("talend.user_db");
        String password = environment.getProperty("talend.password_db");
        String dbName = environment.getProperty("talend.name_db");

        return createMysqlConnection(server,port,dbName,userName,password);
    }
}
