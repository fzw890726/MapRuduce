package MapReduce;

import Parser.*;
import Property.*;

import java.io.IOException;
import java.util.TreeMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.codehaus.jackson.map.ObjectMapper;


 
public class Demo {
    
	public static class JsonMapper extends Mapper<Object, Text, Text, InfoWritable> {

		private InfoWritable info = new InfoWritable();
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			try {
				ObjectMapper mapper = new ObjectMapper();
	            String[] tuple = value.toString().split("\\n");
	             for(int i=0;i<tuple.length; i++)
	             {
	            	 Business bus = mapper.readValue(tuple[i], Business.class);
	            	 
	            	 if(bus.reviewCount != null && Integer.parseInt(bus.reviewCount) > 3)
	            	 {
	            		 info.SetName(bus.name);
	            		 info.SetStars(bus.stars);
	            		 if(info!= null && bus.city!= null){
	            		     context.write(new Text(bus.city), info);
	            		 }
	            	 }
	              }

			} catch (JSONException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();

        }
		}
	}
	
	public static class JsonReducer extends Reducer<Text, InfoWritable, Text, Text>{
		private InfoWritable info = new InfoWritable();
		private TreeMap<Text,Text> tree = new TreeMap<Text, Text>();
		public void reduce(Text key, Iterable<InfoWritable> values, Context context) throws IOException, InterruptedException{
			for(InfoWritable info : values)
			{
				tree.put(new Text(info.GetName()),new Text(info.GetStars()));
				if(tree.size() > 10)
				{
					tree.remove(tree.firstKey());
				}
			}
			for(Text t: tree.values())
			{
				context.write(key, t);
			}
		}
	}
	
    public static void main(String[] args) throws Exception {
    	 runJob(args[0], args[1]);
    }
 

    
    public static void runJob(String input, String output) throws Exception {
    	
        Configuration conf = new Configuration();
        Job job = new Job(conf);
        job.setJarByClass(Demo.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(InfoWritable.class);
        job.setMapperClass(JsonMapper.class);
        job.setReducerClass(JsonReducer.class);
        //job.setInputFormatClass(KeyValueTextInputFormat.class);
        job.setNumReduceTasks(1);
        job.setOutputFormatClass(TextOutputFormat.class);

        FileInputFormat.setInputPaths(job, new Path(input));
        Path outPath = new Path(output);
        FileOutputFormat.setOutputPath(job, outPath);
        outPath.getFileSystem(conf).delete(outPath, true);

        job.waitForCompletion(true);
    }
}

