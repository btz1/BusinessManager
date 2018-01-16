package pk.temp.bm.utilities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pk.temp.bm.models.User;

/**
 * Created by Abubakar on 7/26/2017.
 */
public class GlobalAppUtil {

    private static final Logger logger = LoggerFactory.getLogger(GlobalAppUtil.class);


    private volatile static Map<String, Object> dataMap = new HashMap<>();


    public static void setLoggedInUser(User user){
        dataMap.put(GlobalConstants.LOGGED_IN_USER_KEY,user);
    }
    public static User getLoggedInUser(){
        return (User) dataMap.get(GlobalConstants.LOGGED_IN_USER_KEY);
    }

    public static void setLoggedInUserClient(String clientId){
        dataMap.put(GlobalConstants.LOGGED_IN_USER_CLIENT_KEY,clientId);
    }
    public static String getLoggedInUserClient(){
        return (String) dataMap.get(GlobalConstants.LOGGED_IN_USER_CLIENT_KEY);
    }

    public static String getActiveEnvironment(){
        return (String) dataMap.get(GlobalConstants.SERVER_ENVIRONMENT_PROPERTY);
    }

    public static void setActiveEnvironment(String activeEnvironment){
        dataMap.put(GlobalConstants.SERVER_ENVIRONMENT_PROPERTY,activeEnvironment);
    }


    public static Executor getCompFuturePool() {
        return (Executor) ApplicationContextHolder.getContext().getBean("compFutureTaskExecutor");
    }

    public static Executor getAgentTaskExecutor() {
        return (Executor) ApplicationContextHolder.getContext().getBean("agentTaskExecutor");
    }

    public static Executor getIOPool() {
        return (Executor) ApplicationContextHolder.getContext().getBean("IOTaskExecutor");
    }

    public static Executor getStateMachineInitPool() {
        return (Executor) ApplicationContextHolder.getContext().getBean("stateMachineInit");
    }

    public static String decodeCamelCase(String s) {
        return s.replaceAll(
                String.format("%s|%s|%s",
                        "(?<=[A-Z])(?=[A-Z][a-z])",
                        "(?<=[^A-Z])(?=[A-Z])",
                        "(?<=[A-Za-z])(?=[^A-Za-z])"
                ),
                " "
        );
    }

    // inventory update
    public static List<String> getTableColumns(List<Map<String,Object>> updateList, String tableNamePrefix){
        return updateList.get(0).entrySet().stream()
                .filter(map -> map.getKey().contains(tableNamePrefix))
                .map(p -> p.getKey().replaceAll(tableNamePrefix,""))
                .collect(Collectors.toList());
    }

}
