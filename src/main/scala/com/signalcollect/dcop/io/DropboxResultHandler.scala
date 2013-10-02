package com.signalcollect.dcop.io

import java.io.FileInputStream
import com.dropbox.core.DbxAppInfo
import com.dropbox.core.DbxClient
import java.io.FileOutputStream
import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.DbxWebAuthNoRedirect
import com.dropbox.core.DbxAuthFinish
import java.io.File
import com.dropbox.core.DbxWriteMode
import java.util.Locale
import com.signalcollect.dcop.benchmark.BenchmarkModes
import scala.collection.mutable.ListBuffer
import java.io.PrintWriter

class DropboxResultHandler(val fileName: String, val targetName: String, val benchmarkMode: BenchmarkModes.Value) {

  val APP_KEY = "sj05wf0r35ikx7w";
  val APP_SECRET = "6oufdnbybuh0991";
  val appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);
  val config = new DbxRequestConfig("JavaTutorial/1.0", Locale.getDefault().toString());
  val webAuth = new DbxWebAuthNoRedirect(config, appInfo);
  val finish = new DbxAuthFinish("LebVIiczFlUAAAAAAAAAAeqa2TTTkoMklh6qxKOLwgia_J0cv6-zrcPOvNyQLkSy", "49462298", null)
  val client = new DbxClient(config, finish.accessToken);

  def uploadFile() = {
    val inputFile = new File(fileName);
    val inputStream = new FileInputStream(inputFile);
    try {
      val uploadedFile = client.uploadFile(targetName,
        DbxWriteMode.add(), inputFile.length(), inputStream);
      System.out.println("Uploaded: " + uploadedFile.toString());
    } finally {
      inputStream.close();
    }

  }

  private def createFileOnFileSystem(list: ListBuffer[Tuple2[Int, Double]]) = {
    val newLine = "\n"
    val writer = new PrintWriter(new File(fileName))
    write()

    def write() = {
      list.foreach { l =>
        writer.write(l._1.toString)
        writer.write(" ")
        writer.write(l._2.toString)
        writer.write(newLine)
      }
      writer.close()
    }
  }
  
  private def createFileOnFileSystem(result : Double) = {
    val writer = new PrintWriter(new File(fileName))
    write()
    
    def write() = {
      writer.write(result.toString)
    }
  }
}