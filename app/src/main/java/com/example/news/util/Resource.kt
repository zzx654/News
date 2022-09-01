package com.example.news.util

data class Resource<out T>(val data:T?,val message:String?=null,val status:Status) {

    companion object{

        fun<T> Success(data:T?)=Resource(data,null,Status.SUCCESS)
        fun<T> Error(message:String)=Resource(null,message,Status.ERROR)
        fun<T> Loading()=Resource(null,null,Status.LOADING)
    }



}
enum class Status{
    SUCCESS,
    ERROR,
    LOADING,


}