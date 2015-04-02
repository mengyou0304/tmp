package com.robin.source;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class DataSource {
	MongoClient mongo;
	DB mongodb;
	DBCollection bfenleiTable;

	public void getConnection() throws UnknownHostException {
		mongo = new MongoClient("111.204.165.5", 27017);
		mongodb = mongo.getDB("my_dict");
		String pass="my_dict.Pwd2";
		mongodb.authenticate("my_dict", pass.toCharArray());
		bfenleiTable = mongodb.getCollection("bfenlei");
	}

	public void closeConnection() {
		mongodb.cleanCursors(false);
		mongo.close();
	}

	public void updateInfo(DBCollection table, String name,
			HashMap<String, String> map) {
		if (table == null)
			table = bfenleiTable;
		// 更新一条记录
		System.out.println("Calling update Message......"+map);
		BasicDBObject query = new BasicDBObject();
		query.put("name", name);

		BasicDBObject updatedinfo = new BasicDBObject();
		for (String key : map.keySet()) {
			if(key.equals("_id"))
				continue;
			String value = map.get(key);
			updatedinfo.put(key, value);
		}
		BasicDBObject updateObj = new BasicDBObject();
		updateObj.put("$set", updatedinfo);
		table.update(query, updateObj);
	}

	public List<Map> queryInfo(DBCollection table, String key, String value) {
		if (table == null)
			table = bfenleiTable;
		ArrayList<Map> reslist = new ArrayList<Map>();
		BasicDBObject query = new BasicDBObject();
		query.put(key, value);
		DBCursor cursor = null;
		if (key == null || key.length() == 0)
			cursor = table.find();
		else
			cursor = table.find(query);
		while (cursor.hasNext()) {
			DBObject o = cursor.next();
			reslist.add(o.toMap());
		}
		return reslist;
	}

	public static void main(String[] args) {
		DataSource ds = new DataSource();
		try {
			ds.getConnection();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("haha", "hehe");
		// ds.updateInfo(null, "乌龙绝配", map);
		// ds.queryInfo(null, "name", "乌龙绝配");
		ArrayList<String> typs=new ArrayList<String>();
		typs.add("yanyuan");
		typs.add("geshou");
		typs.add("mingxing");
		typs.add("daoyan");
		typs.add("zhuchiren");
		typs.add("mote");
		typs.add("dianying");
		typs.add("yule");
//		for(String key:typs){
//			List<Map> infos = ds.queryInfo(null, "category", key);
//			System.out.println(key+" :"+infos.size());
//		}
		List<Map> infos = ds.queryInfo(null, "", "");
		System.out.println(infos.size());

		ds.closeConnection();
	}

}
