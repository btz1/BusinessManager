package pk.temp.bm.inventoryJob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pk.temp.bm.utilities.GlobalConstants;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;

import static pk.temp.bm.utilities.GlobalAppUtil.getTableColumns;

public class UpdateOrInsert implements Runnable{

    private List<Map<String,Object>> dataList;
    private String tableNamePrefix;
    private String tableName;
    private List<String> tableColumnList;
    private String action;
    private int batchSize = 1000;
    private String[] productColToReturn = {};
    Map<String,Long> insertedProductIdAndSkuMap = new HashMap<>();
    Phaser phaser;
    private static final Logger logger = LoggerFactory.getLogger(UpdateOrInsert.class);
    Connection connection;
    Boolean insertIntoInventoryOnly;
    AtomicReference<List<Long>> atomicReferenceOfMasterUpdateList;
    PersistInventoryData persistInventoryData;


    UpdateOrInsert(List<Map<String, Object>> dataList, String tableNamePrefix, String tableName,
                   List<String> tableColumnList, String action, Phaser phaser, Connection connection,Boolean insertIntoInventoryOnly) {
        this.dataList = dataList;
        this.tableColumnList = tableColumnList;
        this.tableName = tableName;
        this.tableNamePrefix = tableNamePrefix;
        this.action = action;
        this.phaser = phaser;
        this.connection = connection;
        this.insertIntoInventoryOnly = insertIntoInventoryOnly;
        this.persistInventoryData = new PersistInventoryData();
    }


    @Override
    public void run() {
        try {
            if(action.equalsIgnoreCase(GlobalConstants.INSERT_ACTION)){
                if(insertIntoInventoryOnly){
                    tableNamePrefix = GlobalConstants.INVENTORY_TABLE_PREFIX + GlobalConstants.SEPARATOR_UNDERSCORE;
                    tableName = GlobalConstants.INVENTORY_TABLE_NAME;
                    tableColumnList = getTableColumns(dataList, tableNamePrefix);
                    updateRecords();
                }
                else{
                    for(String name : GlobalConstants.INVENTORY_UPDATE_TABLES){
                        switch (name){
                            case GlobalConstants.PRODUCT_TABLE_NAME:
                                tableColumnList = getTableColumns(dataList, GlobalConstants.PRODUCT_TABLE_PREFIX+ GlobalConstants.SEPARATOR_UNDERSCORE);
                                tableName = GlobalConstants.PRODUCT_TABLE_NAME;
                                tableNamePrefix = GlobalConstants.PRODUCT_TABLE_PREFIX + GlobalConstants.SEPARATOR_UNDERSCORE;
                                productColToReturn = new String[]{GlobalConstants.DEFAULT_KEY_COLUMN, GlobalConstants.PRODUCT_SKU, GlobalConstants.PRODUCT_UPC};
                                updateRecords();
                                break;

                            case GlobalConstants.PRODUCT_DETAIL_TABLE_NAME:
                                tableNamePrefix = GlobalConstants.PRODUCT_DETAIL_TABLE_PREFIX + GlobalConstants.SEPARATOR_UNDERSCORE;
                                tableColumnList = getTableColumns(dataList, tableNamePrefix);
                                if(null != tableColumnList && !tableColumnList.isEmpty()){
                                    for(Map<String,Object> singleRow: dataList){
                                        Long productId = insertedProductIdAndSkuMap.get(singleRow.get("plainSku"));
                                        singleRow.put(tableNamePrefix+"product_id", productId);
                                    }
                                    tableName = GlobalConstants.PRODUCT_DETAIL_TABLE_NAME;
                                    tableColumnList = getTableColumns(dataList, tableNamePrefix); // fetching again to include product_id
                                    updateRecords();
                                }
                                break;

                            case GlobalConstants.INVENTORY_TABLE_NAME:
                                tableNamePrefix = GlobalConstants.INVENTORY_TABLE_PREFIX + GlobalConstants.SEPARATOR_UNDERSCORE;
                                for(Map<String,Object> singleRow: dataList){
                                    Long productId = insertedProductIdAndSkuMap.get(singleRow.get("plainSku"));
                                    singleRow.put(tableNamePrefix+"product_id",productId);

                                    // following block gets current list of product_ids and adds product_ids of current process in synchronized way
//                                    List<Long> existing = persistInventoryData.getAtomicReferenceOfMasterUpdateList().get();
                                    List<Long> existing = PersistInventoryData.atomicReferenceOfMasterUpdateList.get();
                                    UnaryOperator<List<Long>> productIdList = t -> {
                                        existing.add(productId);
                                        return existing;
                                    };
                                    PersistInventoryData.atomicReferenceOfMasterUpdateList.updateAndGet(productIdList);
                                    // end block
                                }
                                tableName = GlobalConstants.INVENTORY_TABLE_NAME;
                                tableColumnList = getTableColumns(dataList, tableNamePrefix); // fetching again to include product_id
                                updateRecords();
                                break;

                            case GlobalConstants.MASTER_INVENTORY_TABLE_NAME:
                                tableNamePrefix = GlobalConstants.MASTER_INVENTORY_TABLE_PREFIX + GlobalConstants.SEPARATOR_UNDERSCORE;
                                for(Map<String,Object> singleRow: dataList){
                                    Long productId = insertedProductIdAndSkuMap.get(singleRow.get("plainSku"));
                                    singleRow.put(tableNamePrefix+"product_id",productId);
                                    singleRow.put(tableNamePrefix+ GlobalConstants.STORE_QUANTITY,singleRow.get(GlobalConstants.INVENTORY_TABLE_PREFIX + GlobalConstants.SEPARATOR_UNDERSCORE + GlobalConstants.STORE_QUANTITY));
                                }
                                tableName = GlobalConstants.MASTER_INVENTORY_TABLE_NAME;
                                tableColumnList = getTableColumns(dataList, tableNamePrefix); // fetching again to include product_id
                                updateRecords();
                                break;
                        }
                    }
                }
            }
            else{
                updateRecords();
            }
        } catch (Exception e) {
            logger.error("Exception while updating inventory.",e.getMessage(),e);
            throw new RuntimeException("Exception while updating inventory. \n" + e.getMessage() ,e.getCause());
        }
        finally {
            phaser.arriveAndDeregister();
        }
    }

    private void updateRecords() throws Exception{
//        Connection connection = null;
        PreparedStatement ps = null;
        try{
            if(!dataList.isEmpty()){
                String keyColumn = tableName.equalsIgnoreCase(GlobalConstants.MASTER_INVENTORY_TABLE_NAME) ? "product_id" : GlobalConstants.DEFAULT_KEY_COLUMN;
                Map<String,Object> queryMap = createQuery(action, keyColumn);
                Map<String,Integer> paramPositionMap = (Map<String, Integer>) queryMap.get("paramPositions");
                String query = (String) queryMap.get("query");
//                connection = CoreDbConnection.getBmDbConnection();
//                connection.setAutoCommit(false);
                ps = productColToReturn.length != 0 ? connection.prepareStatement(query, productColToReturn) : connection.prepareStatement(query) ;
                long start = System.currentTimeMillis();
                int count = 0;
                ResultSet resultSet = null;
                List<String> insertedSkuList = new ArrayList<>();

                // dataList is number of rows
                for (Map<String,Object> singleRowData : dataList) {

                    for(Map.Entry<String,Integer> paramPositionAndLookupKey : paramPositionMap.entrySet()){ // set column values of row
                        ps.setObject(paramPositionAndLookupKey.getValue(), singleRowData.get(paramPositionAndLookupKey.getKey()));
                    }
                    if(tableName.equalsIgnoreCase(GlobalConstants.PRODUCT_TABLE_NAME)){
                        String sku_key = (String) singleRowData.get(GlobalConstants.PRODUCT_TABLE_PREFIX + GlobalConstants.SEPARATOR_UNDERSCORE + GlobalConstants.PRODUCT_SKU);
                        sku_key = sku_key.replaceAll(GlobalConstants.SEPARATOR_DASH, GlobalConstants.EMPTY_STRING).toLowerCase();
                        insertedSkuList.add(sku_key);
                    }
                    ps.addBatch();

                    if(++count % batchSize == 0) {
                        ps.executeBatch();
                        resultSet = ps.getGeneratedKeys();
//                        connection.commit();
                        getProductIdsAfterInsert(resultSet,insertedSkuList);
                        insertedSkuList = new ArrayList<>();
                        logger.info(action+" Batch of : "+count);
                        logger.info("Time Taken="+(System.currentTimeMillis()-start));
                        count = 0;
                    }
                }
                if(!connection.isClosed()){
                    ps.executeBatch(); // insert remaining records
                    resultSet = ps.getGeneratedKeys();
//                    connection.commit();
                    getProductIdsAfterInsert(resultSet,insertedSkuList);
                    logger.info(action+" Batch of : "+count);
                    logger.info("Time Taken="+(System.currentTimeMillis()-start));
                    ps.close();
//                    connection.close();
                }
            }
        } finally {
            if(null != ps) {
                ps.close();
            }
        }
    }

    private Map<String,Object> createQuery(String action, String keyColName) throws Exception{
        Map<String,Integer> paramPositionMap = new HashMap<>();
        String fieldSeparator = ", ";
        StringBuilder sb = new StringBuilder();
        StringBuilder sbInsertParams = new StringBuilder();
        if(action.equalsIgnoreCase(GlobalConstants.UPDATE_ACTION)){
            sb.append("update "+tableName+" set ");
        }
        else {
            sb.append("insert into "+tableName + " (");
        }
        int paramPosition = 1;
        String insertValuesClause = "Values (";
        for(int count = 0; count < tableColumnList.size() ; count++){
            String colName = tableColumnList.get(count);
            Boolean addSeparator = false;
            if(!colName.equalsIgnoreCase(keyColName)){
                if(action.equalsIgnoreCase(GlobalConstants.UPDATE_ACTION)){
                    sb.append( colName + " = ?");
                    addSeparator = true;
                }
                else{
                    sb.append(colName);
                    if(sbInsertParams.indexOf(insertValuesClause) == -1){
                        sbInsertParams.append(insertValuesClause);
                    }
                    sbInsertParams.append("?");
                    addSeparator = true;
                }
            }
            if(addSeparator){
                paramPositionMap.put(tableNamePrefix + colName , paramPosition);
                /*
                * if {keyCol} comes at the end of the list, we don't have
                * to put a separator after second last column.
                * */
                if(count != tableColumnList.size() - 1){ // if its not the end of the list.
                    // to check if the next col is key col and current col is not the second last col.
                    // if that's not the case, put separator. Otherwise, don't.
                    if(count != tableColumnList.size() -2 || !tableColumnList.get(count +1).equalsIgnoreCase(keyColName)){
                        sb.append(fieldSeparator);
                        if(!action.equalsIgnoreCase(GlobalConstants.UPDATE_ACTION)) {
                            sbInsertParams.append(fieldSeparator);
                        }
                    }
                }
                paramPosition++;
            }

            if(count == tableColumnList.size() - 1){ // end of query
                if(action.equalsIgnoreCase(GlobalConstants.UPDATE_ACTION)){
                    sb.append(" where "+keyColName+" = ?");
                    paramPositionMap.put(tableNamePrefix + keyColName ,paramPosition);
                }
                else{
                    sb.append(")");
                    sbInsertParams.append(")");
                    sb.append(sbInsertParams);
                }
            }
        }
        String query = sb.toString();
        Map<String,Object> queryMap = new HashMap<>();
        queryMap.put("query",query);
        queryMap.put("paramPositions",paramPositionMap);
        return queryMap;
    }

    private void getProductIdsAfterInsert(ResultSet resultSet,List<String> insertedSkuList) throws SQLException {
        if(tableName.equalsIgnoreCase(GlobalConstants.PRODUCT_TABLE_NAME)){
            int skuCount = 0;
            while (resultSet.next()){
                insertedProductIdAndSkuMap.put(insertedSkuList.get(skuCount),resultSet.getLong(1));
                skuCount++;
            }
        }
    }
}
