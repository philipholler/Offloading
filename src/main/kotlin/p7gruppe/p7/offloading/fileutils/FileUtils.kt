package p7gruppe.p7.offloading.fileutils;

import java.io.*
import java.lang.StringBuilder
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

val hasher: Hasher = Hasher()

fun checkZipFilesEquality(file1: File, file2: File): Boolean{
    var file1Map = getFileToHashMap(file1)
    var file2Map = getFileToHashMap(file2)

    // Check that all keys are contained in both
    if(!file1Map.keys.equals(file2Map.keys)){
        println("Keysets not equal")
        throw ZipFilesNotEqualException("Keysets not equal")
    }
    // Check that hashes of files are identical
    for(key in file1Map.keys){
        if(file1Map[key] != file2Map[key]){
            println("file ${key} not equal. file1 ${file1Map[key]}, file2 ${file2Map[key]}")
            throw ZipFilesNotEqualException("file ${key} not equal. file1 ${file1Map[key]}, file2 ${file2Map[key]}")
        }
    }

    return true
}

private fun getFileToHashMap(file: File): MutableMap<String, String> {
    // Result map from file path to hash value
    var resultMap: MutableMap<String, String> = mutableMapOf();

    val zipStream = ZipInputStream(file.inputStream())

    var zipEntry: ZipEntry? = zipStream.nextEntry

    while(zipEntry != null){
        // Relative path from zipped file
        val filePath = zipEntry.name;

        // If a folder, continue
        if (filePath!!.endsWith("/")){
            zipEntry = zipStream.nextEntry
            continue
        }
        // Check if the file starts with . or __, then ignore
        val filename = filePath.substring(filePath.lastIndexOf("/") + 1)
        if(filename.startsWith(".") || filename.startsWith("__")){
            zipEntry = zipStream.nextEntry
            continue
        }

        var data = StringBuilder() // Data accumulator
        val reader = zipStream.bufferedReader()
        var line: String
        val it = reader.lineSequence().iterator();
        while(it.hasNext()){
            line = it.next()
            data.append(line)
        }

        val hash = hasher.hash(data.toString());

        resultMap.put(key = filePath.substring(filePath.indexOf("/") + 1), value = hash)

        zipEntry = zipStream.nextEntry
    }

    return resultMap
}

fun unzip(inStream: InputStream, outputFolder: File) {
    val zipStream = ZipInputStream(inStream)
    var ze: ZipEntry? = zipStream.nextEntry
    val buffer = ByteArray(2048)
    while (ze != null) {
        val fileName = ze.name
        val newFile = File(outputFolder.path + File.separator + fileName)
        if (ze.isDirectory || fileName.endsWith("\\") || fileName.endsWith(File.separator) || fileName.endsWith("/")) {
            newFile.mkdirs()
        } else {
            File(newFile.parent).mkdirs()
            newFile.createNewFile()
            val fos = FileOutputStream(newFile)
            var len: Int
            while (true) {
                len = zipStream.read(buffer)
                if (len <= 0)
                    break
                fos.write(buffer, 0, len)
            }
            fos.close()
        }

        zipStream.closeEntry()
        ze = zipStream.nextEntry
    }
    zipStream.close()
}

fun encodeFileForUpload(path: String): ByteArray {
    var f = File(path)
    var encoded = Base64.getEncoder().encodeToString(f.readBytes())
    return encoded.toByteArray();
}

fun zipDir(directory: String, destPath: String) {
    val sourceFile = File(directory)
    zipAll(sourceFile, FileOutputStream(destPath))
}

fun zipAll(folderToZip: File, outputStream: OutputStream) {
    val zipOut = ZipOutputStream(BufferedOutputStream(outputStream))
    zipRecursive(zipOut, folderToZip, "")
    zipOut.close()
}

private fun zipRecursive(zipOut: ZipOutputStream, sourceFile: File, parentDirPath: String) {
    val data = ByteArray(2048)
    sourceFile.listFiles()?.forEach { f ->
        val path = (if (parentDirPath.isNotEmpty()) parentDirPath + File.separator else "") + f.name
        if (f.isDirectory) {
            val entry = ZipEntry(path + File.separator) // Separator is added to indicate that this is a folder
            entry.time = f.lastModified()
            entry.isDirectory
            entry.size = f.length()
            zipOut.putNextEntry(entry)
            //Call recursively to add files within this directory
            zipRecursive(zipOut, f, path)
            zipOut.closeEntry()
        } else {
            FileInputStream(f).use { fi ->
                BufferedInputStream(fi).use { origin ->
                    val entry = ZipEntry(path)
                    entry.time = f.lastModified()
                    entry.isDirectory
                    entry.size = f.length()
                    zipOut.putNextEntry(entry)
                    while (true) {
                        val readBytes = origin.read(data)
                        if (readBytes == -1) {
                            break
                        }
                        zipOut.write(data, 0, readBytes)
                    }
                    zipOut.closeEntry()
                }
            }
        }
    }

}



