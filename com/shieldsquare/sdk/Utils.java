package com.shieldsquare.sdk;

import org.json.JSONObject;
import com.shieldsquare.sdk.Main;
import com.shieldsquare.utils.Constants;
import redis.clients.jedis.Jedis;

public class Utils 
{
	synchronized private static String getSID(Jedis configJedis, String sidInternal)
	{
		String testSID = "";
		String ismapped=configJedis.hget(Constants.MAPPED_SID, sidInternal);
		if(ismapped==null)
		{
			testSID=configJedis.srandmember(Constants.TEST_SID);
			configJedis.hset(Constants.MAPPED_SID, sidInternal, testSID);
		}
		else
		{
			testSID=configJedis.hget(Constants.MAPPED_SID, sidInternal);
		}
		return testSID;
	}

	//TODO change modifyAndPushAPi---Done
	synchronized public static void modifyAndPushApi(Jedis destinationJedis,Jedis configJedis,String sidInternal, JSONObject jsonObj)
	{
		String testSID = "";
		if(Constants.ENABLE_SIDWISETRAFFIC) // TODO give descriptive name--Done
		{
			// TODO if meant for only, one don't use sets for default_sid and get_traffic 
			String defautlsid=configJedis.srandmember(Constants.DEFAULT_SID); // TODO variable names in small letter--Done
			String getTraffic=configJedis.srandmember(Constants.GET_TRAFFIC);
			if(!jsonObj.getString("_zpsbd1").isEmpty())
			{											
				if( defautlsid!=null && getTraffic!=null)
				{
					boolean check = configJedis.sismember(Constants.GET_TRAFFIC, sidInternal);
					if(check)
					{
						testSID=configJedis.srandmember(Constants.DEFAULT_SID);
						changeSignatureAndPushApi(destinationJedis,testSID,Constants.SIZE,jsonObj);
					}
				}
				else
				{
					Main.logger.error("Default SID | GET TRAFFIC ID not found");
				}
			}
			else
			{
				Main.logger.error("SID not found");
			}	
		}
		else
		{
			if(!jsonObj.getString("_zpsbd1").isEmpty())
			{
				testSID=getSID(configJedis, sidInternal);
				changeSignatureAndPushApi(destinationJedis, testSID, Constants.SIZE, jsonObj);
			}
			else
			{
				Main.logger.error("SID not found");
			}
		}

	}

	//TODO changeSignatureAndPushApi no underscore in method names. no synchronized--Done
	private static void changeSignatureAndPushApi(Jedis destinationJedis,String testSID,int size,JSONObject jsonObj)
	{
		JSONObject cjsonObj=null;
		for(int i=1;i<=size;i++)
		{
			cjsonObj=jsonObj;
			try
			{
				String sidpart[]=testSID.split("-");					
				String oldpid=cjsonObj.getString("_zpsbd2");
				String old2part[]=oldpid.split("-");				
				String newpid=oldpid.replace(old2part[1], sidpart[3]);    
				String cdata = cjsonObj.put("_zpsbd1",testSID).put("_zpsbd2",newpid).toString();
				destinationJedis.lpush(Constants.DESTINATION_API,cdata);
			}
			catch(Exception E)
			{
				Main.logger.error("Exception is:", E);
			}
		}
	}	

	synchronized public static void modifyAndPushJs(Jedis destinationJedis,Jedis configJedis, String sidInternal, JSONObject jsonObj){
		String testSID = "";
		if(Constants.ENABLE_SIDWISETRAFFIC)
		{
			String Defautlsid=configJedis.srandmember(Constants.DEFAULT_SID);
			String GetTraffic=configJedis.srandmember(Constants.GET_TRAFFIC);
			if(!jsonObj.getString("pid").isEmpty())
			{
				if( Defautlsid!=null && GetTraffic!=null)
				{
					boolean check = configJedis.sismember(Constants.GET_TRAFFIC, sidInternal);
					if(check) 
					{
						testSID=configJedis.srandmember(Constants.DEFAULT_SID);
						changeSignatureAndPushJs(destinationJedis,testSID,Constants.SIZE,jsonObj);
					}
				}
				else
				{
					System.out.println("Default SID | GET TRAFFIC ID not found");
					Main.logger.error("Default SID | GET TRAFFIC ID not found");
					System.exit(0);
				}
			}
			else
			{
				Main.logger.error("PID not found");
			}	
		}
		else
		{
			if(!jsonObj.getString("pid").isEmpty())
			{
				testSID = getSID(configJedis, sidInternal);
				changeSignatureAndPushJs(destinationJedis, testSID, Constants.SIZE, jsonObj);
			}
			else
			{
				Main.logger.error("PID not found");
			}
		}
	}

	private static void changeSignatureAndPushJs(Jedis destinationJedis,String testSID,int size,JSONObject jsonObj)
	{
		JSONObject cjsonObj=null;
		String cdata;
		try
		{
			for(int i=1;i<=size;i++)
			{
				cjsonObj=jsonObj;
				String sidpart[]=testSID.split("-");
				String oldpid=cjsonObj.getString("pid");
				String old2part[]=oldpid.split("-");				
				String newpid=oldpid.replace(old2part[1], sidpart[3]);
				if(cjsonObj.has("sid"))
				{
					cdata = cjsonObj.put("pid",newpid).put("sid", testSID).toString();
				}
				else
				{
					cdata = cjsonObj.put("pid",newpid).toString();
				}
				destinationJedis.lpush(Constants.DESTINATION_JS,cdata);
			}
		}
		catch(Exception E)
		{
			Main.logger.error("Exception is:", E);
		}
	}
}
