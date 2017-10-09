package com.carto.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.Properties;

import org.apache.log4j.Logger;


import com.esri.arcgis.carto.IFeatureLayer;
import com.esri.arcgis.carto.ILayer;
import com.esri.arcgis.carto.IMap;
import com.esri.arcgis.carto.IMapDocument;
import com.esri.arcgis.carto.IMxdContents;
import com.esri.arcgis.datasourcesGDB.SdeWorkspaceFactory;
import com.esri.arcgis.datasourcesfile.ShapefileWorkspaceFactory;
import com.esri.arcgis.display.IColor;
import com.esri.arcgis.geodatabase.IFeatureClass;
import com.esri.arcgis.geodatabase.IFeatureWorkspace;
import com.esri.arcgis.geometry.esriGeometryType;
import com.esri.arcgis.interop.AutomationException;
import com.esri.arcgis.server.IServerContext;
import com.esri.arcgis.server.IServerObjectAdmin;
import com.esri.arcgis.server.IServerObjectConfiguration;
import com.esri.arcgis.server.IServerObjectConfiguration2;
import com.esri.arcgis.server.IServerObjectManager;
import com.esri.arcgis.server.ServerConnection;
import com.esri.arcgis.server.esriServerIsolationLevel;
import com.esri.arcgis.server.esriStartupType;
import com.esri.arcgis.system.IPropertySet;
import com.esri.arcgis.system.IPropertySetProxy;
import com.esri.arcgis.system.PropertySet;
import com.esri.arcgis.system.ServerInitializer;

/**
 * 根据用户配置信息及提交请求，动态创建地图服务
 * @author whu
 *
 */
@SuppressWarnings("deprecation")
public class CreateWebService {
	
	public Properties ps = new Properties();
	public String shpType = "";
	
	public CreateWebService()
	{
		ps = getProperties();
	}
	
	/**
	 * 根据ArcGIS Server服务链接及服务名，动态创建地图服务
	 * @param conn
	 * @param serviceName
	 * @return
	 */
	public  boolean createMapServices(ServerConnection conn,String serviceName)

    {

		try {
			
				//ServerConnection conn = getServerConnection();
					 
				IServerObjectAdmin pServerObjectAdmin = conn.getServerObjectAdmin();

		        IServerObjectConfiguration2 configuration = (IServerObjectConfiguration2)pServerObjectAdmin.createConfiguration();
		        

		        if (isExistService(pServerObjectAdmin,serviceName))
		        {
		            return true;
		        }

		        configuration.setName(serviceName);

		        configuration.setTypeName("MapServer");

		        IPropertySet props = configuration.getProperties();

		        @SuppressWarnings("unused")
				String temp = ps.getProperty("arcgisoutput")+"/"+serviceName+".mxd";
		        
		        props.setProperty("FilePath", ps.getProperty("arcgisoutput")+"/"+serviceName+".mxd");//设置MXD的路径

		        //一下的property并非必须，只要一个filepath就可以发布
		        
		       

		        props.setProperty("OutputDir", ps.getProperty("arcgisoutput"));//图片的输出目录

		        props.setProperty("VirtualOutPutDir", "http://"+ ps.getProperty("SERVER")+ps.getProperty("snapshotimgurl"));//图片输出的虚拟路径

		        props.setProperty("SupportedImageReturnTypes", "URL");//支持的图片类型

		        props.setProperty("MaxImageHeight", "2048");//图片的最大高度

		        props.setProperty("MaxRecordCount", "500");//返回记录的最大条数

		        props.setProperty("MaxBufferCount", "100");//缓冲区分析的最大数目

		        props.setProperty("MaxImageWidth", "2048");//图片的最大宽度

//		        props.setProperty("IsCached", "false");//是否切片
//
//		        props.setProperty("CacheOnDemand", "false");//是否主动切片
//
//		        props.setProperty("IgnoreCache", "false");//是否忽略切片
//
//		        props.setProperty("ClientCachingAllowed", "true");//是否允许客户端缓冲

		        @SuppressWarnings("unused")
				String tmp1 = ps.getProperty("snapshotimgcache")+"\\"+serviceName;
		        
		        props.setProperty("CacheDir", ps.getProperty("snapshotimgcache")+"\\"+serviceName);//切片的输出路径

		        props.setProperty("SOMCacheDir", ps.getProperty("snapshotimgcache"));//som的切片输出路径
		        
		        //
		        //props.setProperties("HasAttachments","true");

		        configuration.setDescription(serviceName);//Service的描述

		        configuration.setIsolationLevel(esriServerIsolationLevel.esriServerIsolationHigh);//或者esriServerIsolationLow,esriServerIsolationAny

		        configuration.setIsPooled(true);//是否池化

		        configuration.setMaxInstances(10);//最多的实例数

		        configuration.setMinInstances(1);//最少的实例数
		        //
		        configuration.setExtensionEnabled("FeatureServer", true);
		        
		        //设置附件
		        
		        IPropertySet extInfoProp = configuration.getExtensionInfo("FeatureServer");
		        extInfoProp.setProperty("WebEnabled", "true");
		        extInfoProp.setProperty("WebCapabilities", "Query,Editing");
		        //extInfoProp.setProperty("AttachmentsEnabled", "true");
		        
		        //IPropertySet extPro = configuration.getExtensionProperties("FeatureServer");
		        //extPro.setProperties("HasAttachments", "true");
		        //extInfoProp.setProperties("HasAttachments", "true");

		        //设置刷新

		        IPropertySet recycleProp = configuration.getRecycleProperties();

		        recycleProp.setProperty("StartTime", "00:00");//刷新开始时间

		        recycleProp.setProperty("Interval", "3600");//刷新间隔

		        //设置是否开启REST服务

		        IPropertySet infoProp = configuration.getInfo();

		        infoProp.setProperty("WebEnabled", "true");//是否提供REST服务

		        infoProp.setProperty("WebCapabilities", "Map,Query,Data");//提供何种服务
		        
		        //infoProp.setProperty("Capabilities", "Feature Access");//提供何种服务
		        
		        configuration.setStartupType(esriStartupType.esriSTAutomatic);//或者esriSTManual

		        configuration.setUsageTimeout(120);//客户端占用一个服务的最长时间

		        configuration.setWaitTimeout(120);//客户端申请一个服务的最长等待时间

		        //添加服务到Server

		        pServerObjectAdmin.addConfiguration(configuration);

		        //启动服务

		        pServerObjectAdmin.startConfiguration(serviceName, "MapServer");
		        System.out.println("finish");
		        
		        
		        return true;
		        
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			 Logger log=Logger.getLogger(CreateWebService.class.getName());
			 log.info("创建地图服务失败！错误原因："+e.getMessage());
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			 Logger log=Logger.getLogger(CreateWebService.class.getName());
			 log.info(e.getMessage());
			return false;
		}



    }
	
	public  boolean createMapServicesForFile(ServerConnection conn,String serviceName){
		try {
			IServerObjectAdmin pServerObjectAdmin = conn.getServerObjectAdmin();

	        IServerObjectConfiguration2 configuration = (IServerObjectConfiguration2)pServerObjectAdmin.createConfiguration();

	        if (isExistService(pServerObjectAdmin,serviceName))
	        {
	            return true;
	        }

	        configuration.setName(serviceName);

	        configuration.setTypeName("MapServer");

	        IPropertySet props = configuration.getProperties();

	        props.setProperty("FilePath", ps.getProperty("arcgisoutput")+"/"+serviceName+".mxd");//设置MXD的路径

	        //一下的property并非必须，只要一个filepath就可以发布
	        
	       

	        props.setProperty("OutputDir", ps.getProperty("arcgisoutput"));//图片的输出目录

	        props.setProperty("VirtualOutPutDir", "http://"+ ps.getProperty("SERVER")+ps.getProperty("snapshotimgurl"));//图片输出的虚拟路径

	        props.setProperty("SupportedImageReturnTypes", "URL");//支持的图片类型

	        props.setProperty("MaxImageHeight", "2048");//图片的最大高度

	        props.setProperty("MaxRecordCount", "500");//返回记录的最大条数

	        props.setProperty("MaxBufferCount", "100");//缓冲区分析的最大数目

	        props.setProperty("MaxImageWidth", "2048");//图片的最大宽度

	        props.setProperty("IsCached", "false");//是否切片

	        props.setProperty("CacheOnDemand", "false");//是否主动切片

	        props.setProperty("IgnoreCache", "false");//是否忽略切片

	        props.setProperty("ClientCachingAllowed", "true");//是否允许客户端缓冲

	        @SuppressWarnings("unused")
			String tmp1 = ps.getProperty("snapshotimgcache")+"\\"+serviceName;
	        
	        props.setProperty("CacheDir", ps.getProperty("snapshotimgcache")+"\\"+serviceName);//切片的输出路径

	        props.setProperty("SOMCacheDir", ps.getProperty("snapshotimgcache"));//som的切片输出路径
	        
	        //
	        //props.setProperties("HasAttachments","true");

	        configuration.setDescription(serviceName);//Service的描述

	        configuration.setIsolationLevel(esriServerIsolationLevel.esriServerIsolationHigh);//或者esriServerIsolationLow,esriServerIsolationAny

	        configuration.setIsPooled(true);//是否池化

	        configuration.setMaxInstances(10);//最多的实例数

	        configuration.setMinInstances(1);//最少的实例数
	        //
//	        configuration.setExtensionEnabled("FeatureServer", true);
	        configuration.setExtensionEnabled("KmlServer", true);
	        
	        configuration.setExtensionEnabled("WMSServer", true);
	        
	        //设置附件
	        
//	        IPropertySet extInfoProp = configuration.getExtensionInfo("FeatureServer");
	        IPropertySet extInfoProp = configuration.getExtensionInfo("KmlServer");
	        extInfoProp.setProperty("WebEnabled", "true");
	        extInfoProp.setProperty("WebCapabilities", "Query,Editing");

	        //设置刷新

	        IPropertySet recycleProp = configuration.getRecycleProperties();

	        recycleProp.setProperty("StartTime", "00:00");//刷新开始时间

	        recycleProp.setProperty("Interval", "3600");//刷新间隔

	        //设置是否开启REST服务
	        IPropertySet infoProp = configuration.getInfo();

	        infoProp.setProperty("WebEnabled", "true");//是否提供REST服务

	        infoProp.setProperty("WebCapabilities", "Map,Query,Data");//提供何种服务
	        
	        //infoProp.setProperty("Capabilities", "Feature Access");//提供何种服务
	        
	        configuration.setStartupType(esriStartupType.esriSTAutomatic);//或者esriSTManual

	        configuration.setUsageTimeout(120);//客户端占用一个服务的最长时间

	        configuration.setWaitTimeout(120);//客户端申请一个服务的最长等待时间

	        //添加服务到Server

	        pServerObjectAdmin.addConfiguration(configuration);

	        //启动服务

	        pServerObjectAdmin.startConfiguration(serviceName, "MapServer");
	        
	       // pServerObjectAdmin.updateConfiguration(configuration);
	        
	        
	        System.out.println("Service Start");       
	        
	        return true;
	        
	} catch (UnknownHostException e) {
		// TODO Auto-generated catch block
		//e.printStackTrace();
		 Logger log=Logger.getLogger(CreateWebService.class.getName());
		 log.info("创建地图服务失败！错误原因："+e.getMessage());
		return false;
	} catch (IOException e) {
		// TODO Auto-generated catch block
		//e.printStackTrace();
		 Logger log=Logger.getLogger(CreateWebService.class.getName());
		 log.info(e.getMessage());
		return false;
	}

	}
	
	/**
	 * 动态删除地图
	 * @param conn
	 * @param serviceName
	 * @return
	 */
	public boolean deleteMapService(ServerConnection conn,String serviceName)
	{
		boolean flag = true;
		try {
			IServerObjectAdmin pServerObjectAdmin = conn.getServerObjectAdmin();
			 if (isExistService(pServerObjectAdmin,serviceName))
		        {
				 	pServerObjectAdmin.deleteConfiguration(serviceName, "MapServer");
				 	return flag;
		        }
			return false;
			
		} catch (AutomationException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			 Logger log=Logger.getLogger(CreateWebService.class.getName());
			 log.info("删除地图服务失败！错误原因："+e.getMessage());
			return false;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			 Logger log=Logger.getLogger(CreateWebService.class.getName());
			 log.info("删除地图服务失败！错误原因："+e.getMessage());
			return false;			
		}
		
	}
	/**
	 * 获得ArcGIS Server服务连接
	 * @return
	 */
	public ServerConnection getServerConnection()
	{
		try {
			
			ServerInitializer initializer = new ServerInitializer();
				System.out.println("ServerInitializer isnot null ");  

				String sDomain=ps.getProperty("domain");
				String sSysname=ps.getProperty("sysname");
				String sSyspassword=ps.getProperty("syspassword");
				String sServer=ps.getProperty("SERVER");
				System.out.println(sDomain+" "+sSysname+" "+sSyspassword);
				initializer.initializeServer(sDomain, sSysname, sSyspassword); 
				ServerConnection conn = new ServerConnection(); 
				if(conn!=null)
				{
					System.out.println("conn isnot null "); 
				}
				//conn.connect("10.5.9.191"); 
				//conn.connect("whdx", "*", "ArcGISSOM", "zb19871314");
				conn.connect(sServer, "*", sSysname, sSyspassword);
				return conn;
		}catch(Exception e)
		{
			//e.getStackTrace();
			 Logger log=Logger.getLogger(CreateWebService.class.getName());
			 log.info("获得地图服务连接失败！错误原因："+e.getMessage());
			return null;
		}
	}
	
	/**
	 * 释放serverConnection连接
	 * @param conn
	 */
	public void releaseServerConnection(ServerConnection conn)
	{
		if(conn != null)
		{
			conn.release();
		}
	}
	
	/**
	 * 获得ArcGIS Server服务上下文环境
	 * @return
	 */
	public IServerContext getServerContext()
	{
		try {
			
			ServerInitializer initializer = new ServerInitializer();
				System.out.println("ServerInitializer isnot null ");  

				String sDomain=ps.getProperty("domain");
				String sSysname=ps.getProperty("sysname");
				String sSyspassword=ps.getProperty("syspassword");
				String sServer=ps.getProperty("SERVER");
				System.out.println(sDomain+" "+sSysname+" "+sSyspassword);
				initializer.initializeServer(sDomain, sSysname, sSyspassword); 
				ServerConnection conn = new ServerConnection(); 
				if(conn!=null)
				{
					System.out.println("conn isnot null "); 
				}
				//conn.connect("10.5.9.191"); 
				//conn.connect("whdx", "*", "ArcGISSOM", "zb19871314");
				conn.connect(sServer, "*", sSysname, sSyspassword);
				
				IServerObjectManager som = conn.getServerObjectManager();
				IServerContext sc = som.createServerContext("chinalp", "MapServer");
				
				return sc;
		}catch(Exception e)
		{
			//e.getStackTrace();
			 Logger log=Logger.getLogger(CreateWebService.class.getName());
			 log.info("获得地图服务连接失败！错误原因："+e.getMessage());
			return null;
		}
	}
	
	/**
	 * 根据服务Admin、服务名称，判断当前服务是否存在
	 * @param pServerObjectAdmin
	 * @param serviceName 服务名称
	 * @return
	 */
	private boolean isExistService(IServerObjectAdmin pServerObjectAdmin,String serviceName)
    {

        try
        {
            @SuppressWarnings("unused")
			IServerObjectConfiguration pConfig = pServerObjectAdmin.getConfiguration(serviceName, "MapServer");
            return true;
        }
        catch (Exception e)
        {
//        	hw-2013-3-30
//        	 Logger log=Logger.getLogger(CreateWebService.class.getName());
//			 log.info("判断地图服务失败！错误原因："+e.getMessage());
        	return false;
        }

    }

	/**
	 * 创建用于地图发布的MXD文件
	 * @param conn 地图连接
	 * @param MxdPath mxd文件路径
	 * @param MxdName mxd文件名称
	 * @return
	 */
	public boolean CreateOnlyMxd(ServerConnection conn,String MxdPath, String MxdName)
    {
        IMapDocument pMapDocument = (IMapDocument) CreateObject(conn,"esriCarto.MapDocument");
        if (MxdPath.substring(MxdPath.length() - 1) != "/")
            MxdPath += "/";
        try {
			pMapDocument.esri_new(MxdPath + MxdName + ".mxd");
		        if (pMapDocument.isReadOnly(MxdPath + MxdName + ".mxd") == true)
		        {
		            return false;
		        }
		        pMapDocument.save(true, false);
		        
		        return true;
		} catch (AutomationException e) {
			// TODO Auto-generated catch block
			 Logger log=Logger.getLogger(CreateWebService.class.getName());
			 log.info("创建mxd文件失败！错误原因："+e.getMessage());
			//e.printStackTrace();
			return false;
		} catch (IOException e) {
			
			 Logger log=Logger.getLogger(CreateWebService.class.getName());
			 log.info("创建mxd文件失败！错误原因："+e.getMessage());
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return false;
		}
       
       
    }

    /**
     * esri RGB颜色转换
     * @param conn
     * @param R
     * @param G
     * @param B
     * @return
     */
    public IColor GetRGBColor(ServerConnection conn,int R, int G, int B)
    {
        IColor pColor = (IColor) CreateObject(conn,"esriDisplay.RgbColor");
        try {
			pColor.setRGB(B * 65536 + G * 256 + R);
		} catch (AutomationException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			 Logger log=Logger.getLogger(CreateWebService.class.getName());
			 log.info(e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			 Logger log=Logger.getLogger(CreateWebService.class.getName());
			 log.info(e.getMessage());
			//e.printStackTrace();
		}
        return pColor;
    }

    
    
    /**
     * 根据服务连接创建esri object对象
     * @param conn 服务连接
     * @param ObjectCLSID 对象CLSID
     * @return
     */
    private Object CreateObject(ServerConnection conn,String ObjectCLSID)
    {
    	IServerObjectManager som;
    	IServerContext serverContext = null;
		try {
			som = conn.getServerObjectManager();
			
			
			serverContext=som.createServerContext("chinalp", "MapServer");
			Object reValue = new Object();
			reValue = serverContext.createObject(ObjectCLSID);
			serverContext.releaseContext();
			
			return reValue;
			
		} catch (AutomationException e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
			 Logger log=Logger.getLogger(CreateWebService.class.getName());
			 log.info("创建ArcGIS服务对象失败！错误原因："+e1.getMessage());
			return null;
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
			 Logger log=Logger.getLogger(CreateWebService.class.getName());
			 log.info("创建ArcGIS服务对象失败！错误原因："+e1.getMessage());
			return null;
		} 
		
		
		
    }
    
    /**
     * 根据服务上下文环境创建esri object对象
     * @param serverContext
     * @param ObjectCLSID
     * @return
     */
    private Object CreateObject(IServerContext serverContext,String ObjectCLSID)
    {
		try {
			Object reValue = new Object();
			reValue = serverContext.createObject(ObjectCLSID);
			serverContext.releaseContext();
			
			return reValue;
			
		} catch (AutomationException e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
			 Logger log=Logger.getLogger(CreateWebService.class.getName());
			 log.info("创建ArcGIS服务对象失败！错误原因："+e1.getMessage());
			return null;
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
			 Logger log=Logger.getLogger(CreateWebService.class.getName());
			 log.info("创建ArcGIS服务对象失败！错误原因："+e1.getMessage());
			return null;
		} 
		
		
		
    }
    
  /**
   * 根据服务上下文环境，构建新的MXD文档
   * @param serverContext 服务上下文环境
   * @param mapPath 地图路径
   * @param mapName 地图名
   * @param sdeLayer sde要素图层
   */
    protected void CreateMXD(IServerContext serverContext,String mapPath,String mapName,String sdeLayer)
    {
       
    	IPropertySet pPropertySet = getPropertySet(serverContext,ps);
    	IFeatureWorkspace Outworkspace = null;
    	 //创建地图对象
        IMap pMap = (IMap)(CreateObject(serverContext,"esriCarto.Map"));
        
			SdeWorkspaceFactory SDEworkspaceFactory;
			try {
				SDEworkspaceFactory = new SdeWorkspaceFactory(serverContext.createObject(SdeWorkspaceFactory.getClsid()));
				Outworkspace = (IFeatureWorkspace) SDEworkspaceFactory.open(pPropertySet,0);
				
				IFeatureLayer pFeatureLayer = (IFeatureLayer)CreateObject(serverContext,"esriCarto.FeatureLayer");
	            IFeatureClass pFeatureClass = Outworkspace.openFeatureClass(sdeLayer);
	            pFeatureLayer.setFeatureClassByRef(pFeatureClass);
	            ILayer  layer =  (ILayer)pFeatureLayer;
	            layer.setName(pFeatureClass.getAliasName());
	            pMap.addLayer(layer);
			} catch (AutomationException e1) {
				// TODO Auto-generated catch block
				//e1.printStackTrace();
				 Logger log=Logger.getLogger(CreateWebService.class.getName());
				 log.info("创建mxd文件失败！错误原因："+e1.getMessage());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				//e1.printStackTrace();
				 Logger log=Logger.getLogger(CreateWebService.class.getName());
				 log.info("创建mxd文件失败！错误原因："+e1.getMessage());
			}		
			

        //mxd文档的保存地址
        String sDocument = mapPath+mapName+".mxd";
       //创建地图文档对象
        IMapDocument pMapDocument = (IMapDocument)CreateObject(serverContext,"esriCarto.MapDocument");
        try {
			pMapDocument.esri_new(sDocument);
			 pMapDocument.replaceContents((IMxdContents)pMap);
		        pMapDocument.save(true, false);
		       //释放服务器上下文
		        serverContext.releaseContext();
		} catch (AutomationException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			 Logger log=Logger.getLogger(CreateWebService.class.getName());
			 log.info("创建mxd文件失败！错误原因："+e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			 Logger log=Logger.getLogger(CreateWebService.class.getName());
			 log.info("创建mxd文件失败！错误原因："+e.getMessage());
		}        
       
    }

    /**
     * 根据服务连接，构建新的MXD文档
     * @param conn 服务连接
     * @param mapPath 地图路径
     * @param mapName 地图名称
     * @param sdeLayer sde要素图层
     * @return
     */
    protected boolean CreateMXD(ServerConnection conn,String mapPath,String mapName,String sdeLayer)
    {
    	IServerObjectManager som;
    	IServerContext serverContext = null;
		try {
			som = conn.getServerObjectManager();
			//serverContext=som.createServerContext("chinalp", "MapServer");
			serverContext=som.createServerContext("chinaroad", "MapServer");
		}catch(Exception e)
		{
			//e.getStackTrace();
			 Logger log=Logger.getLogger(CreateWebService.class.getName());
			 log.info("创建mxd文件失败！错误原因："+e.getMessage());
		}
		
    	
    	IPropertySet pPropertySet = getPropertySet(serverContext,ps);
    	IFeatureWorkspace Outworkspace = null;
    	 //创建地图对象
        IMap pMap = (IMap)(CreateObject(serverContext,"esriCarto.Map"));
        
			SdeWorkspaceFactory SDEworkspaceFactory;
			try {
				SDEworkspaceFactory = new SdeWorkspaceFactory(serverContext.createObject(SdeWorkspaceFactory.getClsid()));
				Outworkspace = (IFeatureWorkspace) SDEworkspaceFactory.open(pPropertySet,0);
				
	            //添加空白层
//				IFeatureLayer pFeatureLayer1 = (IFeatureLayer)CreateObject(serverContext,"esriCarto.FeatureLayer");
//	            IFeatureClass pFeatureClass1 = Outworkspace.openFeatureClass(sdeLayer+"_BLANK");
//	            pFeatureLayer1.setFeatureClassByRef(pFeatureClass1);
//	            ILayer  layer1 =  (ILayer)pFeatureLayer1;
//	            layer1.setName(pFeatureClass1.getAliasName());
//	            pMap.addLayer(layer1);
				
				IFeatureLayer pFeatureLayer = (IFeatureLayer)CreateObject(serverContext,"esriCarto.FeatureLayer");
	            IFeatureClass pFeatureClass = Outworkspace.openFeatureClass(sdeLayer);
	            pFeatureLayer.setFeatureClassByRef(pFeatureClass);
	            ILayer  layer =  (ILayer)pFeatureLayer;
	            layer.setName(pFeatureClass.getAliasName());
	            pMap.addLayer(layer);
	            
	            
			} catch (AutomationException e1) {
				// TODO Auto-generated catch block
				//e1.printStackTrace();
				 Logger log=Logger.getLogger(CreateWebService.class.getName());
				 log.info("创建mxd文件失败！错误原因："+e1.getMessage());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				//e1.printStackTrace();
				 Logger log=Logger.getLogger(CreateWebService.class.getName());
				 log.info("创建mxd文件失败！错误原因："+e1.getMessage());
			}		
			

        //mxd文档的保存地址
        String sDocument = mapPath+mapName+".mxd";
       //创建地图文档对象
        IMapDocument pMapDocument = (IMapDocument)CreateObject(serverContext,"esriCarto.MapDocument");
        try {
        	pMapDocument.esri_new(sDocument);
        	pMapDocument.replaceContents((IMxdContents)pMap);
        	pMapDocument.save(true, false);
        	//释放服务器上下文
        	serverContext.releaseContext();

        	return true;
		} catch (AutomationException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			 Logger log=Logger.getLogger(CreateWebService.class.getName());
			 log.info("创建mxd文件失败！错误原因："+e.getMessage());
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			 Logger log=Logger.getLogger(CreateWebService.class.getName());
			 log.info("创建mxd文件失败！错误原因："+e.getMessage());
			return false;
		}        
       
    }
    
    protected boolean CreateMXDFromFile(ServerConnection conn, String mxdPath,String shpPath,String shpName,String serviceName){
    	IServerObjectManager som;
    	IServerContext serverContext = null;
    	try {
			som = conn.getServerObjectManager();
//			serverContext = som.createServerContext("chinaroad", "MapServer");
			serverContext = som.createServerContext(ps.getProperty("servicename"), "MapServer");
		} catch (AutomationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	IMap pMap = (IMap)(CreateObject(serverContext, "esriCarto.Map"));
    	IFeatureLayer pFeatureLayer = (IFeatureLayer)CreateObject(serverContext, "esriCarto.FeatureLayer");
    	ShapefileWorkspaceFactory SHPworkspaceFactory;
    	
    	//判断上传文件类型以进行处理
    	File tmpFile = new File(shpPath + "/" +shpName + ".shp");
    	
    	if(tmpFile.exists()){
    		if(!(new File(shpPath + "/" +shpName + ".prj")).exists()){
    			System.out.println("shp数据缺少prj文件，请提供数据prj文件");
    			return false;
    		}
    		else{
    			//判断shp数据的投影类型，决定是否进行投影变换。此处先统一进行变换，变换后的数据存入临时文件夹tmp
    			File tmpPath = new File(shpPath + "/tmp");
    			tmpPath.mkdir();
    			
//    			if(Upload.transformUploadFile(tmpFile.getAbsolutePath(), tmpPath.getAbsolutePath())==false){
//    				System.out.println("坐标系统转换失败");
//    				return false;
//    			}
    			
    			try {
        			SHPworkspaceFactory = new ShapefileWorkspaceFactory(serverContext.createObject(ShapefileWorkspaceFactory.getClsid()));
        	    	IFeatureWorkspace Inworkspace = (IFeatureWorkspace) SHPworkspaceFactory.openFromFile(tmpPath.getAbsolutePath(),0);
        	    	IFeatureClass pFeatureClass = Inworkspace.openFeatureClass(shpName);
        	    	
//        	    	FeatureClass myFeatureCls = (FeatureClass)pFeatureClass;
//        	    	int typeID = myFeatureCls.getFeature(0).getShapeCopy().getGeometryType();
//        	    	int geometryId = pFeatureClass.getFeatureType();
//        	    	int featureTypeID = pFeatureClass.getShapeType();
//        	    	int temp =  pFeatureClass.getFeature(0).getShapeCopy().getGeometryType();
        	    	
        	    	
        	    	if(esriGeometryType.esriGeometryPoint == pFeatureClass.getFeature(0).getShapeCopy().getGeometryType()){
        	    		shpType = "pointSymbolizer";
        	    	}
        	    	else if(esriGeometryType.esriGeometryPolyline == pFeatureClass.getFeature(0).getShapeCopy().getGeometryType()){
        	    		shpType = "lineSymbolizer";
        	    	}
        	    	else if(esriGeometryType.esriGeometryPolyline == pFeatureClass.getFeature(0).getShapeCopy().getGeometryType()){
        	    		shpType = "polygonSymbolizer";
        	    	}
        	    	
        	    	pFeatureLayer.setFeatureClassByRef(pFeatureClass);
        	    	
        	    	ILayer layer = (ILayer)pFeatureLayer;
        	    	layer.setName(pFeatureClass.getAliasName());        	    	
        	    	pMap.addLayer(layer);
        		} catch (AutomationException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		} catch (IOException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}	
    			
    		}
    	}
    	else{
    		File currentPath = new File(shpPath);
    		String [] temString = currentPath.list();
    		for(int i=0;i<temString.length; i++){
    			if(temString[i].split("\\.")[0].equals(shpName)){
//    				shpName = shpPath + "/" + shpName + "." + temString[i].split(".")[1];
    				
    				//文件格式转换后的文件名字发生了变化
//    				if(Upload.transformUploadFile(shpPath + "/" + shpName + "." + temString[i].split("\\.")[1], shpPath)==false){
//    					System.out.println("格式转换失败");
//    					return false;
//    				}
    				break;
    			}
    		}
    		
    		try {
    			SHPworkspaceFactory = new ShapefileWorkspaceFactory(serverContext.createObject(ShapefileWorkspaceFactory.getClsid()));
    	    	IFeatureWorkspace Inworkspace = (IFeatureWorkspace) SHPworkspaceFactory.openFromFile(shpPath,0);
    	    	IFeatureClass pFeatureClass = Inworkspace.openFeatureClass(shpName);
    	    	
    	    	pFeatureLayer.setFeatureClassByRef(pFeatureClass);
    	    	
    	    	ILayer layer = (ILayer)pFeatureLayer;
    	    	layer.setName(pFeatureClass.getAliasName());
    	    	
    	    	pMap.addLayer(layer);
    		} catch (AutomationException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}	
    	}
    	
    	//String mapName = "ADD_"+"SHP"+"_"+System.currentTimeMillis();
    	
    	String sDocument = mxdPath  + "/" + serviceName + ".mxd";
    	IMapDocument pMapDocument =  (IMapDocument)CreateObject(serverContext, "esriCarto.MapDocument");
    	try {
			pMapDocument.esri_new(sDocument);
	    	pMapDocument.replaceContents((IMxdContents)pMap);
	    	pMapDocument.save(true,false);

	    	serverContext.releaseContext();
		} catch (AutomationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	return true;
    }
    
    /**
     * 获得配置信息
     * @return
     */
    public Properties getProperties()
    {
		InputStream fis = getClass().getResourceAsStream("/property.properties");
		Properties ps = new Properties();
		try {
			ps.load(fis);
			fis.close();
			
			return ps;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			 Logger log=Logger.getLogger(CreateWebService.class.getName());
			 log.info("获得server配置信息失败！错误原因："+e.getMessage());
			return null;
		}
		
		
		
    }
    /**
     * 根据服务上下文获得配置信息
     * @param serverContext 服务上下文环境
     * @param ps 配置要素类
     * @return
     */
    public IPropertySet getPropertySet(IServerContext serverContext,Properties ps)
    {
			
			try {
				IPropertySet pPropertySet=new IPropertySetProxy(serverContext.createObject(PropertySet.getClsid()));
				
				String server=ps.getProperty("SERVER");
				String instance=ps.getProperty("INSTANCE");
				String user=ps.getProperty("user");
				String password=ps.getProperty("password");
				String version=ps.getProperty("VERSION");
				
				pPropertySet.setProperty("SERVER", server);
				pPropertySet.setProperty("INSTANCE", instance);
				pPropertySet.setProperty("DATABASE", "");
				pPropertySet.setProperty("USER", user);
				pPropertySet.setProperty("PASSWORD", password);
				pPropertySet.setProperty("VERSION", version);
				
				return pPropertySet;
			} catch (AutomationException e) {
				//e.printStackTrace();
				 Logger log=Logger.getLogger(CreateWebService.class.getName());
				 log.info(e.getMessage());
				return null;
			} catch (IOException e) {
				
				//e.printStackTrace();
				 Logger log=Logger.getLogger(CreateWebService.class.getName());
				 log.info("获得server配置信息失败！错误原因："+e.getMessage());
				return null;
			}
			
    }
    
    public static void main(String[] Args)
	{
		CreateWebService cws = new CreateWebService();
		//IServerContext serverContext = cws.getServerContext();
		ServerConnection conn = cws.getServerConnection();
		cws.CreateMXD(conn, "D:/OnlineEdit/","WSYL_140200_2009_PT","WSYL_140200_2009_PT");
		cws.createMapServices(conn,"WSYL_140200_2009_PT");
		conn.release();
	}
	
    public String publishWebService()
    {

		CreateWebService cws = new CreateWebService();
		//IServerContext serverContext = cws.getServerContext();
		ServerConnection conn = cws.getServerConnection();
		boolean isMxd = cws.CreateMXD(conn, "D:/OnlineEdit/","WSYL_140200_2009_PT","WSYL_140200_2009_PT");
		String reValue="failed";
		if(isMxd)
		{
			boolean isWS = cws.createMapServices(conn,"WSYL_140200_2009_PT");
			if(isWS)
			{
				reValue = "success";
			}
		}
		
		conn.release();
		return reValue;
    }
   
//	public boolean transformUploadFile(String inputFile, String outputFile){
//		ITransformation myTransfor = new CoordinateTransformation();
//		myTransfor.connect();
//		myTransfor.excute(inputFile, outputFile, "LL84", true);
//		myTransfor.disconnect();
//		return true;
//	} 
    
}
