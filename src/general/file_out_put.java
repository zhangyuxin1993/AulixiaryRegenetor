package general;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class file_out_put {

	public void filewrite(String write_name, String string) throws IOException {
		// TODO Auto-generated method stub
	 File f=new File(write_name);
	 FileWriter out=new FileWriter(f,true);
	 out.write(string);
	 out.close();
	}
	public void filewrite2(String filename, String result) {
		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(filename, true);
			byte[] buffer1 = result.getBytes();
			fos.write(buffer1);

			String str = "\r\n";
			byte[] buffer = str.getBytes();

			fos.write(buffer);
			fos.close();
		} catch (FileNotFoundException e1) {
			System.out.println(e1);
		} catch (IOException e1) {
			System.out.println(e1);
		}
	}
/*
 * 输出不换行
 */
	public void filewrite_without(String filename, String result) {
		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(filename, true);
			byte[] buffer1 = result.getBytes();
			fos.write(buffer1);

//			String str = "\r\n";
//			byte[] buffer = str.getBytes();
//
//			fos.write(buffer);
			fos.close();
		} catch (FileNotFoundException e1) {
			System.out.println(e1);
		} catch (IOException e1) {
			System.out.println(e1);
		}
	}
	
	public void filewrite(String filename, int result) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(filename, true);
			String str1 = String.valueOf(result);
			byte[] buffer1 = str1.getBytes();
			fos.write(buffer1);
			String str = "\r\n";
			byte[] buffer = str.getBytes();
			fos.write(buffer);
			fos.close();
		} catch (FileNotFoundException e1) {
			System.out.println(e1);
		} catch (IOException e1) {
			System.out.println(e1);
		}
	}
	
	/*
	 * 输出result不换行
	 */
	public void filewrite_without(String filename, int result) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(filename, true);
			String str1 = String.valueOf(result);
			byte[] buffer1 = str1.getBytes();
			fos.write(buffer1);
//			String str = "\r\n";
//			byte[] buffer = str.getBytes();
//			fos.write(buffer);
			fos.close();
		} catch (FileNotFoundException e1) {
			System.out.println(e1);
		} catch (IOException e1) {
			System.out.println(e1);
		}
	}

}
