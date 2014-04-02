package servlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sevice.Recorder;
import bean.Sentence;

/**
 * 
 * @author lang
 * @格式	昵称:内容:时间>><<昵称:内容:时间>><<...
 * @description 以">><<"来分隔每个人说话的内容，注意在提交的内容中替换">"、"<"相应的其他内容
 */
/**
 * 还没决定使用ArrayList保存所有的Sentence实例，还是继续用StringBuffer保存连接起来的字符串
 */
public class ChatServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private StringBuffer sb = new StringBuffer();
	private ArrayList<String> users = new ArrayList<String>();
	private long counts=0;//发言数目
	private String timeStamp="0";//时间戳，由上一次提交生成
	private Recorder recorder = new Recorder();//记录日志和说话内容
	private static boolean DEBUG = true;
	
	
	//负责登录和定时更新
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setCharacterEncoding("UTF-8");
		PrintWriter out = resp.getWriter();
		//定时轮询更新模块，做好标志位——当前更新到哪里了？
		String refresh = req.getParameter("refresh");
		refresh= (refresh!=null)?refresh.trim():"";		
		
		if("false".equalsIgnoreCase(refresh)){
			out.println(this.timeStamp);
			out.flush();
			out.close();
			//print("返回时间戳——"+this.timeStamp);//太频繁了，就不打印了
			return;
		}else if("true".equalsIgnoreCase(refresh)){
			print("返回数据——"+sb.toString());
			out.println(sb.toString());
			out.flush();
			out.close();
			return;
		}
		
		
		
		//登录模块
//		String nickname = URLDecoder.decode(req.getParameter("nickname"),"UTF-8");
		String nickname = new String(req.getParameter("nickname").getBytes("ISO-8859-1"), "UTF-8").trim();
		nickname.replaceAll(">","").replaceAll("<","").replace(":","");//去掉已使用的特殊字符
		print("登录用户名："+nickname);
		users.add(nickname);
		
		out.print(sb.toString());
		out.flush();
		out.close();
		
	}

	//负责处理提交内容
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		print("capture request of POST from "+req.getRemoteAddr());
		resp.setCharacterEncoding("UTF-8");
		PrintWriter out = resp.getWriter();
		System.out.println(req.getParameter("nickname")+"\n"+req.getParameter("say"));
		//String nickname = URLDecoder.decode(req.getParameter("nickname"), "UTF-8"); ;
		//String contents = URLDecoder.decode(req.getParameter("say"),"UTF-8");
		String nickname = new String(req.getParameter("nickname").getBytes("ISO-8859-1"), "UTF-8");
		String contents = new String(req.getParameter("say").getBytes("ISO-8859-1"), "UTF-8");
		nickname=(nickname!=null)?nickname.trim():"";
		contents=(contents!=null)?contents.trim():"";
		nickname.replaceAll(">","").replaceAll("<","").replace(":","");
		contents.replaceAll("<", "&lt;").replaceAll(">", "&gt");
		Sentence sentence = new Sentence(nickname,contents);
		print(sentence.toString());
		sb.append(sentence.toString()).append(">><<");
		recorder.printItem(sentence);
		
		counts++;//留言+1
		this.timeStamp = Long.toString(System.currentTimeMillis());//更新盖上时间戳
		print("时间戳："+this.timeStamp);
		
		//提交成功，返回ok
		out.print("ok");
		out.flush();
		out.close();
	}

	//处理字符串编码问题
	public String toUTF8(String msg){
		try {
			return new String(msg.getBytes("ISO-8859-1"),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public  void print(String msg){
		
		String result = new Date() + "----------" + msg;
		if(DEBUG){
			System.out.println(result);
		}
		recorder.printLog(msg);
		recorder.flushLog();
	}

	@Override
	public void destroy() {
		recorder.close();
		super.destroy();
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		System.out.println(new Date() + "----------" +"Server started~~~~~~");
		super.init(config);
	}

}
