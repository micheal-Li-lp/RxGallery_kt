package com.micheal.rxgallery.entity

import android.os.Parcel
import android.os.Parcelable

open class MediaEntity() : Parcelable,BaseEntity{

    //图片ID
    var id: Long = 0
    var title: String? = null
    //图片、视频源地址
    var originalPath: String? = null
    //图片、视频创建时间
    var createDate: Long = 0
    //图片、视频最后修改时间
    var modifiedDate: Long = 0
    //媒体类型
    var mimeType: String? = null
    //宽
    var width: Int = 0
    //高
    var height: Int = 0
    //纬度
    var latitude: Double = 0.toDouble()
    //经度
    var longitude: Double = 0.toDouble()
    //图片方向
    var orientation: Int = 0
    //文件大小
    var length: Long = 0
    //文件夹相关
    var bucketId: String? = null
    var bucketDisplayName: String? = null
    //大缩略图
    var thumbnailBigPath: String? = null
    //小缩略图
    var thumbnailSmallPath: String? = null


    constructor(`in`: Parcel) : this() {
        id = `in`.readLong()
        title = `in`.readString()
        originalPath = `in`.readString()
        createDate = `in`.readLong()
        modifiedDate = `in`.readLong()
        mimeType = `in`.readString()
        bucketId = `in`.readString()
        bucketDisplayName = `in`.readString()
        thumbnailBigPath = `in`.readString()
        thumbnailSmallPath = `in`.readString()
        width = `in`.readInt()
        height = `in`.readInt()
        latitude = `in`.readDouble()
        longitude = `in`.readDouble()
        orientation = `in`.readInt()
        length = `in`.readLong()
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(id)
        dest.writeString(title)
        dest.writeString(originalPath)
        dest.writeLong(createDate)
        dest.writeLong(modifiedDate)
        dest.writeString(mimeType)
        dest.writeString(bucketId)
        dest.writeString(bucketDisplayName)
        dest.writeString(thumbnailBigPath)
        dest.writeString(thumbnailSmallPath)
        dest.writeInt(width)
        dest.writeInt(height)
        dest.writeDouble(latitude)
        dest.writeDouble(longitude)
        dest.writeInt(orientation)
        dest.writeLong(length)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MediaEntity> {
        override fun createFromParcel(parcel: Parcel): MediaEntity {
            return MediaEntity(parcel)
        }

        override fun newArray(size: Int): Array<MediaEntity?> {
            return arrayOfNulls(size)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is MediaEntity) {
            return false
        }

        val bean = other as MediaEntity?
        return bean!!.id == this.id

    }

    override fun hashCode(): Int {
        return this.id.toInt()
    }

    override fun toString(): String {
        return "MediaBean{" +
                "id=" + id +
                ", title='" + title + '\''.toString() +
                ", originalPath='" + originalPath + '\''.toString() +
                ", createDate=" + createDate +
                ", modifiedDate=" + modifiedDate +
                ", mimeType='" + mimeType + '\''.toString() +
                ", width=" + width +
                ", height=" + height +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", orientation=" + orientation +
                ", length=" + length +
                ", bucketId='" + bucketId + '\''.toString() +
                ", bucketDisplayName='" + bucketDisplayName + '\''.toString() +
                ", thumbnailBigPath='" + thumbnailBigPath + '\''.toString() +
                ", thumbnailSmallPath='" + thumbnailSmallPath + '\''.toString() +
                '}'.toString()
    }

}