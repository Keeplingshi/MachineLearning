package trash;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.google.common.collect.Maps;
import com.model.EventEnum;

/**
 * 事件处理过程
 */
public class EventParser {
    
    /**
     * 获取事件触发词及其所代表事件类型
     * @param path
     * @return
     */
    public static Map<EventEnum, Map<String, Integer>> parseEvent(String path){

    	Map<EventEnum, Map<String, Integer>> triggerMap=new HashMap<EventEnum, Map<String,Integer>>();
    	
		Workbook originWb=null;
		
		try {
			originWb = WorkbookFactory.create(new File(path));
		} catch (EncryptedDocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		}
		
		Sheet originSheet = originWb.getSheetAt(0);
		Cell triggerCell=null;
		Cell typeCell=null;
    	
		for(Row row:originSheet)
		{
			if(row.getRowNum()==0){
				continue;
			}
			
			//事件类型
			typeCell=row.getCell(7);
			String type=null;
			if(typeCell!=null&&!"".equals(typeCell.toString().trim()))
			{
	        	if((typeCell.toString().trim()).equals("地震")){
	        		type="Earthquake";
	        	}else if((typeCell.toString().trim()).equals("火灾")){
	        		type="Fire";
	        	}else if((typeCell.toString().trim()).equals("交通事故")){
	        		type="Accident";
	        	}else if((typeCell.toString().trim()).equals("恐怖袭击")){
	        		type="Terror";
	        	}else{
	        		type="FoodPoison";
	        	}

	        	
				//触发词
				triggerCell=row.getCell(5);
				String triggerWord="";
				if(triggerCell!=null){
					triggerWord=triggerCell.toString().trim();
				}
	        	EventEnum eventEnum=EventEnum.valueOf(type);
	        	
	        	Map<String, Integer> triggerCountMap = triggerMap.get(eventEnum);
	            if(null == triggerCountMap){
	                triggerCountMap = Maps.newHashMap();
	                triggerCountMap.put(triggerWord, 1);
	            }else{
	                if(triggerCountMap.containsKey(triggerWord)){
	                    triggerCountMap.put(triggerWord, triggerCountMap.get(triggerWord)+1);
	                }
	                else{
	                    triggerCountMap.put(triggerWord, 1);
	                }
	            }
	            triggerMap.put(eventEnum, triggerCountMap);
	        	
			}
			
		}
		
        return triggerMap;
    }
}
