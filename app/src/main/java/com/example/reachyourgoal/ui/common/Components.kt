package com.example.reachyourgoal.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DiscFull
import androidx.compose.material.icons.filled.Gif
import androidx.compose.material.icons.filled.Html
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LaptopWindows
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun getFileIcon(extension: String?): ImageVector {
    return when (extension) {
        "pdf" -> Icons.Filled.PictureAsPdf
        "mp3", "wav", "ogg" -> Icons.Filled.MusicNote
        "mp4", "avi", "mov" -> Icons.Filled.VideoLibrary
        "jpg", "jpeg", "png" -> Icons.Filled.Image
        "txt" -> Icons.Filled.Description
        "doc", "docx" -> Icons.Filled.Description
        "xls", "xlsx" -> Icons.Filled.Description
        "ppt", "pptx" -> Icons.Filled.Description
        "zip", "rar" -> Icons.Filled.Archive
        "apk" -> Icons.Filled.Android
        "exe" -> Icons.Filled.LaptopWindows
        "iso" -> Icons.Filled.DiscFull
        "html", "htm" -> Icons.Filled.Html
        "css" -> Icons.Filled.Code
        "js" -> Icons.Filled.Code
        "java" -> Icons.Filled.Code
        "kt" -> Icons.Filled.Code
        "cpp" -> Icons.Filled.Code
        "py" -> Icons.Filled.Code
        "json" -> Icons.Filled.Code
        "xml" -> Icons.Filled.Code
        "svg" -> Icons.Filled.Image
        "gif" -> Icons.Filled.Gif
        "mpg", "mpeg" -> Icons.Filled.VideoLibrary
        "csv" -> Icons.Filled.TableChart
        "log" -> Icons.Filled.Info
        "apk" -> Icons.Filled.Android
        "bat", "cmd" -> Icons.Filled.Code
        "dll" -> Icons.Filled.LaptopWindows
        "jar" -> Icons.Filled.Code
        "bak" -> Icons.Filled.Description
        "dat" -> Icons.Filled.Description
        "db", "dbf" -> Icons.Filled.Description
        "dll" -> Icons.Filled.LaptopWindows
        "gz", "gzip" -> Icons.Filled.Archive
        "jar" -> Icons.Filled.Code
        "obj" -> Icons.Filled.Description
        "bin" -> Icons.Filled.Description
        "sys" -> Icons.Filled.Description
        "wma" -> Icons.Filled.MusicNote
        "wmv" -> Icons.Filled.VideoLibrary
        "txt" -> Icons.Filled.Description
        "xls", "xlsx" -> Icons.Filled.Description
        "ppt", "pptx" -> Icons.Filled.Description
        "java" -> Icons.Filled.Code
        "kt" -> Icons.Filled.Code
        "cpp" -> Icons.Filled.Code
        "py" -> Icons.Filled.Code
        "json" -> Icons.Filled.Code
        "xml" -> Icons.Filled.Code
        "csv" -> Icons.Filled.TableChart
        "bak" -> Icons.Filled.Description
        "dat" -> Icons.Filled.Description
        "db", "dbf" -> Icons.Filled.Description
        "gz", "gzip" -> Icons.Filled.Archive
        "bat", "cmd" -> Icons.Filled.Code
        "bin" -> Icons.Filled.Description
        "sys" -> Icons.Filled.Description
        "wma" -> Icons.Filled.MusicNote
        "wmv" -> Icons.Filled.VideoLibrary
        "ico" -> Icons.Filled.Image
        "php" -> Icons.Filled.Code
        "psd" -> Icons.Filled.Image
        "rar" -> Icons.Filled.Archive
        "rpm" -> Icons.Filled.Description
        "tar" -> Icons.Filled.Archive
        "tif", "tiff" -> Icons.Filled.Image
        "bak" -> Icons.Filled.Description
        "dat" -> Icons.Filled.Description
        "db", "dbf" -> Icons.Filled.Description
        "gz", "gzip" -> Icons.Filled.Archive
        "bat", "cmd" -> Icons.Filled.Code
        "bin" -> Icons.Filled.Description
        "sys" -> Icons.Filled.Description
        "wma" -> Icons.Filled.MusicNote
        "wmv" -> Icons.Filled.VideoLibrary
        "ico" -> Icons.Filled.Image
        "php" -> Icons.Filled.Code
        "psd" -> Icons.Filled.Image
        "rpm" -> Icons.Filled.Description
        "tar" -> Icons.Filled.Archive
        "tif", "tiff" -> Icons.Filled.Image
        "ai" -> Icons.Filled.Image
        "css" -> Icons.Filled.Code
        "dll" -> Icons.Filled.LaptopWindows
        "fla" -> Icons.Filled.Description
        "htm", "html" -> Icons.Filled.Html
        "jar" -> Icons.Filled.Code
        "log" -> Icons.Filled.Info
        "sql" -> Icons.Filled.Description
        "wav" -> Icons.Filled.MusicNote
        "xls", "xlsx" -> Icons.Filled.Description
        "zip" -> Icons.Filled.Archive
        "db" -> Icons.Filled.Description
        "iso" -> Icons.Filled.DiscFull
        "bak" -> Icons.Filled.Description
        "dat" -> Icons.Filled.Description
        "dbf" -> Icons.Filled.DiscFull
        "gzip" -> Icons.Filled.Archive
        "cmd" -> Icons.Filled.Code
        "sys" -> Icons.Filled.Description
        "wma" -> Icons.Filled.MusicNote
        "wmv" -> Icons.Filled.VideoLibrary
        "ico" -> Icons.Filled.Image
        "php" -> Icons.Filled.Code
        "psd" -> Icons.Filled.Image
        "tar" -> Icons.Filled.Archive
        "tif", "tiff" -> Icons.Filled.Image
        else -> Icons.Filled.Description
    }
}
