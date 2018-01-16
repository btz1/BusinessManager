package pk.temp.bm.inventoryJob;

import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pk.temp.bm.utilities.CoreDbConnection;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;

import static pk.temp.bm.utilities.GlobalAppUtil.getTableColumns;
import static pk.temp.bm.utilities.GlobalConstants.*;

public class PersistInventoryData implements Thread.UncaughtExceptionHandler, Runnable {

    private Phaser phaser = new Phaser(1);
    private static final Logger logger = LoggerFactory.getLogger(PersistInventoryData.class);
    static AtomicReference<List<Long>> atomicReferenceOfMasterUpdateList;
    private Long brandId;
    private Date modifiedDate;
    private boolean partialUpdate;
    private Map<String,Object> insertAndUpdateDataList;
    private CountDownLatch countDownLatch;
    private AtomicBoolean response;
    private Connection connection;
    public AtomicBoolean commitData = new AtomicBoolean(true); // true by default, only the thread with an exception will change its value to false.

    public PersistInventoryData(){}

    public PersistInventoryData(Map<String, Object> insertAndUpdateDataList, Long brandId, final Date modifiedDate,
                                boolean partialUpdate, CountDownLatch countDownLatch, AtomicBoolean response) throws Exception{
        this.brandId = brandId;
        this.response = response;
        this.modifiedDate = modifiedDate;
        this.partialUpdate = partialUpdate;
        this.countDownLatch = countDownLatch;
        this.insertAndUpdateDataList = insertAndUpdateDataList;
        this.connection = CoreDbConnection.getOeDbConnection();
        connection.setAutoCommit(false);
    }

    private void updateOrSaveData() throws Exception {

        boolean updated = false;
        try{
            List<Map<String,Object>> insertList = (List<Map<String, Object>>) insertAndUpdateDataList.get("insertList");
            List<Map<String,Object>> insertInventoryDataList = (List<Map<String, Object>>) insertAndUpdateDataList.get("insertInventoryDataList");
            List<Map<String,Object>> updateList = (List<Map<String, Object>>) insertAndUpdateDataList.get("updateList");
            List<Long> productIdsToUpdateMaster = (List<Long>) insertAndUpdateDataList.get("productIdsToUpdateMaster");

            if(insertList.isEmpty() && updateList.isEmpty()){
                logger.error("No products found to Update or Insert. Aborting inventory update process for brand : "+brandId);
                countDownLatch.countDown(); // error response, no need to set atomicBoolean
                return;
            }
            atomicReferenceOfMasterUpdateList = new AtomicReference<>(productIdsToUpdateMaster);

            // update existing products
            List<String> updateProductColumns = updateList.isEmpty() ? null : getTableColumns(updateList,PRODUCT_TABLE_PREFIX+ SEPARATOR_UNDERSCORE);
            List<String> updateProductDetailColumns = updateList.isEmpty() ? null : getTableColumns(updateList,PRODUCT_DETAIL_TABLE_PREFIX+ SEPARATOR_UNDERSCORE);
            List<String> updateInventoryColumns = updateList.isEmpty() ? null : getTableColumns(updateList,INVENTORY_TABLE_PREFIX+ SEPARATOR_UNDERSCORE);


            if(null != updateProductColumns && !updateProductColumns.isEmpty()){
                logger.info("Going to update existing products : "+updateList.size());
                triggerAction(updateList,updateProductColumns,PRODUCT_TABLE_PREFIX+ SEPARATOR_UNDERSCORE
                        ,"products",UPDATE_ACTION,false);
            }
            if(null != updateProductDetailColumns && !updateProductDetailColumns.isEmpty()){
                logger.info("Going to update existing product details : "+updateList.size());
                triggerAction(updateList,updateProductDetailColumns,PRODUCT_DETAIL_TABLE_PREFIX+ SEPARATOR_UNDERSCORE
                        ,"products_detail",UPDATE_ACTION,false);
            }
            if(null != updateInventoryColumns && !updateInventoryColumns.isEmpty()){
                logger.info("Going to update existing inventory : "+updateList.size());
                triggerAction(updateList,updateInventoryColumns,INVENTORY_TABLE_PREFIX+ SEPARATOR_UNDERSCORE
                        ,"inventory_location",UPDATE_ACTION,false);
            }
            if(insertAndUpdateDataList.containsKey("inventoryIdListToSetZero")){
                if( !partialUpdate){
                    List<Map<String,Object>> inventoryIdsToMarkZero = (List<Map<String, Object>>) insertAndUpdateDataList.get("inventoryIdListToSetZero");
                    List<String> updateInventoryToZeroColumns = inventoryIdsToMarkZero.isEmpty() ?
                            null : getTableColumns(inventoryIdsToMarkZero,INVENTORY_TABLE_PREFIX+ SEPARATOR_UNDERSCORE);
                    if(null != updateInventoryToZeroColumns && !updateInventoryToZeroColumns.isEmpty()){
                        logger.info("Going mark inventory as Zero for : "+ inventoryIdsToMarkZero.size() +" skus");
                        triggerAction(inventoryIdsToMarkZero,updateInventoryToZeroColumns,INVENTORY_TABLE_PREFIX+ SEPARATOR_UNDERSCORE
                                ,"inventory_location",UPDATE_ACTION,false);
                    }
                }
            }

            if(insertAndUpdateDataList.containsKey("insertIntoInventoryOnly") && (Boolean) insertAndUpdateDataList.get("insertIntoInventoryOnly")){

                // it means the inventory doesn't exist for specified location, but products exist, so we need to insert into inventory only.
                if(null != insertInventoryDataList && !insertInventoryDataList.isEmpty()){
                    logger.info("Going to add new products : "+insertInventoryDataList.size());
                    List<String> insertInventoryColumns = getTableColumns(insertInventoryDataList,PRODUCT_TABLE_PREFIX+ SEPARATOR_UNDERSCORE);
                    triggerAction(insertInventoryDataList,insertInventoryColumns,INVENTORY_TABLE_PREFIX+ SEPARATOR_UNDERSCORE, INVENTORY_TABLE_NAME,
                            INSERT_ACTION,true);
                }


            }
//            logger.info("All updating threads fired, waiting for completion...");
//            phaser.arriveAndAwaitAdvance();
//            connection.commit();

            // insert new products from file
            if(!insertList.isEmpty()){
                logger.info("Going to add new products : "+insertList.size());
                List<String> insertProductColumns = getTableColumns(insertList,PRODUCT_TABLE_PREFIX+ SEPARATOR_UNDERSCORE);
                triggerAction(insertList,insertProductColumns,PRODUCT_TABLE_PREFIX+ SEPARATOR_UNDERSCORE, PRODUCT_TABLE_NAME,
                        INSERT_ACTION,false);
            }

            logger.info("All inserting threads fired, waiting for completion...");
            phaser.arriveAndAwaitAdvance();
            logger.info("Store Update Completed");
            if(commitData.get()){
                connection.commit();
            }
            else {
                connection.rollback();
                return;
            }

            // update master inventory
            if(atomicReferenceOfMasterUpdateList.get().isEmpty()){
                logger.info("No Product Ids found to update master.");
            }
            else{
                logger.info("Going to update Master");
                LoadResources loadResources = new LoadResources();
                List<Map<String,Object>> updateMasterDataList = loadResources.getStoreQuantityOfLocation(atomicReferenceOfMasterUpdateList.get(),brandId,modifiedDate);
                List<String> updateMasterColumns = getTableColumns(updateMasterDataList,MASTER_INVENTORY_TABLE_PREFIX+ SEPARATOR_UNDERSCORE);
                triggerAction(updateMasterDataList,updateMasterColumns,MASTER_INVENTORY_TABLE_PREFIX+ SEPARATOR_UNDERSCORE,
                        MASTER_INVENTORY_TABLE_NAME, UPDATE_ACTION,false);
                phaser.arriveAndAwaitAdvance();
            }
            updated = true;
        }
        catch (Exception e){
            updated = false;
            logger.info("Exception in inventory while updating data.",e.getMessage(),e);
        } finally {
            if(null != connection && connection.isClosed()){
                connection.close();
            }
        }
//        connection.rollback();
        if(commitData.get()){
            connection.commit();
            connection.close();
        }
        else{
            connection.rollback();
            connection.close();
            return;
        }
        logger.info("Sending final response from Persistence class as : "+updated);
        response.compareAndSet(false,updated);
        countDownLatch.countDown();
    }

    private void triggerAction(List<Map<String,Object>> updateList, List<String> tableColumns,String tableNamePrefix, String tableName,
                               String action, Boolean insertIntoInventoryOnly){
        int threshold = 10;
        List<List<Map<String,Object>>> partitions = new ArrayList<>();
        if(updateList.size() > threshold){
            int groupSize = updateList.size() / threshold;
            partitions = ListUtils.partition(updateList,groupSize);
        }
        else{
            partitions.add(updateList);
        }
        for(List<Map<String,Object>> part : partitions){
            phaser.register();
            UpdateOrInsert updateOrInsert = new UpdateOrInsert(part,tableNamePrefix,tableName, tableColumns,action,phaser,connection,insertIntoInventoryOnly);
            Thread thread = new Thread(updateOrInsert);
            thread.setUncaughtExceptionHandler(this);
            thread.start();
        }
    }


    public AtomicReference<List<Long>> getAtomicReferenceOfMasterUpdateList() {
        return atomicReferenceOfMasterUpdateList;
    }

    public void setAtomicReferenceOfMasterUpdateList(UnaryOperator<List<Long>> listToAddToAtomicReference) {
        this.atomicReferenceOfMasterUpdateList.updateAndGet(listToAddToAtomicReference);
    }



    @Override
    public void uncaughtException(Thread t, Throwable e) {
        commitData.compareAndSet(true,false);
        logger.error(e.getMessage(),e);
        countDownLatch.countDown(); // error response, no need to set atomicBoolean
    }

    @Override
    public void run() {
        try {
            updateOrSaveData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
