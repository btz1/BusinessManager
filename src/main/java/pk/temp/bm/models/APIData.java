package pk.temp.bm.models;

import java.util.List;

public class APIData {

	private String referenceId;
	
	private String callbackURL;
	
	private String locationId;
	
	private String locationCode;
	
	private int batch;

	private int batchSize;
	
	private List<ProductData> products;

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	public String getCallbackURL() {
		return callbackURL;
	}

	public void setCallbackURL(String callbackURL) {
		this.callbackURL = callbackURL;
	}

	public List<ProductData> getProducts() {
		return products;
	}

	public void setProducts(List<ProductData> productData) {
		this.products = productData;
	}
	
	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public int getBatch() {
		return batch;
	}

	public void setBatch(int batchId) {
		this.batch = batchId;
	}

	public int getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	public String getLocationCode() {
		return locationCode;
	}

	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}

}
