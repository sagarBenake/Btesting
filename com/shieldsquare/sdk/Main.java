package com.shieldsquare.sdk;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import org.apache.log4j.Logger;

import com.shieldsquare.sdk.Push2Api;
import com.shieldsquare.utils.Constants;


public class Main 
{
	public static Logger logger = Logger.getLogger(Main.class);
	
	public static void main(String[] args)
	{		
		Push2Api[] apiThreads = new Push2Api[Constants.API_THREAD_COUNT];
		Push2Js[] jsThreads = new Push2Js[Constants.JS_THREAD_COUNT];
	
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxTotal(Constants.API_THREAD_COUNT + Constants.JS_THREAD_COUNT);
		
		JedisPool sourceJedisPool=new JedisPool(poolConfig,Constants.SOURCE_SERVER,Constants.SOURCE_PORT,Constants.TIME_OUT,Constants.SOURCE_PASSWD);
		JedisPool destinationJedisPool = new JedisPool(poolConfig, Constants.DESTINATION_SERVER, Constants.DESTINATION_PORT, Constants.TIME_OUT, Constants.DESTINATION_PASSWD);
		JedisPool configJedisPool=new JedisPool(poolConfig,Constants.CONFIG_SERVER,Constants.CONFIG_PORT,Constants.TIME_OUT,Constants.CONFIG_PASSWD);
		
		for (Push2Api apiThread : apiThreads) 
		{
			apiThread = new Push2Api(sourceJedisPool,destinationJedisPool,configJedisPool);
			Thread thread = new Thread(apiThread);
			thread.start();
		}
		for (Push2Js jsThread : jsThreads)
		{
			jsThread = new Push2Js(sourceJedisPool,destinationJedisPool,configJedisPool);
			Thread thread = new Thread(jsThread);
			thread.start();
		}
	}	
}