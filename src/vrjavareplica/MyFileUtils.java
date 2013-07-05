package vrjavareplica;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is used as utility to load data from files.
 * @author Karol
 */
public class MyFileUtils {

	/**
     * Reads data from specified file.
     * @param path Path to file.
     * @return 2-dimensional array of data.
     */
    public static ArrayList<ArrayList<String>> loadFile(String path)
	{
		FileReader fr = null;
		String line = "";
		ArrayList<ArrayList<String>> dataArray = new ArrayList<>();
		
		try {
			fr = new FileReader(path);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
		
		BufferedReader br = new BufferedReader(fr);
		ArrayList<String> tokens = new ArrayList<>();
		
		try {
			while((line = br.readLine())!=null){
				tokens = parseLine(line);
				dataArray.add(tokens);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			fr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return dataArray;
	}
        
        /**
     * Reads data from specified file.
     * @param file A file to read from.
     * @return 2-dimensional array of data.
     */
    public static ArrayList<ArrayList<String>> loadFile(File file)
	{
		FileReader fr = null;
		String line = "";
		ArrayList<ArrayList<String>> dataArray = new ArrayList<>();
		
		try {
			fr = new FileReader(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
		
		BufferedReader br = new BufferedReader(fr);
		ArrayList<String> tokens = new ArrayList<>();
		
		try {
			while((line = br.readLine())!=null){
				tokens = parseLine(line);
				dataArray.add(tokens);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			fr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return dataArray;
	}
	
	private static ArrayList<String> parseLine(String line)
	{
		StringTokenizer st = new StringTokenizer(line);
		ArrayList<String> tokens = new ArrayList<>();
		
		while(st.hasMoreTokens()){
			tokens.add(st.nextToken());
		}
		
		return tokens;
	}
        
        /**
     * Saves text into a specified file.
     * @param file A file to write to.
     * @param text A text to write to file.
     * @throws FileNotFoundException
     */
    public static void saveFile(File file, String text) throws FileNotFoundException {
            PrintWriter out = new PrintWriter(file);
            out.println(text);
            out.close();
        }
    
    public static boolean saveFile(String path, byte[] file ) {
        FileOutputStream fos = null;
        boolean result = false;
        try {
            fos = new FileOutputStream(Constants.FILE_DIRECTORY + path);
            fos.write(file);
            fos.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MyFileUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MyFileUtils.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fos.close();
                result = true;
            } catch (IOException ex) {
                Logger.getLogger(MyFileUtils.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                return result;
            }
        }
    }
    
    public static boolean deleteFile(String path) {
        boolean result = false;
        File file = new File(Constants.FILE_DIRECTORY + path);
        result = file.delete();
        return result;        
    }
}
