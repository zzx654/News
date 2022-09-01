package com.example.news.db

import androidx.room.TypeConverter
import com.example.news.models.Source

class Converters {//테이블 원소중에 일반적타입이 아닌 정의된 클래스타입이 있을경우 typeconverter사용

    @TypeConverter
    fun fromSource(source: Source):String{
        return source.name

    }

    @TypeConverter
    fun toSource(name:String): Source {
        return Source(name,name)
    }



}