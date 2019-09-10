package com.micheal.rxgallery.utils

import java.io.File
import kotlin.math.max

object FileNameUtils {

    /**
     * The extension separator character.
     *
     * @since 1.4
     */
    private const val EXTENSION_SEPARATOR = '.'

    /**
     * The extension separator String.
     *
     * @since 1.4
     */
    private const val EXTENSION_SEPARATOR_STR = EXTENSION_SEPARATOR.toString()

    /**
     * The Unix separator character.
     */
    private const val UNIX_SEPARATOR = '/'

    /**
     * The Windows separator character.
     */
    private const val WINDOWS_SEPARATOR = '\\'

    /**
     * The system separator character.
     */
    private val SYSTEM_SEPARATOR = File.separatorChar

    /**
     * The separator character that is the opposite of the system separator.
     */
    private var OTHER_SEPARATOR : Char?=null

    init {
        if (isSystemWindows()) {
            OTHER_SEPARATOR = UNIX_SEPARATOR
        } else {
            OTHER_SEPARATOR = WINDOWS_SEPARATOR
        }
    }

    /**
     * Determines if Windows file system is in use.
     *
     * @return true if the system is Windows
     */
    @JvmStatic
    private fun isSystemWindows(): Boolean {
        return SYSTEM_SEPARATOR == WINDOWS_SEPARATOR
    }

    @JvmStatic
    fun getName(filename: String?): String? {
        if (filename == null) {
            return null
        }
        val index = indexOfLastSeparator(filename)
        return filename.substring(index + 1)
    }

    /**
     * Returns the index of the last directory separator character.
     *
     *
     * This method will handle a file in either Unix or Windows format.
     * The position of the last forward or backslash is returned.
     *
     *
     * The output will be the same irrespective of the machine that the code is running on.
     *
     * @param filename the filename to find the last path separator in, null returns -1
     * @return the index of the last separator character, or -1 if there
     * is no such character
     */
    @JvmStatic
    fun indexOfLastSeparator(filename: String?): Int {
        if (filename == null) {
            return -1
        }

        val lastUnixPos = filename.lastIndexOf(UNIX_SEPARATOR)
        val lastWindowsPos = filename.lastIndexOf(WINDOWS_SEPARATOR)
        return max(lastUnixPos, lastWindowsPos)
    }

}