package com.signalcollect.dcop.test

import com.dropbox.core.DbxAppInfo
import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.DbxWebAuthNoRedirect
import java.util.Locale
import java.io.BufferedReader
import java.io.InputStreamReader
import com.dropbox.core.DbxAuthFinish
import com.dropbox.core.DbxClient
import java.io.File
import java.io.FileInputStream
import com.dropbox.core.DbxEntry
import com.dropbox.core.DbxWriteMode
import java.io.FileOutputStream

object DropBoxTest extends App {

        val APP_KEY = "sj05wf0r35ikx7w";
        val APP_SECRET = "6oufdnbybuh0991";
        val appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);
        val config = new DbxRequestConfig("JavaTutorial/1.0",Locale.getDefault().toString());
        val webAuth = new DbxWebAuthNoRedirect(config, appInfo);
        val finish = new DbxAuthFinish("LebVIiczFlUAAAAAAAAAAeqa2TTTkoMklh6qxKOLwgia_J0cv6-zrcPOvNyQLkSy","49462298",null)
        val client = new DbxClient(config, finish.accessToken);

        System.out.println("Linked account: " + client.getAccountInfo().displayName);

        val inputFile = new File("graphs/example.txt");
        val inputStream = new FileInputStream(inputFile);
        try {
            val uploadedFile = client.uploadFile("/magnum-opus.txt",
                DbxWriteMode.add(), inputFile.length(), inputStream);
            System.out.println("Uploaded: " + uploadedFile.toString());
        } finally {
            inputStream.close();
        }

        val listing = client.getMetadataWithChildren("/");
        System.out.println("Files in the root path:");
        for(i <- 0 to listing.children.size() - 1){
          System.out.println("	" + listing.children.get(i).name + ": " + listing.children.get(i).toString());
        }

        val outputStream = new FileOutputStream("magnum-opus.txt");
        try {
            val downloadedFile = client.getFile("/magnum-opus.txt", null,
                outputStream);
            System.out.println("Metadata: " + downloadedFile.toString());
        } finally {
            outputStream.close();
        }
}