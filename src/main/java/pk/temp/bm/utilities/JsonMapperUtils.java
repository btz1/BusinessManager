package pk.temp.bm.utilities;

import com.amazonaws.util.json.JSONArray;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonMapperUtils {

    public final static ObjectMapper jsonmapper = new ObjectMapper();

    public String sentenceCase(final String line) {
		String[] splited = line.split(" +");
		String result = "";
		String resultdash = "";
		for (int i = 0; i < splited.length; i++) {
			if (i != 0) {
				result = result + " ";
			}

			resultdash = Character.toUpperCase(splited[i].charAt(0))
					+ splited[i].substring(1).toLowerCase();
			String[] spliteddash = splited[i].split("-");
			String resultd = "";

			if (spliteddash.length > 1) {
				for (int k = 0; k < spliteddash.length; k++) {
					if (k != 0) {
						resultd = resultd + "-";
					}
					resultd = resultd
							+ Character.toUpperCase(spliteddash[k].charAt(0))
							+ spliteddash[k].substring(1).toLowerCase();
				}
				result = resultd;
			} else {
				result = result + resultdash;

			}
		}
		return result;
	}
    
    public  String customOptionMapping(String customOptions){
    	
    	StringBuilder sb = new StringBuilder();
    	if(!customOptions.equals("N/A")){
    		try{
    			JSONArray jsonarray = new JSONArray(customOptions);
    			String label=null;
    			String value=null;
    			for (int i = 0; i < jsonarray.length(); i++) {
    			    com.amazonaws.util.json.JSONObject jsonobject = jsonarray.getJSONObject(i);
    	
    			    label = jsonobject.getString("label");
    			    value = jsonobject.getString("value");
    			    
    			    sb.append(label);
    			    sb.append(":");
    			    sb.append(value);
    			    sb.append(".");
    			}
    		}catch(Exception es){
    			
    		}
    	}
    	else{
    		sb.append("N/A");
    	}
		return sb.toString();
    }
    
    public String categoryMapping(String category){
    	StringBuilder sb = new StringBuilder();
    	if(!category.equals("N/A")){
    		try{
    			JSONArray jsonarrayList = new JSONArray(category);
    			
    			for(int i=0; i < jsonarrayList.length() ; i++){
    				sb.append(jsonarrayList.getString(i));
    				sb.append("-");
    			}
    			
    		}catch(Exception ex){
    			
    		}
    	}
    	else{
    		sb.append("N/A");
    	}
    	return sb.toString();
    }

}