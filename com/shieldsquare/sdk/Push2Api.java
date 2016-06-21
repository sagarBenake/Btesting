package com.shieldsquare.sdk;

import java.util.List;

import org.json.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisException;
import com.shieldsquare.utils.Constants;
import com.shieldsquare.sdk.Utils;

public class Push2Api implements Runnable
{
	private Jedis sourceJedis;
	private Jedis destinationJedis;
	private Jedis configJedis;

	private JedisPool destinationJedisPool;
	private JedisPool sourceJedisPool;
	private JedisPool configJedisPool;

	
	public Push2Api(JedisPool sourceJedisPool,JedisPool destinationJedisPool,JedisPool configJedisPool)
	{
		this.sourceJedisPool=sourceJedisPool;
		this.destinationJedisPool = destinationJedisPool;	
		this.configJedisPool=configJedisPool;
	}
	@Override
	public void run() 
	{	
		while (true) 
		{
			List<String> data = null;
			try 
			{
				
				sourceJedis=sourceJedisPool.getResource();
				destinationJedis = destinationJedisPool.getResource();
				configJedis=configJedisPool.getResource();
				String chkpresent=configJedis.srandmember(Constants.TEST_SID);
				if(chkpresent!=null)
				{
					data = sourceJedis.brpop(0, Constants.SOURCE_API);
					
					if (data != null && data.size() == 2)
					{
						JSONObject jsonObj = new JSONObject(data.get(1));
						if(!jsonObj.getString("_zpsbd2").isEmpty())
						{
							String spid[] = jsonObj.getString("_zpsbd2").split("-");
							String sidInternal = destinationJedis.hget(Constants.CHECK_SIDMINMAP, spid[1]);									
							Utils.modifyAndPushApi(destinationJedis,configJedis,sidInternal, jsonObj);
						}
						else
						{						
							Push2ML.logger.error("PID not found");
						}
					}
					else 
					{
						Push2ML.logger.error("Wrong Data is:" + data);
					}
				}
				else
				{
					Push2ML.logger.error("Test SIDs not found");
					System.out.println("Please ADD test SIDs in 'S:testsid'");
					System.exit(0);
				}
			}
			catch(JedisConnectionException E)
			{
				Push2ML.logger.error("Jedis Broken Pipe Exception is:", E);
			}
			catch (JedisException E)
			{
				Push2ML.logger.error("Jedis RunTime Exception is:", E);
			}
			catch (Exception E)
			{
				Push2ML.logger.error("Exception is:", E);
				Push2ML.logger.error("Data is"+data.get(1));
				sourceJedis.rpush(Constants.SOURCE_API, data.get(1));
			}
			finally 
			{
				jedisCloseQuietly(sourceJedis);
				jedisCloseQuietly(destinationJedis);
				jedisCloseQuietly(configJedis);
			}
		}
	}
	
	private void jedisCloseQuietly(Jedis jedis){
		try{
			jedis.close();
			Push2ML.logger.info("Return resource to MLM Pool!");
		}catch(JedisException e){
			Push2ML.logger.error("Exception in finally: " + e);
		}
	}
}