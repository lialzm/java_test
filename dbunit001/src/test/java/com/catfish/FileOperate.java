package com.catfish;

/**
 * Created by lcy on 17/5/18.
 */

import java.io.*;

class FileOperate {
    public String readFile(String pathName) throws FileNotFoundException {

        FileInputStream inputStream = new FileInputStream(pathName);
        try {

            int ch;
            StringBuffer sb = new StringBuffer();
            while ((ch = inputStream.read()) != -1) {
                if ((char) ch == "\n".toCharArray()[0]) {
                    sb.append(" ");
                }else {
                    sb.append((char) ch);
                }
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void outFile(String pathName, String result) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(new File(pathName));
        outputStream.write(result.replace("\n","").getBytes());
    }
}
