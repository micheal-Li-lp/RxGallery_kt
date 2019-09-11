package com.micheal.rxgallery.rxjob

interface Job {
    fun onRunJob():Result

    enum class Result{
        SUCCESS,FAILURE;
        var data :Any?=null
    }

    class Params(val tag :String?,val requestData : Any)

}