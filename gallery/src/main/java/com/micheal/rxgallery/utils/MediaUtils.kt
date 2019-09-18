package com.micheal.rxgallery.utils

import android.content.Context
import android.database.Cursor
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.TextUtils
import com.micheal.rxgallery.R
import com.micheal.rxgallery.entity.BucketEntity
import com.micheal.rxgallery.entity.MediaEntity
import com.nostra13.universalimageloader.utils.StorageUtils
import java.io.File
import java.io.IOException
import java.util.ArrayList

object MediaUtils {
    @JvmStatic
    fun getMediaWithImageList(context: Context,page : Int,limit : Int):List<MediaEntity>{
        return getMediaWithImageList(context, Integer.MIN_VALUE.toString(), page, limit)
    }

    @JvmStatic
    fun getMediaWithImageList(context: Context,bucketId:String,page: Int,limit: Int):List<MediaEntity>{
        val offset = (page - 1) * limit
        arrayListOf<MediaEntity>().apply {
            val contentResolver = context.contentResolver
            arrayListOf<String>().apply {
                add(MediaStore.Images.Media._ID)
                add(MediaStore.Images.Media.TITLE)
                add(MediaStore.Images.Media.DATA)
                add(MediaStore.Images.Media.BUCKET_ID)
                add(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
                add(MediaStore.Images.Media.MIME_TYPE)
                add(MediaStore.Images.Media.DATE_ADDED)
                add(MediaStore.Images.Media.DATE_MODIFIED)
                add(MediaStore.Images.Media.LATITUDE)
                add(MediaStore.Images.Media.LONGITUDE)
                add(MediaStore.Images.Media.ORIENTATION)
                add(MediaStore.Images.Media.SIZE)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    add(MediaStore.Images.Media.WIDTH)
                    add(MediaStore.Images.Media.HEIGHT)
                }
            }.run {
                var selection: String? = null
                var selectionArgs: Array<String>? = null

                if (!TextUtils.equals(bucketId, Integer.MIN_VALUE.toString())) {
                    selection = MediaStore.Images.Media.BUCKET_ID + "=?"
                    selectionArgs = arrayOf(bucketId)
                }
                val cursor = contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    this.toTypedArray(),
                    selection,
                    selectionArgs,
                    MediaStore.Images.Media.DATE_ADDED + " DESC LIMIT " + limit + " OFFSET " + offset
                )

                if (cursor != null) {
                    val count = cursor.count
                    if (count > 0) {
                        cursor.moveToFirst()
                        do {
                            parseImageCursorAndCreateThumImage(context, cursor)?.run {
                                add(this)
                            }

                        } while (cursor.moveToNext())
                    }
                }

                if (cursor != null && !cursor.isClosed) {
                    cursor.close()
                }
            }
            return this
        }
    }


    @JvmStatic
    private fun parseImageCursorAndCreateThumImage(context: Context,cursor:Cursor):MediaEntity?{
        val size = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.SIZE))
        val originalPath =cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
        if (originalPath.isNullOrEmpty()||size<=0||!File(originalPath).exists()){
            return null
        }

        val id =cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID))

        val title = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.TITLE))
        val bucketId = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID))
        val bucketDisplayName =
            cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
        val mimeType = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE))
        val createDate = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED))
        val modifiedDate =
            cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED))

        return MediaEntity().apply {
            this.id = id
            this.title = title
            this.originalPath = originalPath
            this.bucketId = bucketId
            this.bucketDisplayName = bucketDisplayName
            this.mimeType = mimeType
            this.createDate = createDate
            this.modifiedDate = modifiedDate
            this.thumbnailBigPath = createThumbnailBigFileName(
                context,
                originalPath
            ).absolutePath

            this.thumbnailSmallPath = createThumbnailSmallFileName(
                context,
                originalPath
            ).absolutePath

        }

    }

    @JvmStatic
    fun createThumbnailBigFileName(context: Context,originalPath :String?):File{
        val storeFile =StorageUtils.getCacheDirectory(context)
        return File(storeFile,"big_${FileNameUtils.getName(originalPath)}")
    }

    @JvmStatic
    fun createThumbnailSmallFileName(context: Context,originalPath: String?) :File{

        val storeFile =StorageUtils.getCacheDirectory(context)
        return File(storeFile,"small_${FileNameUtils.getName(originalPath)}")
    }


    @JvmStatic
    fun getMediaWithVideoList(context: Context,page: Int,limit: Int) : List<MediaEntity>{
        return  getMediaWithVideoList(context, Int.MIN_VALUE.toString(),page,limit)
    }

    /**
     * 从数据库中读取视频
     */
    @JvmStatic
    fun getMediaWithVideoList(context: Context,bucketId: String,page: Int,limit: Int) : List<MediaEntity>{
        val offset = (page - 1) * limit
        val mediaBeanList = ArrayList<MediaEntity>()
        val contentResolver = context.contentResolver
        val projection = ArrayList<String>()
        projection.add(MediaStore.Video.Media._ID)
        projection.add(MediaStore.Video.Media.TITLE)
        projection.add(MediaStore.Video.Media.DATA)
        projection.add(MediaStore.Video.Media.BUCKET_ID)
        projection.add(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)
        projection.add(MediaStore.Video.Media.MIME_TYPE)
        projection.add(MediaStore.Video.Media.DATE_ADDED)
        projection.add(MediaStore.Video.Media.DATE_MODIFIED)
        projection.add(MediaStore.Video.Media.LATITUDE)
        projection.add(MediaStore.Video.Media.LONGITUDE)
        projection.add(MediaStore.Video.Media.SIZE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            projection.add(MediaStore.Video.Media.WIDTH)
            projection.add(MediaStore.Video.Media.HEIGHT)
        }
        var selection: String? = null
        var selectionArgs: Array<String>? = null
        if (!TextUtils.equals(bucketId, Integer.MIN_VALUE.toString())) {
            selection = MediaStore.Video.Media.BUCKET_ID + "=?"
            selectionArgs = arrayOf(bucketId)
        }

        val cursor = contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection.toTypedArray(),
            selection,
            selectionArgs,
            MediaStore.Video.Media.DATE_ADDED + " DESC LIMIT " + limit + " OFFSET " + offset
        )
        if (cursor != null) {
            val count = cursor.count
            if (count > 0) {
                cursor.moveToFirst()
                do {
                    parseVideoCursorAndCreateThumImage(context, cursor)?.run {
                          mediaBeanList.add(this)
                    }
                } while (cursor.moveToNext())
            }
        }

        if (cursor != null && !cursor.isClosed) {
            cursor.close()
        }
        return mediaBeanList
    }

    @JvmStatic
    private fun parseVideoCursorAndCreateThumImage(context: Context,cursor: Cursor) :MediaEntity?{
        val MediaEntity = MediaEntity()
        MediaEntity.id = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media._ID))
        MediaEntity.title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE))
        val originalPath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA))
        MediaEntity.originalPath = originalPath
        MediaEntity.bucketId = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_ID))
        val bucketDisplayName =
            cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME))
        MediaEntity.bucketDisplayName = bucketDisplayName
        val mimeType = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.MIME_TYPE))
        MediaEntity.mimeType = mimeType
        val createDate = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED))
        MediaEntity.createDate = createDate
        val modifiedDate =
            cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED))
        MediaEntity.modifiedDate = modifiedDate
        val length = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE))
        MediaEntity.length = length

        //创建缩略图文件
        MediaEntity.thumbnailBigPath = createThumbnailBigFileName(
            context,
            originalPath
        ).absolutePath

        MediaEntity.thumbnailSmallPath = createThumbnailSmallFileName(
            context,
            originalPath
        ).absolutePath


        var width = 0
        var height = 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            width = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.WIDTH))
            height = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.HEIGHT))
        } else {
            try {
                val exifInterface = ExifInterface(originalPath)
                width = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0)
                height = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0)
            } catch (e: IOException) {
                Logger.e(e)
            }

        }
        MediaEntity.width=width
        MediaEntity.height=height

        val latitude = cursor.getDouble(cursor.getColumnIndex(MediaStore.Video.Media.LATITUDE))
        MediaEntity.latitude = latitude
        val longitude = cursor.getDouble(cursor.getColumnIndex(MediaStore.Video.Media.LONGITUDE))
        MediaEntity.longitude = longitude
        return MediaEntity
    }


    /**
     * 获取所有的图片文件夹
     */
    @JvmStatic
    fun getAllBucketByImage(context: Context): List<BucketEntity> {
        return getAllBucket(context, true)
    }


    /**
     * 获取所以视频文件夹
     */
    @JvmStatic
    fun getAllBucketByVideo(context: Context): List<BucketEntity> {
        return getAllBucket(context, false)
    }

    /**
     * 获取所有的问media文件夹
     */
    @JvmStatic
    fun getAllBucket(context: Context, isImage: Boolean): List<BucketEntity> {
        val bucketBeenList = ArrayList<BucketEntity>()
        val contentResolver = context.contentResolver
        val projection: Array<String>
        if (isImage) {
            projection = arrayOf(
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.ORIENTATION
            )
        } else {
            projection = arrayOf(
                MediaStore.Video.Media.BUCKET_ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME
            )
        }
        val allMediaBucket = BucketEntity()
        allMediaBucket.bucketId = Integer.MIN_VALUE.toString()
        val uri: Uri
        if (isImage) {
            allMediaBucket.bucketName = context.getString(R.string.gallery_all_image)
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        } else {
            allMediaBucket.bucketName = context.getString(R.string.gallery_all_video)
            uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }
        bucketBeenList.add(allMediaBucket)
        var cursor: Cursor? = null
        try {
            cursor = contentResolver.query(
                uri,
                projection,
                null,
                null,
                MediaStore.Video.Media.DATE_ADDED + " DESC"
            )
        } catch (e: Exception) {
            Logger.e(e)
        }

        if (cursor != null && cursor.count > 0) {
            cursor.moveToFirst()
            do {
                val bucketBean = BucketEntity()
                val bucketId: String
                val bucketKey: String
                val cover: String
                if (isImage) {
                    bucketId =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID))
                    bucketBean.bucketId = bucketId
                    val bucketDisplayName =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
                    bucketBean.bucketName = bucketDisplayName
                    bucketKey = MediaStore.Images.Media.BUCKET_ID
                    cover = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
                    val orientation =
                        cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.ORIENTATION))
                    bucketBean.orientation=orientation
                } else {
                    bucketId =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_ID))
                    bucketBean.bucketId = bucketId
                    val bucketDisplayName =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME))
                    bucketBean.bucketName = bucketDisplayName
                    bucketKey = MediaStore.Video.Media.BUCKET_ID
                    cover = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA))
                }
                if (TextUtils.isEmpty(allMediaBucket.cover)) {
                    allMediaBucket.cover=cover
                }
                if (bucketBeenList.contains(bucketBean)) {
                    continue
                }
                //获取数量
                val c = contentResolver.query(
                    uri, projection,
                    "$bucketKey=?", arrayOf(bucketId), null
                )
                if (c != null && c.count > 0) {
                    bucketBean.imageCount = c.count
                }
                bucketBean.cover = cover
                if (c != null && !c.isClosed) {
                    c.close()
                }
                bucketBeenList.add(bucketBean)
            } while (cursor.moveToNext())
        }

        if (cursor != null && !cursor.isClosed) {
            cursor.close()
        }
        return bucketBeenList
    }


    /**
     * 根据原图获取图片相关信息
     */
    fun getMediaEntityWithImage(context: Context, originalPath: String): MediaEntity? {
        val contentResolver = context.contentResolver
        val projection = ArrayList<String>().apply {
            add(MediaStore.Images.Media._ID)
            add(MediaStore.Images.Media.TITLE)
            add(MediaStore.Images.Media.DATA)
            add(MediaStore.Images.Media.BUCKET_ID)
            add(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            add(MediaStore.Images.Media.MIME_TYPE)
            add(MediaStore.Images.Media.DATE_ADDED)
            add(MediaStore.Images.Media.DATE_MODIFIED)
            add(MediaStore.Images.Media.LATITUDE)
            add(MediaStore.Images.Media.LONGITUDE)
            add(MediaStore.Images.Media.ORIENTATION)
            add(MediaStore.Images.Media.SIZE)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                add(MediaStore.Images.Media.WIDTH)
                add(MediaStore.Images.Media.HEIGHT)
            }
        }

        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection.toTypedArray(),
            MediaStore.Images.Media.DATA + "=?",
            arrayOf(originalPath),
            null
        )
        var entity: MediaEntity? = null
        if (cursor != null && cursor.count > 0) {
            cursor.moveToFirst()
            entity = parseImageCursorAndCreateThumImage(context, cursor)
        }
        if (cursor != null && !cursor.isClosed) {
            cursor.close()
        }
        return entity
    }

    /**
     * 根据地址获取视频相关信息
     */
    fun getMediaEntityWithVideo(context: Context, originalPath: String): MediaEntity? {
        val contentResolver = context.contentResolver
        val projection = ArrayList<String>()
        projection.add(MediaStore.Video.Media._ID)
        projection.add(MediaStore.Video.Media.TITLE)
        projection.add(MediaStore.Video.Media.DATA)
        projection.add(MediaStore.Video.Media.BUCKET_ID)
        projection.add(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)
        projection.add(MediaStore.Video.Media.MIME_TYPE)
        projection.add(MediaStore.Video.Media.DATE_ADDED)
        projection.add(MediaStore.Video.Media.DATE_MODIFIED)
        projection.add(MediaStore.Video.Media.LATITUDE)
        projection.add(MediaStore.Video.Media.LONGITUDE)
        projection.add(MediaStore.Video.Media.SIZE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            projection.add(MediaStore.Video.Media.WIDTH)
            projection.add(MediaStore.Video.Media.HEIGHT)
        }
        val cursor = contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection.toTypedArray(),
            MediaStore.Images.Media.DATA + "=?",
            arrayOf(originalPath), null
        )
        var MediaEntity: MediaEntity? = null
        if (cursor != null && cursor.count > 0) {
            cursor.moveToFirst()
            MediaEntity = parseVideoCursorAndCreateThumImage(context, cursor)
        }
        if (cursor != null && !cursor.isClosed) {
            cursor.close()
        }
        return MediaEntity
    }

}