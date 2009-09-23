#关于

def paint()
	int width=_zsgCanvasWidth()
	int height=_zsgCanvasHeight()
	_zsgFillRect(0xFFFFFF,0,0,width,height)
	int fontH=_zsgGetFontHeight()+2
	_zsgDrawGradient(0xDC2786,0xffffff,0,0,width,fontH,1)
	_zsgDrawGradient(0xDC2786,0xffffff,0,height-fontH,width,fontH,16)
	int strW=_zsgGetStringWidth("关于")
	int x=(width-strW)/2
	int y=1
	_zsgDrawString(0x000000,"关于",x,y,20)
	y=height-fontH+1
	x=2
	_zsgDrawString("确定",x,y,20)
	x=width-2
	_zsgDrawString("返回",x,y,24)
	int b=0
	y=fontH
	while b<maxLine
		_zsgDrawString(0x000000,info[offset+b],2,y,20)
		y=y+fontH
		b=b+1
	end
end

def keypress(int keycode)
	_zssprintln(keycode)
	if (keycode==-7)||(keycode==-6)
		load "/menu.zs"
	elsif keycode==-1
		offset=offset-1
		if offset<0
			offset=0
		end
	elsif keycode==-2
		offset=offset+1
		if offset+maxLine>totalLine
			offset=offset-1
		end
	end
end

def init()
	int width=_zsgCanvasWidth()
	int height=_zsgCanvasHeight()
	maxLine=height/(_zsgGetFontHeight()+2)-2
	string s="ZincScript是j2me平台上的脚本语言，语法涉及上参考了Ruby语言，采用了和Ruby相同的代码结构和关键字，例如if-else-end结构，def-end结构。熟悉Ruby语言和JAVA语言的开发者可以很快学会使用ZincScript进行脚本编程。ZincScript还在开发中，目前已经实现如下功能有1、整数的四则运算；2、整数与字符串的混合运算(比如连接)；3、完备的逻辑运算；4、完备的条件分支语句；5、while循环；6、函数定义；7、库函数和脚本函数调用（可以递归调用）；8、脚本动态装载    开发者：Jarod Yv   电子邮箱：JarodYv@gmail.com   网站：http://zincfish.heroku.com"
	int len=_zssStringLen(s)
	int line=_zsgGetFontWidth()
	line=width/line
	int start,stop
	while start<len
		stop=start+line
		if(stop>len)
			stop=len
		end
		info[totalLine]=_zssSubString(s,start,stop)
		start=stop
		totalLine=totalLine+1
	end
end

int maxLine
int offset
int totalLine
array info[]
_zssprintln("init1")
init()
_zssprintln("init2")