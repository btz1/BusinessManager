package pk.temp.bm.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.DateUtil;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Abubakar on 7/24/2017.
 */
public class BMDateUtils {

	private static final Logger logger = LoggerFactory.getLogger(BMDateUtils.class.getName());

	
    public static Date stringToDate(String date) {
        return stringToDateWithGivenFormat(date,"yyyy-MM-dd");
    }
    
	public static final SimpleDateFormat simpleFormetter = new SimpleDateFormat("yyyy-MM-dd");
	
	public static final SimpleDateFormat mysqlDateAndTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static final DateTimeFormatter dateTimeformatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

	public static String getMySQLDateString(Date date){
		return simpleFormetter.format(date);
	}

	public static Integer getCurrentMonth(){
	    Calendar calendar = Calendar.getInstance();
	    return calendar.get(Calendar.MONTH + 1);
    }
	
	public static Date changeTimeStampToDateOnly(Date date){
		try {
		 	return simpleFormetter.parse(simpleFormetter.format(date));		
		} catch (ParseException e) {
			logger.error("Exception:"+e.getMessage());
			return null;
		}
	}
	
	public static Date parseMiniDate(String date){
		try {
			return simpleFormetter.parse(date);
		} catch (ParseException e) {
			logger.error("Exception:"+e.getMessage());
			return null;
		}
	}
	
	
	public static String toTimeStampString(Date date){
		return mysqlDateAndTimeFormat.format(date);
	}
	
	public static Date parseTimeStamp(String date){
		try {
			return mysqlDateAndTimeFormat.parse(date);
		} catch (ParseException e) {
			return null;
		}
	}
	
	public static Date parseInvoiceDate(String date){
		if (date != null && !date.equals("")){
		try {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
		} catch (ParseException e) {
			try {
				return new SimpleDateFormat("dd, MMM, yy").parse(date);
			} catch (ParseException ex) {
				try {
					return new SimpleDateFormat("MM/dd/yyyy").parse(date);
				} catch (ParseException ex2) {
					Date javaDate = DateUtil.getJavaDate(Double.parseDouble(date));
					return javaDate;
				}
			}
		}
		}else{
			return null;
		}
	}
	
    public static Date stringToDateWithGivenFormat(String date, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Date formattedDate = null;
        try {
            formattedDate =  formatter.parse(date);
        } catch (ParseException e) {
        	logger.error("Exception:"+e.getMessage());
        }
        return formattedDate;
    }


    public static Date getDateForStartOfDay(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        return calendar.getTime();
    }

    public static String getStringDateForStartOfDay(Date date){
        date = getDateForStartOfDay(date);
        return getMySQLDateString(date);
    }

    public static Date getStartDateOfMonth(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        return calendar.getTime();
    }

    public static Date getEndDateOfMonth(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return calendar.getTime();
    }

    public static Date getDateForEndOfDay(Date date){
        Calendar calendar = Calendar.getInstance();
        date = getDateForStartOfDay(date);
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY,23);
        calendar.set(Calendar.MINUTE,59);
        calendar.set(Calendar.SECOND,59);
        return calendar.getTime();
    }

    public static String getStringDateForEndOfDay(Date date){
        date = getDateForEndOfDay(date);
        return getMySQLDateString(date);
    }

    public static Date parseAnyStringToDate(String strDate){
    	String format = determineDateFormat(strDate);
    	if(null != format){
			return stringToDateWithGivenFormat(strDate,format);
		}
		return null;
	}

	private static final Map<String, String> DATE_FORMAT_REGEXPS = new HashMap<String, String>() {{
		put("^\\d{8}$", "yyyyMMdd");
		put("^\\d{1,2}-\\d{1,2}-\\d{4}$", "dd-MM-yyyy");
		put("^\\d{4}-\\d{1,2}-\\d{1,2}$", "yyyy-MM-dd");
		put("^\\d{1,2}/\\d{1,2}/\\d{4}$", "MM/dd/yyyy");
		put("^\\d{4}/\\d{1,2}/\\d{1,2}$", "yyyy/MM/dd");
		put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}$", "dd MMM yyyy");
		put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}$", "dd MMMM yyyy");
		put("^\\d{12}$", "yyyyMMddHHmm");
		put("^\\d{8}\\s\\d{4}$", "yyyyMMdd HHmm");
		put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}$", "dd-MM-yyyy HH:mm");
		put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}$", "yyyy-MM-dd HH:mm");
		put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}$", "MM/dd/yyyy HH:mm");
		put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}$", "yyyy/MM/dd HH:mm");
		put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}$", "dd MMM yyyy HH:mm");
		put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}$", "dd MMMM yyyy HH:mm");
		put("^\\d{14}$", "yyyyMMddHHmmss");
		put("^\\d{8}\\s\\d{6}$", "yyyyMMdd HHmmss");
		put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd-MM-yyyy HH:mm:ss");
		put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$", "yyyy-MM-dd HH:mm:ss");
		put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "MM/dd/yyyy HH:mm:ss");
		put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$", "yyyy/MM/dd HH:mm:ss");
		put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd MMM yyyy HH:mm:ss");
		put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd MMMM yyyy HH:mm:ss");
	}};

	public static String determineDateFormat(String dateString) {
		for (String regexp : DATE_FORMAT_REGEXPS.keySet()) {
			if (dateString.toLowerCase().matches(regexp)) {
				return DATE_FORMAT_REGEXPS.get(regexp);
			}
		}
		return null; // Unknown format.
	}

	
}
