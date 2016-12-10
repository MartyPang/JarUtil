package com.ecnu.psf;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

/**
 * Create by Marty 16-12-10
 */
public class JarUtility
{
    private static void backupJarFile(String jarPath) throws IOException{
        dealJarFile(jarPath+"/ihpones.jar", jarPath+"/Oldiphones/ihpones.jar");
    }

    public static void recoverJarFile(String jarPath) throws IOException{
        dealJarFile(jarPath+"/Oldiphones/ihpones.jar", jarPath+"/ihpones.jar");
        deleteFile(jarPath+"/Oldiphones");
    }

    private static void dealJarFile(String filePath, String outputPath) throws IOException{
        File jarFile = new File(filePath);
        if(jarFile.exists() && jarFile!=null){
            makeSupDir(outputPath);
            writeFile(jarFile, new File(outputPath));
        }
    }

    private static void makeSupDir(String outFileName) {
        Pattern p = Pattern.compile("[/\\" + File.separator + "]");
        Matcher m = p.matcher(outFileName);
        while (m.find()) {
            int index = m.start();
            String subDir = outFileName.substring(0, index);
            File subDirFile = new File(subDir);
            if (!subDirFile.exists())
                subDirFile.mkdir();
        }
    }

    private static void zipFile(String jarPath) throws IOException{
        JarOutputStream ops = new JarOutputStream(new FileOutputStream(jarPath+"/ihpones.jar"));
        File inFile = new File(jarPath+"/iphones");
        zipFileEntry("", inFile, ops);
        ops.close();
    }

    private static void zipFileEntry(String base, File inFile, JarOutputStream ops) throws IOException{
        if(inFile.isDirectory()){
            File[] files = inFile.listFiles();
            ops.putNextEntry(new ZipEntry(base + "/"));
            base = base.length() == 0 ? "" : base + "/";
            for(File file:files){
                zipFileEntry(base+file.getName(), file, ops);
            }
        }else{
            ops.putNextEntry(new JarEntry(base));
            InputStream ips = new FileInputStream(inFile);
            int len = 0;
            byte[] buffer = new byte[1024];
            while((len = ips.read(buffer)) != -1){
                ops.write(buffer,0,len);
                //ops.flush();
            }
            ips.close();
        }
    }

    /**
     * Unzip Jar file
     * @param jarPath use absolute path
     * @throws IOException
     */
    private static void unZipFile(String jarPath) throws IOException{
        JarFile jarFile = new JarFile(jarPath+"commons-io-2.5.jar");
        //jarEntry example
        /** org/
         *  org/apache
         *  org/apache/hadoop/xxxx.class
         */
        Enumeration<JarEntry> jarEntrys = jarFile.entries();
        while(jarEntrys.hasMoreElements()){
            JarEntry jarEntry = jarEntrys.nextElement();
            jarEntry.getName();
            String outFileName = jarPath + "extract/" + jarEntry.getName();
//            if(outFileName.contains("com/") ){
//                outFileName = outFileName.replace("com/", "");
//            }
            File f = new File(outFileName);
            makeSupDir(outFileName);
            //如果是目录，继续
            if(jarEntry.isDirectory()){
                continue;
            }
            //如果是class文件，将文件写到解压目录下
            writeFile(jarFile.getInputStream(jarEntry), f);
        }
    }

    private static void deleteFile(String jarPath) throws IOException{
        File delFile = delFile = new File(jarPath);
        if(delFile.exists() && delFile.isDirectory()){
            if(delFile.listFiles().length==0){
                delFile.delete();
            } else {
                for(File file:delFile.listFiles()){
                    if(file.isDirectory()){
                        deleteFile(file.getAbsolutePath());
                    }
                    file.delete();
                }
            }
        }
        if(delFile.exists() && delFile.isDirectory() && delFile.listFiles().length==0){
            delFile.delete();
        }
    }

    private static void writeFile(File inputFile, File outputFile) throws IOException{
        writeFile(new FileInputStream(inputFile), outputFile);
    }

    private static void writeFile(InputStream ips, File outputFile) throws IOException{
        OutputStream ops = new BufferedOutputStream(new FileOutputStream(outputFile));
        try{
            byte[] buffer = new byte[1024];
            int nBytes = 0;
            while ((nBytes = ips.read(buffer)) > 0){
                ops.write(buffer, 0, nBytes);
            }
        }catch (IOException ioe){
            throw ioe;
        } finally {
            try {
                if (null != ops){
                    ops.flush();
                    ops.close();
                }
            } catch (IOException ioe){
                throw ioe;
            } finally{
                if (null != ips){
                    ips.close();
                }
            }
        }
    }

    public static void changeJarFile(String jarPath){
        try {
            backupJarFile(jarPath);
            unZipFile(jarPath);
            zipFile(jarPath);
            deleteFile(jarPath+"/iphones");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException{
        String jarPath = "F:\\IdeaProject\\";
        List entries = new ArrayList<String>();

        JarFile jarFile = new JarFile(jarPath+"commons-io-2.5.jar");
        //jarEntry example
        /** org/
         *  org/apache
         *  org/apache/hadoop/xxxx.class
         */
        Enumeration<JarEntry> jarEntrys = jarFile.entries();
        while(jarEntrys.hasMoreElements()){
            JarEntry jarEntry = jarEntrys.nextElement();
            //如果是目录，继续
            if(jarEntry.isDirectory()){
                continue;
            }
            //如果是class文件，记录jarEntry

            if(jarEntry.getName().contains(".class")){
                //entries.add(jarEntry.getName());
                System.out.println(jarEntry.getName());
            }
        }
    }
}
