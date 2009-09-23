#绘制开始菜单

def paint()
	int width=_zsgCanvasWidth()
	int height=_zsgCanvasHeight()
	
	_zsgDrawGradient(0xDC2786,0xffffff,0,0,width,height,1)
	int x=width/2
	int y=20
	int ba=(height-130)/4
	if ba<5
		y=0
		ba=(height-50)/4
	end
	_zsgDrawImage("/img/zincscript.png",x,y,17)
	_zsgFillRect(0,height-25,width,25)
	_zsgDrawImage("/img/jarodyv.png",x,height,33)
	int iy=y+30 
	_zsgDrawImage("/img/sudoku.png",x,iy,17)
	y=iy+ba
	_zsgDrawImage("/img/5chess.png",x,y,17)
	y=y+ba
	_zsgDrawImage("/img/about.png",x,y,17)
	y=y+ba
	_zsgDrawImage("/img/exit.png",x,y,17)
	x=(width-80)/2
	y=iy+ba*curY
	_zsgDrawRect(0xc0e6fe,x-1,y-1,81,16)
	_zsgDrawRect(0x0D4880,x-2,y-2,83,18)
end

def keypress(int keycode)
	if keycode==-1
		curY=curY-1
		if curY<0
			curY=3
		end
	elsif keycode==-2
		curY=curY+1
		if curY>3
			curY=0
		end
	elsif keycode==-5
		switch(curY)
	elsif keycode>=49&&keycode<=57
		switch(keycode-49)
	end
end

def switch(int code)
	if code==0
		load "/sudoku.zs"
	elsif code==1
		load "/five.zs"
	elsif code==2
		load "/about.zs"
	else
		_zssExit()
	end
end

def test(string str1,string str2)
	_zssprintln("str1 = "+str1)
	_zssprintln("str2 = "+str2)
	if str1 == str2
		_zssprintln(str1+" equals "+str2)
		_zssprintln(str2+" equals "+str1)
	else	
		_zssprintln(str1+" does NOT equals "+str2)
		_zssprintln(str2+" does NOT equals "+str1)
	end
end

def init()
	_zsgLoadImage("/img/zincscript.png","/img/sudoku.png","/img/5chess.png","/img/about.png","/img/exit.png","/img/jarodyv.png")
	test("abc","edf")
end

int curY

init()