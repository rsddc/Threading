

package com.threading;


import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class FileCopyUtils implements Runnable{

    private final String path;
    private static final String DESTINATION="C:\\Users\\Rakesh\\copy\\";

    public FileCopyUtils( String path) {
        if(path==null||path.trim().length()==0) throw new IllegalArgumentException("proper directory path is required ");
        this.path=path;
    }


    @Override
    public void run(){

        try {
            isCopy(path);
        } catch (IOException e) {
            System.err.println("Error in run method");

            throw new RuntimeException(e);
        }

    }


    private void isCopy(String fileName) throws IOException {// it must be called by executors again and again

        File inputFile = new File(fileName);
//        inputFile.setExecutable(true);
//        inputFile.setReadable(true);
//        inputFile.setWritable(true);

        File outFile = new File(DESTINATION+"//"+fileName.substring(fileName.lastIndexOf("\\")));
//        outFile.setExecutable(true);
//        outFile.setWritable(true);

        try{

                try(InputStream is = new FileInputStream(inputFile);OutputStream os = new FileOutputStream(outFile);) {

                    // buffer size 1K
                    byte[] buf = new byte[1024];

                    int bytesRead;
                    while ((bytesRead = is.read(buf)) > 0) {
                        os.write(buf, 0, bytesRead);
                    }
                }
            System.out.println("File "+inputFile.getName()+" copied successfully in "+outFile.getName()+" directory by "+Thread.currentThread().getName());
        }catch (IOException e){
            System.err.println("File "+inputFile.getName()+" copied failure by "+Thread.currentThread().getName());
            e.printStackTrace();


        }
    }


}


class CopyTask{

    private final String path;

    public CopyTask(String path) {
        this.path = path;
    }

    private List<String> getAllFiles() {
        assert path != null;
        String[] files = new File(path).list();
        assert files != null;
        return Arrays.stream(files).filter(f-> !new File(f).isDirectory() ).toList();
    }

    public void call(){
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        for(String file : getAllFiles()) {
            if (new File(file).isDirectory()==false)
                executorService.submit(new FileCopyUtils(path+"\\"+file));

        }
        if(!executorService.isShutdown()) executorService.shutdown();


    }
}


public class ThreadCopyFilesDemo {


    public static void main(String[] args) {
        CopyTask copy = new CopyTask("C:\\Users\\Rakesh\\Pictures\\Saved Pictures");//user will give source
        copy.call();
    }
}
