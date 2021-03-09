package com.xxf.media.album.repo

import android.Manifest
import android.content.ContentUris
import android.provider.MediaStore
import android.text.TextUtils
import androidx.core.content.ContentResolverCompat
import androidx.fragment.app.FragmentActivity
import com.xxf.media.album.internal.utils.PathUtils
import com.xxf.permission.RxPermissions
import com.xxf.permission.transformer.RxPermissionTransformer
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.Callable

/**
 * @Author: XGod  xuanyouwu@163.com  17611639080
 * Date: 3/8/21 6:52 PM
 * Description: 相册
 */
object AlbumService {
    // ===========================================================
    // === params for album ALL && showSingleMediaType: true ===
    private const val SELECTION_ALL_FOR_SINGLE_MEDIA_TYPE = (MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
            + " AND " + MediaStore.MediaColumns.SIZE + ">0")
    private val QUERY_URI = MediaStore.Files.getContentUri("external")
    private val PROJECTION = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.SIZE,
            "duration")


    // ===============================================================
    private const val ORDER_BY = MediaStore.Images.Media.DATE_TAKEN + " DESC"

    /**
     * 获取所有图片
     * 自动请求权限
     */
    fun getImages(context: FragmentActivity): Observable<List<String>> {
        return Observable
                .defer<Boolean> {
                    RxPermissions(context).request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .compose(RxPermissionTransformer(context, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                }
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap {
                    Observable
                            .fromCallable<List<String>>(object : Callable<List<String>> {
                                override fun call(): List<String> {
                                    val results = mutableListOf<String>();
                                    val selection = SELECTION_ALL_FOR_SINGLE_MEDIA_TYPE
                                    val selectionArgs = arrayOf<String>(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString());
                                    val cursor = ContentResolverCompat.query(context.getContentResolver(),
                                            QUERY_URI, PROJECTION, selection, selectionArgs, ORDER_BY, null);
                                    try {
                                        if (cursor != null) {
                                            while (cursor.moveToNext()) {
                                                val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID));
                                                val contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                                                val path = PathUtils.getPath(context, contentUri);
                                                if (!TextUtils.isEmpty(path)) {
                                                    results.add(path);
                                                }
                                            }
                                        }
                                    } finally {
                                        try {
                                            cursor.close();
                                        } catch (e: Throwable) {
                                            e.printStackTrace()
                                        }
                                    }
                                    return results;
                                }

                            })
                            .subscribeOn(Schedulers.io());
                }
    }

    /**
     * 获取所有视频
     * 自动请求权限
     */
    fun getVideos(context: FragmentActivity): Observable<List<String>> {
        return Observable
                .defer<Boolean> {
                    RxPermissions(context).request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .compose(RxPermissionTransformer(context, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                }
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap {
                    Observable
                            .fromCallable<List<String>>(object : Callable<List<String>> {
                                override fun call(): List<String> {
                                    val results = mutableListOf<String>();
                                    val selection = SELECTION_ALL_FOR_SINGLE_MEDIA_TYPE
                                    val selectionArgs = arrayOf<String>(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString());
                                    val cursor = ContentResolverCompat.query(context.getContentResolver(),
                                            QUERY_URI, PROJECTION, selection, selectionArgs, ORDER_BY, null);
                                    try {
                                        if (cursor != null) {
                                            while (cursor.moveToNext()) {
                                                val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID));
                                                val contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                                                val path = PathUtils.getPath(context, contentUri);
                                                if (!TextUtils.isEmpty(path)) {
                                                    results.add(path);
                                                }
                                            }
                                        }
                                    } finally {
                                        try {
                                            cursor.close();
                                        } catch (e: Throwable) {
                                            e.printStackTrace()
                                        }
                                    }
                                    return results;
                                }
                            })
                            .subscribeOn(Schedulers.io());
                }
    }
}