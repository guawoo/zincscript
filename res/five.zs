# 五子棋

def paint()
	int width=_zsgCanvasWidth()
	int height=_zsgCanvasHeight()
	if warning!=""
		int sw = _zsgGetStringWidth(warning)
		int sh = _zsgGetFontHeight()
		int x=(width-sw)/2
		int y=(height-sh*3)/2
		_zsgFillRect(0xFFC5A5,x-2,y-2,sw+4,sh*3+4)
		_zsgDrawRect(0x000000,x-1,y-1,sw+1,sh*3+1)
		_zsgDrawString(0xC33702,warning,x,y+sh,20)
		return 0
	end
	
	_zsgFillRect(0xFFFFFF,0,0,width,height)
	int fontH=_zsgGetFontHeight()+2
	_zsgDrawGradient(0xDC2786,0xffffff,0,0,width,fontH,1)
	_zsgDrawGradient(0xDC2786,0xffffff,0,height-fontH,width,fontH,16)
	int strW=_zsgGetStringWidth("五子棋")
	int x=(width-strW)/2
	int y=1
	_zsgDrawString(0x000000,"五子棋",x,y,20)
	y=height-fontH+1
	x=2
	_zsgDrawString("重新开始",x,y,20)
	x=width-2
	_zsgDrawString("返回",x,y,24)
	int boardW
	if width<height
		boardW=width-10
	else
		boardW=height-10
	end
	
	int n=14
	int gridW=boardW/n	
	boardW=gridW*n
	int boardX=(width-boardW)/2
	int boardY=(height-boardW)/2
	_zsgFillRect(0xff5050,boardX,boardY,boardW,boardW)
	_zsgSetColor(0x000000)
	
	while n>=0
		_zsgDrawLine(boardX+gridW*n,boardY,boardX+gridW*n,boardY+boardW);
		_zsgDrawLine(boardX,boardY+gridW*n,boardX+boardW,boardY+gridW*n);
		n=n-1
	end
	
	if round%2==0
		_zsgSetColor(0xffffff)
	else
		_zsgSetColor(0x000000)
	end
	
	int i=0
	while i<round
		x=black[i][0]
		y=black[i][1]
		_zsgFillRect(0x000000,boardX+gridW*x-3,boardY+gridW*y-3,6,6)
		x=white[i][0]
		y=white[i][1]
		_zsgFillRect(0xffffff,boardX+gridW*x-3,boardY+gridW*y-3,6,6)
		i=i+1
	end
	
	_zsgDrawRect(boardX+gridW*curX-3,boardY+gridW*curY-3,6,6)
end

# 移动落子位置
def keypress(int keycode)
	warning=""
	if keycode==-7
		load "/menu.zs"
	elsif keycode==-6
		round=0
		curX=7
		curY=7
	elsif keycode==-1
		curY=curY-1
		if curY<0
			curY=14
		end
	elsif keycode==-2
		curY=curY+1
		if curY>14
			curY=0
		end
	elsif keycode==-3
		curX=curX-1
		if curX<0
			curX=14
		end
	elsif keycode==-4
		curX=curX+1
		if curX>14
			curX=0
		end
	elsif keycode==-5
		int chess=0
		chess=hasChess(curX,curY)
		if chess==0
			black[round][0]=curX
			black[round][1]=curY
			cpu()
			round=round+1;
		else
			warning="该位置已有棋子"					
		end
	end
end

def cpu()
	int i,c
	int h=1,s=1
	while i<14
		c = hasChess(curX,i)
		if c==1
			h=h+1
		elsif c==2
			h=0
		else
			h=1
		end
		if h>=3
			c=searchRight()
			if c!=100
				curY=c
				white[round][0]=curX
				white[round][1]=curY
				return 0
			end
	
			c=searchLeft()
			if c!=100
				curY=c
				white[round][0]=curX
				white[round][1]=curY
				return 0
			end
		end
		
		c = hasChess(i,curY)
		if c==1
			s=s+1
		elsif c==2
			s=0
		else
			s=1
		end
		if s>=3
			c=searchDown()
			if c!=100
				curX=c
				white[round][0]=curX
				white[round][1]=curY
				return 0
			end
	
			c=searchUp()
			if c!=100
				curX=c
				white[round][0]=curX
				white[round][1]=curY
				return 0
			end
		end
		i=i+1
	end
	int x=100
	x=searchRight()
	if x!=100
		curY=x
		white[round][0]=curX
		white[round][1]=curY
		return 0
	end
	
	x=searchLeft()
	if x!=100
		curY=x
		white[round][0]=curX
		white[round][1]=curY
		return 0
	end
	
	int y=100
	y=searchDown()
	if y!=100
		curX=y
		white[round][0]=curX
		white[round][1]=curY
		return 0
	end
	
	y=searchUp()
	if y!=100
		curX=y
		white[round][0]=curX
		white[round][1]=curY
		return 0
	end
end

def searchDown()
	int x=curX+1
	int c=hasChess(x,curY)
	while c!=0
		if c==1
			x=x+1
			if x>14
				return 100
			end
			c=hasChess(x,curY)
		else
			return 100
		end
	end
	return x
end


def searchUp()
	int x=curX-1
	int c=hasChess(x,curY)
	while c!=0
		if c==1
			x=x-1
			if x<0
				return 100
			end
			c=hasChess(x,curY)
		else
			return 100
		end
	end
	return x
end

def searchRight()
	int y=curY+1
	int c=hasChess(curX,y)
	while c!=0
		if c==1
			y=y+1
			if y>14
				return 100
			end
			c=hasChess(curX,y)
		else
			return 100
		end
	end
	return y
end

def searchLeft()
	int y=curY-1
	int c=hasChess(curX,y)
	while c!=0
		if c==1
			y=y-1
			if y<0
				return 100
			end
			c=hasChess(curX,y)
		else
			return 100
		end
	end
	return y
end

def hasChess(int x,int y)
	int num = 0
	
	while num<round
		if x==black[num][0]&&y==black[num][1]
			return 1
		end
		
		if x==white[num][0]&&y==white[num][1]
			return 2
		end
		num=num+1
	end
	return 0
end

# 黑子落子记录
array black[][]
# 白字落子记录
array white[][]
# 光标位置的横坐标
int curX=7
# 光标位置的纵坐标
int curY=7

int round=0

string warning
