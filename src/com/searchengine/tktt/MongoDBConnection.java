package com.searchengine.tktt;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoDBConnection {
	// Cau hinh MongoDB
	MongoClient mongoClient;
	// Lay co so du lieu
	MongoDatabase mdb;
	// Lay bang trong co so du lieu
	MongoCollection<Document> collectionInvertedIndex;
	MongoCollection<Document> collectionLength;
	
	public MongoDBConnection(){
		mongoClient = new MongoClient("localhost", 27017);
		mdb = mongoClient.getDatabase("test");
		collectionInvertedIndex = mdb.getCollection("invertedIndex");
		collectionLength = mdb.getCollection("LengthDoc");
	}
	
	public MongoCollection<Document> getcollectionInvertedIndex() {
		return collectionInvertedIndex;
	}

	public MongoCollection<Document> getCollectionLength() {
		return collectionLength;
	}

	
}
