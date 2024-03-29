package com.micheal.rxgallery.entity

class BucketEntity :BaseEntity{
    var bucketId: String? = null
    var bucketName: String? = null
    var imageCount: Int = 0
    var cover: String? = null
    //图片方向
    var orientation: Int = 0

    override fun equals(other : Any?): Boolean {
        if (other==null || other !is BucketEntity){
            return false
        }

        return this.bucketId.equals(other.bucketId)

    }

}