package sevice;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Recorder {

	//日志和订单记录保存在Tomcat/木录下chatRoom文件夹下以8位数字命名的文件夹下，其实想定义在当前项目目录下
	private static String today =  new SimpleDateFormat("yyyyMMdd").format(new Date());//当前日期
	private static String DIR = "chatRoom" + "/" + today;//存放的目录
	private static String log =  DIR + "/" + today +"_log.txt";
	private static String item = DIR + "/" + today +"_chatContents.txt";
	private static final String ENCODING="UTF-8";
	
	private PrintWriter outLog = null;//日志打印流
	private PrintWriter outItem = null;//订单打印流
	
	public Recorder(){
		//准备写入日志文件
		File dir = new File(DIR);
		if(!dir.exists()){//目录若不存在则创建
			dir.mkdirs();
		}
		File f1 = new File(log);
		File f2 = new File(item);
		try {
			if(!f1.exists()){
				f1.createNewFile();//创建日志文件
			}
			if(!f2.exists()){
				f2.createNewFile();//创建订单文件
			}
			
//			outLog = new PrintWriter(new FileWriter(f1,true));//追加模式写入日志
//			outOrder = new PrintWriter(new FileWriter(f2,true));//追加模式写入日志
			//追加，并以utf-8编码写入
			outLog = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f1, true), ENCODING)));
			outItem = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f2, true), ENCODING)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		printLog("Server started~~~~~~");
		flushLog();
	}

	
	
	
	public void printLog(String msg){
		
		String result = new Date() + "----------" + msg;
		if(outLog!=null){
			outLog.println(result);
		}
	}
	public void flushLog(){
		outLog.flush();
	}
	
	
	public void printItem(Object i){
		if(outItem!=null){
			outItem.println(i.toString());
			flushItem();
		}
	}
	public void flushItem(){
		if(outItem!=null){
			outItem.flush();
		}
	}
	
	
	//关闭文件输出流
	public void close(){
		
		printLog("服务器要关闭了~~~");
		
		if(outLog!=null){
			outLog.flush();
			outLog.close();
		}
		if(outItem!=null){
			outItem.flush();
			outItem.close();
		}
	}
}


