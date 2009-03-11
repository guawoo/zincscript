def newDiary()
	_zssSwitch("/widgets/diary/newdiary.xml");
end

def draft()

end

def edit()

end 

def delete()

end

def refresh()

end 

def getDiaryList()
    _zsnSend("/widgets/diary/diarydata.xml")
end

def showcontent(string id)
	_zsnSend(id)
end