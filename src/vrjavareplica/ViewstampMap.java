/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vrjavareplica;

import java.util.HashMap;

/**
 *
 * @author Karol
 */
public class ViewstampMap extends HashMap<Viewstamp, Integer>{
    
    public void add(Viewstamp viewstamp) {
        if(containsKey(viewstamp)) {
            int count = get(viewstamp);
            count++;
            remove(viewstamp);
            put(viewstamp, count);
        } else {
            put(viewstamp, 1);
        }
    }
}
