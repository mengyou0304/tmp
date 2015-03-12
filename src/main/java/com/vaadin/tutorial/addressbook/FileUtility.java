package com.vaadin.tutorial.addressbook;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * 
 * @author Robin
 * @date 2009-10-20 03:53:32
 * 
 */
public class FileUtility implements Serializable{
	
	public static String readFromFile(String url) {
		System.out.println("Reading file from "+url+" ...............");
		File f = new File(url);
		BufferedReader br = null;
		FileInputStream fis = null;
		String outputfile = "";
		try {
			fis = new FileInputStream(f);
			br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
			while (br.ready()) {
				String s = br.readLine();
				outputfile += s + "\n";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			try {
				if (br != null)
					br.close();
				if (fis != null)
					fis.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return outputfile;
	}
	public static void processByFile(String url,FileProcess processer) {
		System.out.println("Reading file from "+url+" ...............");
		File f = new File(url);
		BufferedReader br = null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(f);
			br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
			while (br.ready()) {
				String s = br.readLine();
				processer.onEachFileLine(s);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
				if (fis != null)
					fis.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public static boolean writeToFile(String url, String xml) {
		try {
			File file = new File(url);
			if (file.exists())
				file.delete();
			File file2 = file.getParentFile();
			file2.mkdirs();
			FileOutputStream fos = new FileOutputStream(url);
			OutputStreamWriter out = new OutputStreamWriter(fos, "UTF-8");
			out.write(xml);
			out.close();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static void writeLineToEnd(String insertString, String url) {
		RandomAccessFile rf = null;
		try {
			rf = new RandomAccessFile(url, "rw");
			long len = rf.length();
			rf.seek(len);
			rf.writeBytes(insertString);
			rf.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rf != null)
					;
				rf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static ArrayList<String> detectFiles(String url, final String parten) {
		ArrayList<String> filelist = new ArrayList<String>();
		File file = new File(url);
		File[] files = file.listFiles();
		for (File tempfile : files) {
			if (tempfile.getName().endsWith("jpg")
					|| tempfile.getName().endsWith("bmp")
					|| tempfile.getName().endsWith("JPG"))
				filelist.add(tempfile.getName());
		}
		return filelist;
	}
	public static void debugMap(HashMap map){
		Set<Object> keyset=map.keySet();
		for(Object o:keyset){
			System.out.println(o+":"+map.get(o));
		}
	}

	public static void main(String[] args) {
	}

	public static void debugList(List enlist) {
		System.out.println("size:"+enlist.size());
		for(Object o:enlist)
			System.out.print(o+",");
		System.out.println();
	}
	public static HashMap<String,String> getConfigurations(String url){
		String context=readFromFile(url);
		HashMap<String,String> map=new HashMap<String,String>();
		String[] lines=context.split("\n");
		for(String line :lines){
			if(line.trim().length()==0)
				continue;
			if(line.trim().startsWith("#"))
				continue;
			System.out.println("config: "+line);
			int v=line.indexOf("=");
			map.put(line.substring(0,v), line.substring(v+1,line.length()).trim());
		}
		return map;
	}
	public static void makeEmptyFile(String url) throws IOException{
		File f=new File(url);
		if(!f.exists())
			f.createNewFile();
	}
	
}
