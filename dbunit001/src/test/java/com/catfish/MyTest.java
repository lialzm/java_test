package com.catfish;

import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by lcy on 17/5/18.
 */
public class MyTest {


    @Test
    public void ReverseListTest() throws Exception {
        String txt = new FileOperate().readFile("/Users/new/source/ideaWorkspace001/java_test/dbunit001/src/test/java/com/catfish/word.txt");
        String[] strings = txt.split(" ");
        List list = Arrays.asList(strings);
        ReverseList reverseList = new ReverseList<String>(list);
        Iterable<String> iterable = reverseList.reversed();
        Iterator<String> iterable1= iterable.iterator();
        StringBuffer stringBuffer=new StringBuffer();
        while (iterable1.hasNext()){
           String s= iterable1.next();
           stringBuffer.append(s+" ");
        }
        new FileOperate().outFile("/Users/new/source/ideaWorkspace001/java_test/dbunit001/src/test/java/com/catfish/hh.txt",stringBuffer.toString());
    }


    @Test
    public void aaa(){
       String[] strings= "aa poverty disreputable.Cultivate;q".split(" |\\.|;");
       System.out.println(Arrays.asList(strings));
    }

}
