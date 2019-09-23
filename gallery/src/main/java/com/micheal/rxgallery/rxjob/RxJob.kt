package com.micheal.rxgallery.rxjob

object RxJob {
    private var jobManager: JobManager = JobManager()

    @JvmStatic
    fun addJob(job: Job) {
        jobManager.addJob(job)
    }

    @JvmStatic
    fun clearJob() {
        jobManager.clear()
    }

}