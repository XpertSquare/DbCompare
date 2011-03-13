package DbCompare.Data.Mock;

import java.util.HashMap;
import java.util.Map;

public class IdGenerator
{
    static Map<String, Integer> idTable = new HashMap<String, Integer>();
    
    public static String GetNextID(String entityType)
    {
        int nextID = 0;
        if (idTable.containsKey(entityType))
        {
            nextID = (int)idTable.get(entityType) + 1;
            
            idTable.put(entityType,nextID);
        }
        else
        {
            idTable.put(entityType, nextID);
        }
        return entityType + nextID;
    }
    
    public static void ResetID(String entityType)
    {
    		 idTable.put(entityType,new Integer(0));
    }
}
