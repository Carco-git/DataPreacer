package top.preacer.database.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SelectUtil {
	public static List<String> handleProp(Map<String, List<String>> projectionMap) {
		List<String> result = new LinkedList<String>();
		for (Map.Entry<String, List<String>> projectionEntry : projectionMap.entrySet()) {
            String projectionKey = projectionEntry.getKey();
            List<String> projectionValues = projectionEntry.getValue();
            for (String projectionValue : projectionValues) {
            	result.add(projectionKey + "." + projectionValue);
            }

        }
		return result;
	}
}
