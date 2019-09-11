package com.micheal.rxgallery.rxjob

import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.LinkedBlockingDeque

class JobManager {
    private val jobQueue : Queue<Job> = LinkedBlockingDeque()
    private var queueFree = true

    fun addJob(job:Job){
        if (jobQueue.isEmpty()&&queueFree){
            jobQueue.offer(job)
            start()
        }else{
            jobQueue.offer(job)
        }
    }

    private fun start(){
        Observable.create(ObservableOnSubscribe<Job> {
            queueFree=false
            var job = jobQueue.poll()
            while (job != null){
                job.onRunJob()
                job = jobQueue.poll()
            }

            it.onComplete()
        }).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object :DisposableObserver<Job>(){
                override fun onComplete() {
                    queueFree = true
                }

                override fun onNext(t: Job) {
                }

                override fun onError(e: Throwable) {
                }

            })

    }

    fun clear(){
        jobQueue.clear()
    }

}