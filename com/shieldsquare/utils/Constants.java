package com.shieldsquare.utils;

import com.shieldsquare.utils.ReadPropertiesFile;


public class Constants 
{
	public static String SOURCE_SERVER=ReadPropertiesFile.getProperty("source.server.name");
	public static int SOURCE_PORT=Integer.parseInt(ReadPropertiesFile.getProperty("source.server.port"));
	public static String SOURCE_PASSWD=ReadPropertiesFile.getProperty("source.server.passwd");
	public static String SOURCE_API=ReadPropertiesFile.getProperty("source.api.list");
	public static String SOURCE_JS=ReadPropertiesFile.getProperty("source.js.list");
	
	public static String DESTINATION_SERVER=ReadPropertiesFile.getProperty("destination.server.name");
	public static int DESTINATION_PORT=Integer.parseInt(ReadPropertiesFile.getProperty("destination.server.port"));
	public static String DESTINATION_PASSWD=ReadPropertiesFile.getProperty("destination.server.passwd");
	public static String DESTINATION_API=ReadPropertiesFile.getProperty("destination.api.list");
	public static String DESTINATION_JS=ReadPropertiesFile.getProperty("destination.js.list");
	
	public static String CONFIG_SERVER=ReadPropertiesFile.getProperty("config.server.name");
	public static int CONFIG_PORT=Integer.parseInt(ReadPropertiesFile.getProperty("config.server.port"));
	public static String CONFIG_PASSWD=ReadPropertiesFile.getProperty("config.server.passwd");
	
	public static int TIME_OUT=Integer.parseInt(ReadPropertiesFile.getProperty("timeout"));
	
	public static String GET_TRAFFIC=ReadPropertiesFile.getProperty("get.traffic");
	public static String CHECK_SIDMAP=ReadPropertiesFile.getProperty("check.sidmap");
	public static String CHECK_SIDMINMAP=ReadPropertiesFile.getProperty("check.sidminmap");

	public static String TEST_SID=ReadPropertiesFile.getProperty("testsid");
	public static String DEFAULT_SID=ReadPropertiesFile.getProperty("defaultsid");
	public static String MAPPED_SID=ReadPropertiesFile.getProperty("mappedsid");
	
	
	public static boolean ENABLE_SIDWISETRAFFIC=Boolean.parseBoolean(ReadPropertiesFile.getProperty("SIDwisetrafficEnable"));
	public static int SIZE=Integer.parseInt(ReadPropertiesFile.getProperty("ReplicaSize"));
	
	public static int API_THREAD_COUNT = Integer.parseInt(ReadPropertiesFile.getProperty("api.thread.count"));
	public static int JS_THREAD_COUNT = Integer.parseInt(ReadPropertiesFile.getProperty("js.thread.count"));
}
