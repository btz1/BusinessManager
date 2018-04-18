package pk.temp.bm.utilities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pk.temp.bm.models.User;

/**
 * Created by Abubakar on 7/26/2017.
 */
public class GlobalAppUtil {

    private static final Logger logger = LoggerFactory.getLogger(GlobalAppUtil.class);
    private volatile static Map<String, Object> dataMap = new HashMap<>();

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

    public static boolean isAllNulls(Iterable<?> array) {
        return StreamSupport.stream(array.spliterator(), true).allMatch(o -> o == null);
    }

}
