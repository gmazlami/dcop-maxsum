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
import java.util.Date

class DropboxResultHandler(val name: String,val folder : String, val benchmarkMode: BenchmarkModes.Value) {

  val APP_KEY = "sj05wf0r35ikx7w";
  val APP_SECRET = "6oufdnbybuh0991";
  val appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);
  val config = new DbxRequestConfig("JavaTutorial/1.0", Locale.getDefault().toString());
  val webAuth = new DbxWebAuthNoRedirect(config, appInfo);
  val finish = new DbxAuthFinish("LebVIiczFlUAAAAAAAAAAeqa2TTTkoMklh6qxKOLwgia_J0cv6-zrcPOvNyQLkSy", "49462298", null)
  val client = new DbxClient(config, finish.accessToken);
  val targetName = "/"+folder+"/"+generateFileName(name)
  val fileName = "dropbox/" + generateFileName(name)
  
  def handleResult(result : Any) = {
    result match {
      case l : List[_] =>createFileOnFileSystem(l.asInstanceOf[List[Tuple2[Long,Int]]]);uploadFile
      case n : Long => createFileOnFileSystem(n); uploadFile 
      case i : Int => createFileOnFileSystem(i.toLong); uploadFile
    }
  }
  
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

  private def createFileOnFileSystem(list: List[Tuple2[Long, Int]]) = {
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
  
  private def createFileOnFileSystem(result : Long) = {
    val writer = new PrintWriter(new File(fileName))
    write()
    
    def write() = {
      writer.write(result.toString)
    }
  }
  
  private def generateFileName(string: String):String = {
  val modeName: String = benchmarkMode.toString()
  val dateTime : Date = new Date(System.currentTimeMillis())
  val timeStamp = dateTime.getDate() +"-"+(dateTime.getMonth()+1)+"-"+(dateTime.getYear()+1900)+"T"+dateTime.getHours()+"-"+dateTime.getMinutes()+"-"+dateTime.getSeconds()
  val fName = modeName + "_" + name + "_" + timeStamp + ".txt"
  val newLine = "\n"
  val tabulator = "\t"
    
  fName
  }
}