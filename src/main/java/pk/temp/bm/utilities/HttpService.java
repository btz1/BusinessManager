package pk.temp.bm.utilities;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

public class HttpService {

	private HttpClient client;
	private String endPoint;
	private String method;
	private int statusCode;
	private NameValuePair[] params;
	
	public HttpService(String meth, NameValuePair[] parameters) {
		client = new HttpClient();
		this.method = meth;
		this.params = parameters;
	}
	
	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	


	public String getEndPoint() {
		return endPoint;
	}



	public void setEndPoint(String endPoint) {
		this.endPoint = endPoint;
	}



	public String getResponseAsString() throws Exception {
		String response = null;
		if(this.method.equals("get")){
			GetMethod method = null;
			try {
				method = new GetMethod(this.endPoint);
				method.setQueryString(params);
				this.statusCode = this.client.executeMethod(method);
				response = method.getResponseBodyAsString();
			} catch(Exception e) {
				throw e;
			} finally {
				if (null != method) {
					method.releaseConnection();
				}
			}
		}else if(this.method.equals("post")){
			PostMethod method = null;
			try {
				method = new PostMethod(this.endPoint);
				method.setRequestBody(params);
				this.statusCode = this.client.executeMethod(method);
				response = method.getResponseBodyAsString();
			} catch (Exception e) {
				throw e;
			} finally {
				if (null != method) {
					method.releaseConnection();
				}
			}
			method.releaseConnection();
		}
		return response;
	}

}
