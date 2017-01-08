package Property;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public class InfoWritable implements Writable {

	private String name;
	private String stars;
	
	
	@Override
	public void readFields(DataInput in) throws IOException {
		// TODO Auto-generated method stub
		name = in.readUTF();  
		stars =  in.readUTF();
	}

	@Override
	public void write(DataOutput out) throws IOException {
		// TODO Auto-generated method stub
		out.writeUTF(name);
		out.writeUTF(stars);
	}

	public void SetName(String name)
	{
		this.name = name;
	}
	
	public void SetStars(String stars)
	{
		this.stars = stars;
	}
	
	public String GetName()
	{
		return name;
	}
	
	public String GetStars()
	{
		return stars;
	}
}
