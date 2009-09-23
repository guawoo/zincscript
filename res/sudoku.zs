#数独游戏

def paint()	
	if warning!=""
		int sw = _zsgGetStringWidth(warning)
		int wx=(width-sw)/2
		int wy=(height-fontH*3)/2
		_zsgFillRect(0xFFC5A5,wx-2,wy-2,sw+4,fontH*3+4)
		_zsgDrawRect(0x000000,wx-1,wy-1,sw+1,fontH*3+1)
		_zsgDrawString(0xC33702,warning,wx,wy+fontH,20)
		return 0
	end

	_zsgFillRect(0xFFFFFF,0,fontH,width,height-fontH*2)
	
	boardW = gridW*3
	int i=0
	while i<9
		if i%2==0
			_zsgSetColor(0xFF99FF)
		else
			_zsgSetColor(0xFFCCFF)
		end
		_zsgFillRect(x+(i%3)*boardW,y+(i/3)*boardW,boardW,boardW)
		i=i+1
	end
	
	boardW = gridW*9
	i=0
	_zsgSetColor(0x000000)
	while i<10
		_zsgDrawLine(x+gridW*i,y,x+gridW*i,y+boardW)
		_zsgDrawLine(x,y+gridW*i,x+boardW,y+gridW*i)
		i=i+1
	end
	
	_zsgFillRect(0xDC2786,x+curX*gridW,y+curY*gridW,gridW,gridW)
	_zsgDrawRect(0xFFFFFF,x+curX*gridW,y+curY*gridW,gridW,gridW)
	_zsgSetColor(0x0099ff)
	i=0
	int j
	while i<9
		j=0
		while j<9
			if sudoku[i][j]>0
				_zsgDrawString(0x0099ff,sudoku[i][j],fontX+gridW*j,fontY+gridW*i,20)
			elsif sudoku[i][j]<0
				_zsgDrawString(0x5050ff,-sudoku[i][j],fontX+gridW*j,fontY+gridW*i,20)
			end
			j=j+1
		end
		i=i+1
	end
end

def keypress(int keycode)
	warning=""
	if keycode==-7
		load "/menu.zs"
	elsif keycode==-6
		clear()
		puzzle()
	elsif keycode==-1
		curY=curY-1
		if curY<0
			curY=8
		end
	elsif keycode==-2
		curY=curY+1
		if curY>8
			curY=0
		end
	elsif keycode==-3
		curX=curX-1
		if curX<0
			curX=8
		end
	elsif keycode==-4
		curX=curX+1
		if curX>8
			curX=0
		end
	elsif keycode>=49&&keycode<=57
		if sudoku[curY][curX]<0
			warning="这个格子是固定值"
		else
			int c=keycode-48
			if check(curY,curX,c)
				if sudoku[curY][curX]==0
					fillNum=fillNum+1
				end
				sudoku[curY][curX]=c	
				if fillNum==81
					warning="你真聪明,这么难都做出来啦"
				end
			else
				warning="您填入的数字不对哦"
			end
		end
	end
end

def depuzzle()
	int i,j
	int rad,tmp
	while i<9
		j=0
		while j<9
			rad=_zssRandom(1,9,rad)
			_zssprintln("rad = "+rad)
			tmp=check(i,j,rad)
			_zssprintln("i = "+i+"tmp = "+tmp+" j = "+j)
			if tmp>0
				sudoku[i][j]=rad
				testNum=0
				j=j+1
			elsif testNum>10
				j=j-1
				if j<0
					j=8
					i=i-1
				end	
				sudoku[i][j]=0
				testNum=0
			else
				testNum=testNum+1
			end
		end
		
		_zssprintln("i = "+i+" j = "+j)
		i=i+1
	end
end

def puzzle()
	sudoku[0][1]=-7
	sudoku[0][3]=-3
	sudoku[0][5]=-5
	sudoku[0][8]=-1
	sudoku[1][0]=-2
	sudoku[1][3]=-1
	sudoku[1][4]=-6
	sudoku[1][6]=-4
	sudoku[1][8]=-9
	sudoku[2][2]=-6
	sudoku[2][4]=-9
	sudoku[2][5]=-7
	sudoku[2][6]=-8
	sudoku[2][8]=-5
	sudoku[3][2]=-8
	sudoku[3][3]=-9
	sudoku[3][7]=-4
	sudoku[3][8]=-7
	sudoku[4][1]=-9
	sudoku[4][4]=-7
	sudoku[4][5]=-6
	sudoku[4][8]=-2
	sudoku[5][0]=-1
	sudoku[5][4]=-2
	sudoku[5][6]=-5
	sudoku[6][0]=-6
	sudoku[6][1]=-3
	sudoku[6][5]=-9
	sudoku[6][8]=-4
	sudoku[7][2]=-5
	sudoku[7][3]=-6
	sudoku[7][6]=-7
	sudoku[8][0]=-7
	sudoku[8][2]=-1
	sudoku[8][3]=-5
	sudoku[8][5]=-4
	sudoku[8][7]=-2
	sudoku[8][8]=-6
	fillNum=38
end

def check(int x,int y,int num)
	int s
	int zoneX=x/3*3
	int zoneY=y/3*3
	while s<9
		if _zssABS(sudoku[x][s])==num
			return 0
		end
		if _zssABS(sudoku[s][y])==num
			return 0
		end
		if _zssABS(sudoku[zoneX+s/3][zoneY+s%3])==num
			return 0
		end
		s=s+1
	end
	return num
end

def clear()
	int i,j
	while i<9
		j=0
		while j<9
			sudoku[i][j]=0
			j=j+1
		end
		i=i+1
	end
end

def init()
	width=_zsgCanvasWidth()
	height=_zsgCanvasHeight()
	fontH=_zsgGetFontHeight()+2
	_zsgDrawGradient(0xDC2786,0xffffff,0,0,width,fontH,1)
	_zsgDrawGradient(0xDC2786,0xffffff,0,height-fontH,width,fontH,16)
	x=_zsgGetStringWidth("数独")
	x=(width-x)/2
	y=1
	_zsgDrawString(0x000000,"数独",x,y,20)
	y=height-fontH+1
	x=2
	_zsgDrawString("重新开始",x,y,20)
	x=width-2
	_zsgDrawString("返回",x,y,24)
	boardW=height-fontH*2
	if boardW>width
		boardW=width
	end
	boardW=boardW-10
	gridW=boardW/9
	boardW = gridW*9
	x=(width-boardW)/2
	y=(height-boardW)/2
	fontX=_zsgGetStringWidth("9")
	fontX=(gridW-fontX)/2+x
	fontY=(gridW-fontH)/2+y+1
	clear()
	puzzle()
end

int curX,curY
int width,height
int gridW,boardW
int fontX,fontY
int x,y
int fontH
int testNum
int fillNum
string warning
array sudoku[][]
init()