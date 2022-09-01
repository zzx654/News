package com.example.news.db

import android.content.Context
import androidx.room.*
import com.example.news.models.Article

@Database(
    entities=[Article::class],
    version=1
)
@TypeConverters(Converters::class)
abstract class ArticleDatabase:RoomDatabase() {

    abstract fun getArticleDao():ArticleDao

    companion object{
        @Volatile
        private var instance:ArticleDatabase?=null//스레드가 이 인스턴스를 변화시킬때 즉시 관찰할수 있다(volatile)?
        private val LOCK=Any()

        operator fun invoke(context: Context)=instance?: synchronized(LOCK){//이름없이 호출되는 연산자함수
            //synchronized 블록안에는 동시에 다른스레드 접근 불가,instance가 null일때 실행
                instance?:createDatabase(context).also{ instance=it}
        }

        private fun createDatabase(context: Context)=
            Room.databaseBuilder(
                context.applicationContext,
                ArticleDatabase::class.java,
                "article_db.db"
            ).build()

    }
}