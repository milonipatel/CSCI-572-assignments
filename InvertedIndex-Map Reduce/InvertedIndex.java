import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class InvertedIndex {

 public static class TokenizerMapper
 extends Mapper < LongWritable, Text, Text, Text > {


  private Text word_text = new Text();
  Text documentid = new Text();

  public void map(LongWritable key, Text value, Context context) throws IOException,
  InterruptedException {

   String line = value.toString();
   StringTokenizer tokens = new StringTokenizer(line);
   
   String docidstr = tokens.nextToken();
   documentid = new Text(docidstr);
   
   
   while (tokens.hasMoreTokens()) {
	   String resultString = tokens.nextToken().replaceAll("[^a-zA-Z]+", "").toLowerCase();
	  // resultString=resultString.toLowerCase();
	   word_text.set(resultString);  
    context.write(word_text, documentid);
   }
  }
 }

 public static class IntSumReducer
 extends Reducer < Text, Text, Text, Text > {



  public void reduce(Text key, Iterable < Text > values,
   Context context
  ) throws IOException,
  InterruptedException {

   HashMap < String, Integer > hmap = new HashMap < String, Integer > ();
   Iterator < Text > itr = values.iterator();
   //int freq = 0;
   String value_count;
   while (itr.hasNext()) {
    value_count = itr.next().toString();
    if (hmap.containsKey(value_count)) {
    	Integer v = hmap.get(value_count);

     hmap.put(value_count, new Integer(v + 1));

    } else {
     hmap.put(value_count, 1);
    }

   }
   StringBuffer input = new StringBuffer("");
   for (Map.Entry < String, Integer > map: hmap.entrySet()) {
    input.append(map.getKey() + ":" + map.getValue() + "\t");

   }
   context.write(key, new Text(input.toString()));


  }
 }

 public static void main(String[] args) throws Exception {
  Configuration config = new Configuration();
  Job job = Job.getInstance(config,"inverted index");
  
  job.setJarByClass(InvertedIndex.class);
  job.setMapperClass(TokenizerMapper.class);
  job.setReducerClass(IntSumReducer.class);
  job.setOutputKeyClass(Text.class);
  job.setOutputValueClass(Text.class);
  FileInputFormat.addInputPath(job, new Path(args[0]));
  FileOutputFormat.setOutputPath(job, new Path(args[1]));
  System.exit(job.waitForCompletion(true) ? 0 : 1);
 }
}