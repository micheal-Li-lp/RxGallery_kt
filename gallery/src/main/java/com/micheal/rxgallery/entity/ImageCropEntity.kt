package com.micheal.rxgallery.entity

import android.os.Parcel
import android.os.Parcelable

class ImageCropEntity : MediaEntity, Parcelable,BaseEntity{
    var cropPath: String? = null
    var aspectRatio = 0F

    constructor()

    constructor(parcel: Parcel): super(parcel){
        cropPath = parcel.readString()
        aspectRatio = parcel.readFloat()
    }

    fun copyMediaEntity(entity: MediaEntity) {
        id = entity.id
        title = entity.title
        originalPath = entity.originalPath
        createDate = entity.createDate
        modifiedDate = entity.modifiedDate
        mimeType=entity.mimeType
        bucketId = entity.bucketId
        bucketDisplayName = entity.bucketDisplayName
        thumbnailSmallPath = entity.thumbnailSmallPath
        thumbnailBigPath = entity.thumbnailBigPath
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        super.writeToParcel(dest, flags)
        dest.writeString(cropPath)
        dest.writeFloat(aspectRatio)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<ImageCropEntity> {
        override fun createFromParcel(parcel: Parcel) = ImageCropEntity(parcel)

        override fun newArray(size: Int) = arrayOfNulls<ImageCropEntity>(size)
    }
}