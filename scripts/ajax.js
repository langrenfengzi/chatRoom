/**
 * 同时太多的对同一个URL的请求会使得浏览器把结果给缓存起来，使得得不得及时的更新，
 * 在URL后缀加上&timeStamp=new Date().getTime() 即可解决。
 * 另JS不停的查询服务器照成浏览器CPU负担和内存占用增多??? 
 */

	var saycache="";//缓存保存当前所说的内容
	var nickname=window.name;//缓存当前用户匿称
	var msgcache="";//缓存保存当前获得的信息
	var timeStamp="";//从服务器获得时间戳
	var tooltip;//页面右下角提示框
	var xmlHttpObj;//XMLHTTP Object
		function getxmlHttp(){
			if(xmlHttpObj){
				return xmlHttpObj;
			}
			if(window.XMLHttpRequest){// code for IE7+, Firefox, Chrome, Opera, Safari
				xmlHttpObj = new XMLHttpRequest();
			}else{// code for IE6, IE5
				xmlHttpObj = new ActiveXObject("Microsoft.XMLHTTP");
			}

			return xmlHttpObj;
		}
		

		//刷新页面window的name属性值仍存在
		window.onload=function(){
			if(nickname){
				afterLogin();	
			}else{
				document.getElementById("nickname").focus();
			}
		}
		
		//登录
		function login(){
			if(!document.getElementById("nickname")) return;
			if(document.getElementById("nickname").value==""){
				alert('Need a name.');
				return;
			}
			nickname=document.getElementById("nickname").value.replace(/>/,'').replace(/</,'').replace(/:/,'');
			var xmlHttp = getxmlHttp();
			xmlHttp.open("GET","login?nickname="+encodeURI(nickname),true);
			xmlHttp.onreadystatechange = function(){
				if(xmlHttp.readyState==4 ){
					if(xmlHttp.status==200){
						window.name=nickname;
						afterLogin();
					}else{
						alert("Login failed! Server no-reponse:"+xmlHttp.status);	
					}
				}
			};
			
			xmlHttp.send(null);
		}
		
		//登陆之后页面处理
		function afterLogin(){
				document.getElementById("login").className ="hidden";
				document.getElementById("all").style.display="block";
				document.getElementById("nickname2").innerText=nickname;
				document.getElementById("say").focus();
	
				refresh();
		}
		
		//发送信息到服务器
		function say(){
			var say = document.getElementById("say");
			if(!say) return;
			if(say.value=="" ||say.value.replace(/^\s+/,'').replace(/\s+$/,'')==""){
				say.innerText = "Say sth please...";	
				return;
			}
			saycache = say.value;
//			var queryString=encodeURIComponent("nickname="+nickname+"&say="+say.value);
//			var queryString="nickname="+nickname+"&say="+say.value;
			var queryString="nickname="+encodeURIComponent(nickname)+"&say="+encodeURIComponent(say.value);
			say.value="";
			var xmlHttp = getxmlHttp();
			
			xmlHttp.open("POST","login",true);
			xmlHttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
			xmlHttp.onreadystatechange = function(){
				if(xmlHttp.readyState==4){
				   if(xmlHttp.status==200){
					   if(xmlHttp.responseText=="ok"){
						   sayTip("Say succeed!");
					   }else{
						   alert("Say failed")
					   }
					}else{
						say.value=saycache;
						alert("Send failed:"+xmlHttp.status);	
					}
				}
			};
			
			xmlHttp.send(queryString);
		}
		
		
		/**
		 * 
		 * @description 解析从服务端获得的内容，并动态更新页面
		 * 注意：从服务端返回的是总会多个换行，"\r\n"，2个长度。
		 * 
		 */
		function analyse(text){
			text.replace(/^\s+/,'').replace(/\s+$/,'');//trim()方法
			if(!text || text=="\r\n" ||  text==msgcache) {
				return;//不用更新
			}
			var msg = text;
			if(msgcache!=""){
				msg = msg.substring(msgcache.length-2);//减2，因为每次多算个回车换行的长度
			}
			var contents = document.getElementById("contents");
			var items = msg.split(">><<");
			for(var i=0,len=items.length;i<len;i++){
				//items[i].replace(/^\s+/,'').replace(/\s+$/,'');
				if(items[i]=="" || items[i]=="\r\n") continue;
				var p = document.createElement("p");
				
				var speak = items[i].substring(0,items[i].length-20);
				var time = items[i].substring(items[i].length-19);
				var label = document.createElement("label");
				label.className="speak";
				label.appendChild(document.createTextNode(speak));
				p.appendChild(label);
				label = document.createElement("label");
				label.className="time2";
				label.appendChild(document.createTextNode(time));
				p.appendChild(label);
				contents.appendChild(p);
			}
			
			msgcache = text;//最后缓存当前发送来的信息
			if(contents.scrollHeight>contents.offsetHeight){
		                contents.scrollTop=contents.scrollHeight-contents.offsetHeight;
             		}
		}

		
		//定时访问服务器，检查是否有内容更新
		function refresh(){
			tooltips(new Date().toLocaleString()+"\nneed refresh?");
			var xmlHttp = getxmlHttp();
			xmlHttp.open("GET","login?refresh=false&timeStamp="+new Date().getTime(),true);
			xmlHttp.onreadystatechange = function(){
				if(xmlHttp.readyState==4 ){
					if(xmlHttp.status==200){//实际上服务器返回信息过来没这么快，要等一会
						needRefresh(xmlHttp.responseText);
						//tooltips(new Date().toLocaleString()+"\ntimeStamp:"+xmlHttp.responseText);
					}
				}
			};
			xmlHttp.send(null);
			setTimeout(refresh,1100);
		}
		
		function needRefresh(text){
			if(text && (text=text.substring(0,text.length-2))== timeStamp){//时间戳一致，无需更新			
				return;
			}else{
				timeStamp =text;
				requestData();
			}
		}
		
		//向服务端请求所有发言信息
		function requestData(){
			var xmlHttp = getxmlHttp();
			xmlHttp.open("GET","login?refresh=true&timeStamp"+new Date().getTime(),true);
			xmlHttp.onreadystatechange = function(){
				if(xmlHttp.readyState==4 ){
					if(xmlHttp.status==200){
						analyse(xmlHttp.responseText);
					}
				}
			};
			xmlHttp.send(null);
		}
		
		function sayTip(msg){
			var say = document.getElementById("say");
			say.innerText=msg;
			setTimeout(function(){document.getElementById("say").innerText="";},1000);
		}
		
	
		
		
		//按键绑定，回车登录
		function autoLogin(event){
			if(event.keyCode==13){
				login()
			}
		}
		//Ctrl+Enter发送说话内容
		function autoSay(event){
			if(!event) return;
			if(event.ctrlKey && event.keyCode==13){
				say();	
			}
		}
		function tooltips(tip){
			if(!tooltip){
				tooltip = document.createElement("div");
				tooltip.style.cssText="background-clor:#666;border:2px solid black;width:200px;position:fixed;right:0;bottom:0;"
				document.body.appendChild(tooltip);
			}
			tooltip.innerText=tip;
			setTimeout(function(){tooltip.style.display='none';},5000);
		}
		function logout(){
			window.name="";
			document.location.reload();
		}