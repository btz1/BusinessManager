package pk.temp.bm.inventoryJob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pk.temp.bm.utilities.CoreDbConnection;
import pk.temp.bm.utilities.GlobalConstants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

public class LoadResources {

    private static final Logger logger = LoggerFactory.getLogger(LoadResources.class);
    public Map<String,Object> loadInventorySettings(Long brandId){
        Map<String,Object> dataMap = new HashMap<String, Object>();
        try {
            String query = "select key_name,key_value from inventory_file_settings where brand_id = ?";
            Object[] params = {brandId};
            ResultSet resultSet = loadDataInResultSet(query, params);
            while (resultSet.next()) {
                dataMap.put(resultSet.getString("key_name"), resultSet.getString("key_value"));
            }
        }
        catch (Exception e){

        }
        return dataMap;
    }

    public Map<String,Object> loadExistingProducts(Long brandId, Long locationId){
        Map<String,Object> dataMap = new HashMap<String, Object>();

        try {
            /* Query index to column mapping
            * 0 -> product_id
            * 1 -> sku
            * 2 -> upc
            * 3 -> quantity
            * 4 -> inventory_id
            * 5 -> product_detail_id
            * */
            String query = "select p.id as product_id, p.sku as sku, p.upc,il.quantity as quantity, il.id as inventory_id, pd.id as product_detail_id  \n" +
                    "from inventory_location il join products p on il.product_id = p.id join products_detail pd on pd.product_id = p.id \n" +
                    "where il.brand_id = ? and il.location_id = ?;\n";
            Object[] params = {brandId, locationId};
            ResultSet resultSet = loadDataInResultSet(query, params);

            /*
            * In case, product exists but product detail doesn't, we have to skip joining the
            * products_detail table.
            * */
            if(!resultSet.isBeforeFirst()){
                query = "select p.id as product_id, p.sku as sku, p.upc, il.quantity as quantity, il.id as inventory_id \n" +
                        "from inventory_location il join products p on il.product_id = p.id  \n" +
                        "join locations loc on il.location_id = loc.id  \n" +
                        "where il.brand_id = ? and il.location_id = ?";
                resultSet = loadDataInResultSet(query,params);
            }
            else {
                dataMap.put(GlobalConstants.LOCATION_INVENTORY_EXIST_KEY,true);
            }

            /*
            * In case of multiple locations, we might have a situation where we have products but
            * no entry of location inventory. So, we need to omit inventory_location table now, and
            * select records on the bases of brand only.
            * */
            if(!resultSet.isBeforeFirst()){
                query = "select p.id as product_id, p.sku as sku, p.upc \n" +
                        "from products p \n" +
                        "where p.brand_id = ?";
                dataMap.put(GlobalConstants.LOCATION_INVENTORY_EXIST_KEY,false);
                Object[] paramsForBrandId = {brandId};
                resultSet = loadDataInResultSet(query,paramsForBrandId);
            }
            else{
                dataMap.put(GlobalConstants.LOCATION_INVENTORY_EXIST_KEY,true);
            }

            while (resultSet.next()){
                List<Object> valueList = new ArrayList<Object>();
                int allColumns = resultSet.getMetaData().getColumnCount();
                for(int i = 1; i <= allColumns; i++){
                    valueList.add(resultSet.getObject(i));
                }
                String sku = resultSet.getString(GlobalConstants.PRODUCT_SKU);
                sku = sku.replaceAll(GlobalConstants.SEPARATOR_DASH, GlobalConstants.EMPTY_STRING).replaceAll(" ", GlobalConstants.EMPTY_STRING).toLowerCase();
                dataMap.put(sku,valueList);
            }
        }
        catch (Exception e){
            logger.info("Exception while loading data : ",e.getMessage(),e);
        }
        return dataMap;
    }

    public Map<Long,Long> getBufferValue(Long locationId) throws SQLException {
        Map<Long,Long> dataMap = new HashMap<Long, Long>();
        String query = "select buffer_formula from locations where id = ?";
        Object[] params = {locationId};
        ResultSet resultSet = loadDataInResultSet(query, params);
        String bufferFormula = "";
        String[] formulaArray;
        if(resultSet.next()){
           bufferFormula = resultSet.getString(1);
        }
        else{
            bufferFormula = GlobalConstants.DEFAULT_BUFFER_FORMULA;
        }
        if(null!= bufferFormula && bufferFormula.length() > 1){
            formulaArray = bufferFormula.split(GlobalConstants.SEPARATOR_UNDERSCORE);
            for(String value : formulaArray){
                String[] quantityArray = value.split(GlobalConstants.SEPARATOR_COMMA);
                dataMap.put(Long.valueOf(quantityArray[0]),Long.valueOf(quantityArray[1]));
            }
        }
        return dataMap;
    }

    public Map<String,Long> getExistingOrdersQuantity(Long brandId) throws SQLException {
        String query = "select oi.sku, sum(oi.quantity) from orders_items oi join orders o on o.id = oi.orders_id \n" +
                "where o.status in (\"UN_PROCESSED\",\"READY_FOR_EBM\",\"EDITED_BY_EBM\",\"AWAITING_SM_APPROVAL\",\"EDITED_BY_SM\",\"BOOKED\") " +
                " and oi.item_counted = 1 and o.brand_channel_id = (select id from brands_channel where brand_id = ?) group by oi.sku";
        Object[] brandIdParam = {brandId};
        ResultSet resultSet = loadDataInResultSet(query,brandIdParam);
        Map<String,Long> existingOrderQuantityMap = new HashMap<>();
        while (resultSet.next()){
            String plainSku = resultSet.getString(1).replaceAll(GlobalConstants.SEPARATOR_DASH, GlobalConstants.EMPTY_STRING).toLowerCase();
            existingOrderQuantityMap.put(plainSku,resultSet.getLong(2));
        }
        return existingOrderQuantityMap;
    }

    public List<Map<String,Object>> getStoreQuantityOfLocation(List<Long> productIdList, Long brandId,final Date modifiedDate){
        StringBuilder sb = new StringBuilder();
        sb.append("select product_id,sum(quantity) as total_store_quantity ,p.sku from inventory_location il join products p on p.id = il.product_id where product_id in ( ");
        for(int i = 1; i<= productIdList.size(); i++){
            if(i != productIdList.size()){
                sb.append(" ?, ");
            }
            else{
                sb.append("? ) group by product_id");
            }
        }
        String query = sb.toString();
        Object[] params = {productIdList};
        ResultSet resultSet = loadDataInResultSet(query,params);
        List<Map<String,Object>> updateMasterDataList = new ArrayList<>();
        try {
            Map<String,Long> existingOrderQuantityMap = getExistingOrdersQuantity(brandId);
            while (resultSet.next()){
                Map<String,Object> productIdAndQuantityMap = new HashMap<>();
                Long finalQuantity = resultSet.getLong(2);
                String sku = resultSet.getString(3);
                String plainSku = sku.replaceAll(GlobalConstants.SEPARATOR_DASH, GlobalConstants.EMPTY_STRING).toLowerCase();
                if(existingOrderQuantityMap.containsKey(plainSku)){
                    Long existingQuantity = existingOrderQuantityMap.get(plainSku);
                    finalQuantity = finalQuantity - existingQuantity;
                }
                if(finalQuantity < 0){
                    logger.warn("Setting quantity to zero of sku : "+ sku + " to update in master because of negative quantity : "+finalQuantity);
                    finalQuantity = 0L;
                }
                productIdAndQuantityMap.put(GlobalConstants.MASTER_INVENTORY_TABLE_PREFIX+ GlobalConstants.SEPARATOR_UNDERSCORE + "product_id",resultSet.getObject(1));
                productIdAndQuantityMap.put(GlobalConstants.MASTER_INVENTORY_TABLE_PREFIX+ GlobalConstants.SEPARATOR_UNDERSCORE + GlobalConstants.STORE_QUANTITY,finalQuantity);
                productIdAndQuantityMap.put(GlobalConstants.MASTER_INVENTORY_TABLE_PREFIX+ GlobalConstants.SEPARATOR_UNDERSCORE + "last_modified",modifiedDate);

                updateMasterDataList.add(productIdAndQuantityMap);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return updateMasterDataList;
    }


    /*
    * @param params send null to not add params to the query.
    * */
    private ResultSet loadDataInResultSet(String query, Object[] params){
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            Connection connection = CoreDbConnection.getBmDbConnection();
            statement = connection.prepareStatement(query);
            if(null != params){
                int parameterIndex = 1;
                for(Object param : params){
                    if(param instanceof List){
                        List<Object> paramList = (List<Object>) param;
                        for(Object value : paramList){
                            statement.setObject(parameterIndex,value);
                            parameterIndex++;
                        }
                    }
                    else{
                        statement.setObject(parameterIndex,param);
                    }
                    parameterIndex++;
                }
            }
            resultSet = statement.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    /* public Map<String,Object> loadStoreInventory(List<Object> productList){
        String query = "select id, quantity from inventory_location where product_id in (";
        for(int i = 1; i<=productList.size(); i++){
            if(i == productList.size()){
                query += "?)";
            }
            else {
                query += "?,";
            }
        }
        Object[] params = {productList};
        return loadDataInResultSet(query,"id","quantity",params);
    }*/

}
