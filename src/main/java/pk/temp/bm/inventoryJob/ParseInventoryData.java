package pk.temp.bm.inventoryJob;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pk.temp.bm.models.APIData;
import pk.temp.bm.models.ProductData;
import pk.temp.bm.utilities.GlobalConstants;

import java.util.*;
import java.util.stream.Collectors;

import static pk.temp.bm.utilities.GlobalConstants.*;

public class ParseInventoryData {

    private Sheet sheet;
    private Long brandId;
    String filePath;
    private Long locationId;
    private Date currentDate;
    private int headerColumnNumber;
    private APIData apiData;
    private boolean partialInventoryUpdate;

    private static final Logger logger = LoggerFactory.getLogger(ParseInventoryData.class);

    public ParseInventoryData(Sheet sheet, Long brandId, Long locationId, int headerColumnNumber, Date currentDate, boolean partialInventoryUpdate){
        this.sheet = sheet;
        this.brandId = brandId;
        this.locationId = locationId;
        this.currentDate = currentDate;
        this.headerColumnNumber = headerColumnNumber;
        this.partialInventoryUpdate = partialInventoryUpdate;
    }

    public ParseInventoryData(APIData apiData, Long brandId, Long locationId, Date currentDate, boolean partialInventoryUpdate){
        this.apiData = apiData;
        this.brandId = brandId;
        this.locationId = locationId;
        this.currentDate = currentDate;
        this.partialInventoryUpdate = partialInventoryUpdate;
    }

    private LoadResources loadResources = new LoadResources();

    public Map<String,Object> parseFileData(){
        Map<String,Object> insertAndUpdateDataListMap = new HashMap<>();
        try{
            boolean ignoreFileRow = false; // to ignore file row if some condition is not met
            int sheetSize = sheet.getLastRowNum() + 1;
            if(sheetSize > 1){
                /* -Following block loads required data before processing file contents.
                *  -Resources contain data from products, product_detail and inventory_location.
                *  -Inventory Settings contains number of columns to be persisted against each row
                *   and also the column number to read from file for each db column.
                * */
                Map<String,Object> resourceMap = loadResources.loadExistingProducts(brandId,locationId);
                boolean inventoryExists = resourceMap.containsKey(LOCATION_INVENTORY_EXIST_KEY) && (boolean) resourceMap.get(LOCATION_INVENTORY_EXIST_KEY);
                logger.info("Existing Products : "+resourceMap.size());
                Map<String,Object> inventorySettings = loadResources.loadInventorySettings(brandId);
                List<Map<String,Object>> insertDataList = new ArrayList<>();
                List<Map<String,Object>> updateDataList = new ArrayList<>();
                List<Long> productIdsToUpdateMaster = new ArrayList<>();
                List<String> skuListFromFile = new ArrayList<>();
                Map<Long,Long> bufferMap = loadResources.getBufferValue(locationId);
                List<Map<String,Object>> insertInventoryDataList = new ArrayList<>();
                Boolean insertIntoInventoryOnly = false;


                // go through file, row by row
                if(!inventorySettings.isEmpty()){
                    logger.info("Starting file parsing...");
                    for (int i = sheet.getFirstRowNum() + headerColumnNumber; i < sheetSize; i++){

                        boolean addOrUpdateProductDetail = false;
                        Map<String,Object> rowDataMap = new HashMap<>();
                        Row row = sheet.getRow(i);
                        for(Map.Entry<String,Object> setting : inventorySettings.entrySet()){
                            String propertyName = setting.getKey(); // column name to be persisted
                            String propertyValue = String.valueOf(setting.getValue()); // column position in file
                            String[] mergedColumns = propertyValue.split(SEPARATOR_UNDERSCORE);
                            String[] fileToDbMapping = propertyName.split(SEPARATOR_DOT);
                            List<String> fileColsToReadFrom = new ArrayList<String>();
                            String dbColToMapTo = null;
                            String finalValueToWriteToDb = "";
                            String columnPrefix = null;
                            if (fileToDbMapping.length > 2 && mergedColumns.length == 1) {
                                columnPrefix = fileToDbMapping[0]+ SEPARATOR_UNDERSCORE;
                                fileColsToReadFrom.add(mergedColumns[0]); // reading with column positions from file
                                dbColToMapTo = fileToDbMapping[2];
                            }
                            else if (fileToDbMapping.length ==2){
                                /* this block combines multiple column values to generate a single value to persisted in DB
                                     product data (with merged columns)*/
                                for(String colPosition : mergedColumns) {
                                    fileColsToReadFrom.add(colPosition); // reading with column positions from file
                                }
                                columnPrefix = fileToDbMapping[0]+ SEPARATOR_UNDERSCORE; // reading dynamic prefix
                                dbColToMapTo = fileToDbMapping[1];
                            }
                            // calculate DB column value
                            int counter = 1;
                            for (String fileCol : fileColsToReadFrom) {
                                String filler = "";
                                if (counter != 1 && counter != fileColsToReadFrom.size() + 1) {
                                    filler = SEPARATOR_DASH;
                                }
                                Cell cell = row.getCell(Integer.valueOf(fileCol));
                                DataFormatter formatter = new DataFormatter();
                                finalValueToWriteToDb += filler + formatter.formatCellValue(row.getCell(Integer.valueOf(fileCol)));
                                if(null != cell && cell.getCellType() != cell.CELL_TYPE_BLANK){
                                    if(cell.getCellType() == cell.CELL_TYPE_NUMERIC){
                                        if (dbColToMapTo != null && dbColToMapTo.contains(GlobalConstants.STORE_QUANTITY)) {
                                            finalValueToWriteToDb = formatInventoryQuantity(finalValueToWriteToDb,bufferMap);
                                        }
                                    }
                                    if(dbColToMapTo.contains(PRODUCT_PRICE)){
                                        finalValueToWriteToDb = formatNumber(finalValueToWriteToDb);
//                                        logger.info("Price Col : "+ dbColToMapTo + " : " + finalValueToWriteToDb);
                                    }
                                    finalValueToWriteToDb = finalValueToWriteToDb.replaceAll(" ",SEPARATOR_DASH); // replacing spaces in sku with dashes
                                }
                                else if(dbColToMapTo != null && dbColToMapTo.contains(GlobalConstants.PRODUCT_SKU)) {
                                    // Ignore file row in case any of the sku columns are null or empty.
                                    logger.warn("Ignored Row Due to Empty Sku Value from File : "+row.getRowNum());
                                    ignoreFileRow = true;
                                    break;
                                }
                                counter++;
                            }

                            String lookupKey = columnPrefix + dbColToMapTo;
                            rowDataMap.put(lookupKey,finalValueToWriteToDb);
                            if(lookupKey.contains(PRODUCT_DETAIL_TABLE_PREFIX)){
                                addOrUpdateProductDetail = true;
                            }
                        }
                        if(!ignoreFileRow){

                            // check if upc exist already, add to update list
                            String resourceLookupKey = (String) rowDataMap.get(PRODUCT_TABLE_PREFIX+ SEPARATOR_UNDERSCORE + PRODUCT_SKU);
                            resourceLookupKey = resourceLookupKey.replaceAll(SEPARATOR_DASH,"").toLowerCase(); // to remove dashes from sku, so that map look will work
                            skuListFromFile.add(resourceLookupKey); // to track upc that exist in DB but did not come with file
                            if(resourceMap.containsKey(resourceLookupKey.toLowerCase())){ // sku exist already, just update
                                rowDataMap = addToRowForUpdate(resourceMap,resourceLookupKey,rowDataMap,addOrUpdateProductDetail);
                                productIdsToUpdateMaster.add(Long.valueOf(rowDataMap.get(PRODUCT_TABLE_PREFIX+ SEPARATOR_UNDERSCORE + DEFAULT_KEY_COLUMN).toString()));
                                updateDataList.add(rowDataMap);

                                if(!inventoryExists){
                                    // prepare a row to inset into inventory, as the product exists, but inventory for that location doesn't
                                    Map<String,Object> inventoryRowDataMap = createNewRowForInventory((String) rowDataMap.get(INVENTORY_TABLE_PREFIX+ SEPARATOR_UNDERSCORE + STORE_QUANTITY),
                                            rowDataMap.get(PRODUCT_TABLE_PREFIX+ SEPARATOR_UNDERSCORE + DEFAULT_KEY_COLUMN),bufferMap);
                                    insertIntoInventoryOnly = true;
                                    insertInventoryDataList.add(inventoryRowDataMap);
                                    rowDataMap.remove(INVENTORY_TABLE_PREFIX+ SEPARATOR_UNDERSCORE + STORE_QUANTITY); // removing inventory_location table entry from update map
                                }
                            }
                            // otherwise add as a new product
                            else{
                                rowDataMap = addToRowForInsert(resourceLookupKey,rowDataMap,addOrUpdateProductDetail);
                                insertDataList.add(rowDataMap);
                            }
                        }
                    }
                    insertAndUpdateDataListMap.put("insertList",insertDataList);
                    insertAndUpdateDataListMap.put("updateList",updateDataList);
                    insertAndUpdateDataListMap.put("productIdsToUpdateMaster",productIdsToUpdateMaster);
                    insertAndUpdateDataListMap.put("insertInventoryDataList",insertInventoryDataList);
                    insertAndUpdateDataListMap.put("insertIntoInventoryOnly",insertIntoInventoryOnly);
                }

                if(!partialInventoryUpdate && inventoryExists){
                    insertAndUpdateDataListMap.put("inventoryIdListToSetZero",getSkusToMarkZero(resourceMap,skuListFromFile));
                }
                logger.info("File Parsing completed.");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return insertAndUpdateDataListMap;
    }

    public Map<String,Object> parseAPIData() throws Exception{
        Map<String,Object> insertAndUpdateDataListMap = new HashMap<>();
        Map<String,Object> resourceMap = loadResources.loadExistingProducts(brandId,locationId);
        logger.info("Existing Products : "+resourceMap.size());
        List<Map<String,Object>> insertDataList = new ArrayList<>();
        List<Map<String,Object>> updateDataList = new ArrayList<>();
        List<Map<String,Object>> insertInventoryDataList = new ArrayList<>();
        List<Long> productIdsToUpdateMaster = new ArrayList<>();
        List<String> skuListFromAPIData = new ArrayList<>();
        Map<Long,Long> bufferMap = loadResources.getBufferValue(locationId);
        boolean inventoryExists = resourceMap.containsKey(LOCATION_INVENTORY_EXIST_KEY) && (boolean) resourceMap.get(LOCATION_INVENTORY_EXIST_KEY);
        Boolean insertIntoInventoryOnly = false;

        for(ProductData productData : apiData.getProducts()){
            Map<String,Object> rowDataMap = new HashMap<>();
            String plainSku = productData.getSKU().replaceAll(SEPARATOR_DASH,"").replaceAll(" ","").toLowerCase();
            rowDataMap.put(PRODUCT_TABLE_PREFIX + SEPARATOR_UNDERSCORE + PRODUCT_PRICE, productData.getPrice());
            rowDataMap.put(PRODUCT_TABLE_PREFIX + SEPARATOR_UNDERSCORE + PRODUCT_SPECIAL_PRICE, productData.getSpecialPrice());
            rowDataMap.put(PRODUCT_TABLE_PREFIX + SEPARATOR_UNDERSCORE + PRODUCT_NAME, productData.getName());
            rowDataMap.put(PRODUCT_TABLE_PREFIX + SEPARATOR_UNDERSCORE + PRODUCT_UPC, productData.getUPC());
            rowDataMap.put(PRODUCT_TABLE_PREFIX + SEPARATOR_UNDERSCORE + PRODUCT_SKU, productData.getSKU());

            skuListFromAPIData.add(plainSku);

            if(resourceMap.containsKey(plainSku)){
                rowDataMap = addToRowForUpdate(resourceMap,plainSku,rowDataMap,false);
                productIdsToUpdateMaster.add(Long.valueOf(rowDataMap.get(PRODUCT_TABLE_PREFIX+ SEPARATOR_UNDERSCORE + DEFAULT_KEY_COLUMN).toString()));
                if(inventoryExists){ // update inventory if exists against this location
                    rowDataMap.put(INVENTORY_TABLE_PREFIX + SEPARATOR_UNDERSCORE + STORE_QUANTITY, formatInventoryQuantity(String.valueOf(productData.getQuantity()),bufferMap));
                }
                else{ // prepare a row to inset into inventory, as the product exists, but inventory for that location doesn't
                    Map<String,Object> inventoryRowDataMap = createNewRowForInventory(String.valueOf(productData.getQuantity()),
                                    rowDataMap.get(PRODUCT_TABLE_PREFIX+ SEPARATOR_UNDERSCORE + DEFAULT_KEY_COLUMN),bufferMap);
                    insertIntoInventoryOnly = true;
                    insertInventoryDataList.add(inventoryRowDataMap);
                }
                updateDataList.add(rowDataMap);
            }else{
                rowDataMap.put(INVENTORY_TABLE_PREFIX + SEPARATOR_UNDERSCORE + STORE_QUANTITY, formatInventoryQuantity(String.valueOf(productData.getQuantity()),bufferMap));
                rowDataMap = addToRowForInsert(plainSku,rowDataMap,false);
                insertDataList.add(rowDataMap);
            }

        }

        insertAndUpdateDataListMap.put("insertList",insertDataList);
        insertAndUpdateDataListMap.put("updateList",updateDataList);
        insertAndUpdateDataListMap.put("productIdsToUpdateMaster",productIdsToUpdateMaster);
        insertAndUpdateDataListMap.put("insertInventoryDataList",insertInventoryDataList);
        insertAndUpdateDataListMap.put("insertIntoInventoryOnly",insertIntoInventoryOnly);

        if(!partialInventoryUpdate && inventoryExists){
            insertAndUpdateDataListMap.put("inventoryIdListToSetZero",getSkusToMarkZero(resourceMap,skuListFromAPIData));
        }
        return insertAndUpdateDataListMap;
    }

    private Map<String,Object> addToRowForUpdate(Map<String,Object> resourceMap, String resourceLookupKey, Map<String,Object> rowDataMap, boolean addOrUpdateProductDetail){
        List<Object> existingDataList = (List<Object>) resourceMap.get(resourceLookupKey);
        rowDataMap.put(PRODUCT_TABLE_PREFIX+ SEPARATOR_UNDERSCORE + DEFAULT_KEY_COLUMN ,existingDataList.get(INDEX_OF_PRODUCT_ID));
        rowDataMap.put(PRODUCT_TABLE_PREFIX+ SEPARATOR_UNDERSCORE +"last_udate",currentDate);

        if(addOrUpdateProductDetail){
            rowDataMap.put(PRODUCT_DETAIL_TABLE_PREFIX+ SEPARATOR_UNDERSCORE + DEFAULT_KEY_COLUMN,existingDataList.get(INDEX_OF_PRODUCT_DETAIL_ID));
            rowDataMap.put(PRODUCT_DETAIL_TABLE_PREFIX+ SEPARATOR_UNDERSCORE +"last_modified",currentDate);
        }

        boolean inventoryExists = resourceMap.containsKey(LOCATION_INVENTORY_EXIST_KEY) && (boolean) resourceMap.get(LOCATION_INVENTORY_EXIST_KEY);
        if(inventoryExists){
            rowDataMap.put(INVENTORY_TABLE_PREFIX+ SEPARATOR_UNDERSCORE + DEFAULT_KEY_COLUMN,existingDataList.get(INDEX_OF_INVENTORY_ID));
            rowDataMap.put(INVENTORY_TABLE_PREFIX+ SEPARATOR_UNDERSCORE +"last_modified",currentDate);
        }
        return rowDataMap;
    }

    private Map<String,Object> addToRowForInsert(String resourceLookupKey, Map<String,Object> rowDataMap, boolean addOrUpdateProductDetail){
        rowDataMap.put("plainSku",resourceLookupKey);
        rowDataMap.put(PRODUCT_TABLE_PREFIX + SEPARATOR_UNDERSCORE +"brand_id",brandId);
        rowDataMap.put(PRODUCT_TABLE_PREFIX + SEPARATOR_UNDERSCORE +"last_udate",currentDate);
        rowDataMap.put(PRODUCT_TABLE_PREFIX + SEPARATOR_UNDERSCORE +"created_date",currentDate);

        if(addOrUpdateProductDetail){
            rowDataMap.put(PRODUCT_DETAIL_TABLE_PREFIX+ SEPARATOR_UNDERSCORE +"last_modified",currentDate);
            rowDataMap.put(PRODUCT_DETAIL_TABLE_PREFIX+ SEPARATOR_UNDERSCORE +"created",currentDate);
        }

        rowDataMap.put(INVENTORY_TABLE_PREFIX + SEPARATOR_UNDERSCORE +"brand_id",brandId);
        rowDataMap.put(INVENTORY_TABLE_PREFIX + SEPARATOR_UNDERSCORE +"location_id",locationId);
        rowDataMap.put(INVENTORY_TABLE_PREFIX + SEPARATOR_UNDERSCORE +"last_modified",currentDate);
        rowDataMap.put(INVENTORY_TABLE_PREFIX + SEPARATOR_UNDERSCORE +"created_date",currentDate);

        rowDataMap.put(MASTER_INVENTORY_TABLE_PREFIX + SEPARATOR_UNDERSCORE +"brand_id",brandId);
        rowDataMap.put(MASTER_INVENTORY_TABLE_PREFIX + SEPARATOR_UNDERSCORE +"last_modified",currentDate);
        return rowDataMap;
    }

    private Map<String,Object> createNewRowForInventory(String inventoryQuantity,Object productId, Map<Long,Long> bufferMap){
        Map<String,Object> inventoryRowDataMap = new HashMap<>();
        inventoryRowDataMap.put(INVENTORY_TABLE_PREFIX+ SEPARATOR_UNDERSCORE + "product_id", productId); // getting product_id from rowDataMap, as it was populated earlier.
        inventoryRowDataMap.put(INVENTORY_TABLE_PREFIX + SEPARATOR_UNDERSCORE + STORE_QUANTITY, formatInventoryQuantity(inventoryQuantity,bufferMap));
        inventoryRowDataMap.put(INVENTORY_TABLE_PREFIX + SEPARATOR_UNDERSCORE +"brand_id",brandId);
        inventoryRowDataMap.put(INVENTORY_TABLE_PREFIX + SEPARATOR_UNDERSCORE +"location_id",locationId);
        inventoryRowDataMap.put(INVENTORY_TABLE_PREFIX + SEPARATOR_UNDERSCORE +"last_modified",currentDate);
        inventoryRowDataMap.put(INVENTORY_TABLE_PREFIX + SEPARATOR_UNDERSCORE +"created_date",currentDate);
        return inventoryRowDataMap;
    }

    private List<Map<String,Object>> getSkusToMarkZero(Map<String,Object> resourceMap, List<String> inComingSkuList){

        // fetch sku list to set their quantity to zero as they were not provided in the file but they exist in store.
        List<Object> invIdListToSetZero;
        invIdListToSetZero = resourceMap.entrySet().stream().filter(entry -> !inComingSkuList.contains(entry.getKey()))
                .map(map -> {
                    if(map.getValue() instanceof List){
                        List<Object> values = (List<Object>) map.getValue();
                        return values.get(GlobalConstants.INDEX_OF_INVENTORY_ID);
                    }
                    else {
                        return 0;
                    }
                }).collect(Collectors.toList());
        List<Map<String,Object>> skuToMarkZeroDataList = new ArrayList<>();
        for(Object inventoryId : invIdListToSetZero){
            Map<String,Object> map = new HashMap<>();
            map.put(INVENTORY_TABLE_PREFIX+ SEPARATOR_UNDERSCORE + DEFAULT_KEY_COLUMN,inventoryId);
            map.put(INVENTORY_TABLE_PREFIX+ SEPARATOR_UNDERSCORE + STORE_QUANTITY,0);
            skuToMarkZeroDataList.add(map);
        }
        return skuToMarkZeroDataList;
    }

    private String formatInventoryQuantity(String finalValueToWriteToDb, Map<Long,Long> bufferMap){
        finalValueToWriteToDb = formatNumber(finalValueToWriteToDb);
        finalValueToWriteToDb = finalValueToWriteToDb.contains(".") ?
                finalValueToWriteToDb.substring(0, finalValueToWriteToDb.indexOf('.')) : finalValueToWriteToDb;
        Long longFinalValue = Long.valueOf(finalValueToWriteToDb);
        if(bufferMap.containsKey(longFinalValue)){
            longFinalValue -= bufferMap.get(longFinalValue);
        }else{
            longFinalValue -= bufferMap.get(-1L); // -1 is default key to get value to subtract if not specified otherwise
        }
        finalValueToWriteToDb = longFinalValue.toString();
        return finalValueToWriteToDb;
    }

    private String formatNumber(String finalValueToWriteToDb){
        // removes all non numeric characters.
        return finalValueToWriteToDb.replaceAll("[^\\d.]", "");
    }
}
