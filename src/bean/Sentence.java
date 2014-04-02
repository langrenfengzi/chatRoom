package bean;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Sentence {

	private String nickname;
	private String contents;
	private String time;
	
	public Sentence(String nickname,String contents){
		this.nickname = nickname;
		this.contents = contents;
		this.time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}
	
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getContents() {
		return contents;
	}
	public void setContents(String contents) {
		this.contents = contents;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
	public String toString(){
		return nickname+":"+contents+":"+time;
	}
	
}
