package pk.temp.bm.utilities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Abubakar on 6/30/2017.
 */
public interface GlobalConstants {


	String ENDPOINT_LOGIN = "/user_login";
	String ENDPOINT_OAUTH = "/oauth";
	String ENDPOINT_CREATE_ORDER = "/orders/create";
	String HEADER_AUTH_KEY = "authorization";
	String BRAND_KEY = "brand";
	String LOCATION_KEY = "location";
	String CONTEXT_PATH = "/";
	String HEADER_X_REQUESTED_WITH = "x-requested-with";
	String HEADER_AUTHORIZATION = "authorization";
	String HEADER_CONTENT_TYPE = "content-type";
	String HEADER_OPTIONAL_TOKEN = "optional-token";
	Long COUNT_VALUE_ZERO = 0L;
	String METHOD_RESPONSE_SUCCESS = "success";
	String LOGGED_IN_USER_KEY = "loggedInUser";
	String LOGGED_IN_USER_CLIENT_KEY = "loggedInUSerClient";
	Integer AUTH_TOKEN_PREFIX_LEN = 7;
	String SERVER_ENVIRONMENT_LIVE = "live";
	String SERVER_ENVIRONMENT_STAGE = "stage";
	String SERVER_ENVIRONMENT_PROPERTY = "spring.profiles.active";
	String DEFAULT_CLIENT = "WebApp";
	String DEFAULT_USER = "System";
	Integer OE_ADMIN_DB_ID = 9;
	// inventory update
	String PRODUCT_DETAIL_TABLE_PREFIX = "ild";
	String PRODUCT_TABLE_PREFIX = "prd";
	String INVENTORY_TABLE_PREFIX = "inv";
	String SEPARATOR_UNDERSCORE = "_";
	String SEPARATOR_DASH = "-";
	String SEPARATOR_COMMA = ",";
	String SEPARATOR_PLUS = "+";
	String SEPARATOR_MULTIPLY = "*";
	String SEPARATOR_DIVIDE = "\\/";
    String SEPARATOR_DOT = "\\.";
    String EMPTY_STRING = "";
    String INVENTORY_TABLE_NAME = "inventory_location";
    String PRODUCT_TABLE_NAME = "products";
    String PRODUCT_DETAIL_TABLE_NAME = "products_detail";
    String MASTER_INVENTORY_TABLE_NAME = "inventory_master";
    String MASTER_INVENTORY_TABLE_PREFIX = "invm";
    String[] INVENTORY_UPDATE_TABLES = {PRODUCT_TABLE_NAME,PRODUCT_DETAIL_TABLE_NAME,INVENTORY_TABLE_NAME,MASTER_INVENTORY_TABLE_NAME};
    String STORE_QUANTITY = "quantity";
    String PRODUCT_SKU = "sku";
    String PRODUCT_UPC = "upc";
    String PRODUCT_PRICE = "price";
    String PRODUCT_NAME = "name";
    String PRODUCT_SPECIAL_PRICE = "special_price";
    String DEFAULT_KEY_COLUMN = "id";
    String INSERT_ACTION = "insert";
    String UPDATE_ACTION = "update";
    String DEFAULT_BUFFER_FORMULA = "-1,0";
    Integer INDEX_OF_PRODUCT_ID = 0;
    Integer INDEX_OF_SKU = 1;
    Integer INDEX_OF_UPC = 2;
    Integer INDEX_OF_QUANTITY = 3;
    Integer INDEX_OF_INVENTORY_ID = 4;
    Integer INDEX_OF_PRODUCT_DETAIL_ID = 5;
    String LOCATION_INVENTORY_EXIST_KEY = "locationInventoryExistKey";
}
