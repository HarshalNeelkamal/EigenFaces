import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class EigenFaceTest {
	
	int eigenMatrix[][]; 
	
	private void run(){
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		String location  = "C:\\Users\\Harshal\\Desktop\\CSYE6205\\trainingSet\\apples";
		
		File reader = new File(location);
		String files[] = reader.list();
		eigenMatrix = new int[40000][files.length];
		for(int i = 0 ; i < files.length; i ++){
			Mat image = Imgcodecs.imread(location+"\\"+files[i]);
			Imgproc.resize(image, image, new Size(200, 200));
			Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2GRAY);
			addImageToEigenMatrix(image, i);
		}
		int arr[] = meanVector(eigenMatrix);
		Mat img_new = new Mat(new Size(200, 200), CvType.CV_32S);
		img_new = arrayToMatrix(arr, img_new);
		img_new.convertTo(img_new, 0);
		Imgcodecs.imwrite("C:\\Users\\Harshal\\Desktop\\CSYE6205\\storage\\mean.jpg", img_new);
		for(int i = 0 ; i < files.length; i ++){
			Mat image = Imgcodecs.imread(location+"\\"+files[i]);
			Imgproc.resize(image, image, new Size(200, 200));
			Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2GRAY);
			Core.subtract(image, img_new, image);
			Imgcodecs.imwrite("C:\\Users\\Harshal\\Desktop\\CSYE6205\\storage\\mean_"+i+".jpg", image);
		}
	}
	
	private void addImageToEigenMatrix(Mat img, int index){
		for(int i = 0 ; i < img.size().height; i++){
			for(int j = 0 ; j < img.size().width; j++){
				eigenMatrix[i*(int)(img.size().width) + j][index] = (int)img.get(i, j)[0];
			}
		}
	}
	
	private Mat arrayToMatrix(int arr[], Mat img){
		for(int i = 0 ; i < img.size().height; i++){
			for(int j = 0 ; j < img.size().width; j++){
				int data[] = new int[1];
				data[0] = (int)arr[i*(int)img.size().width + j];
				img.put(i, j, data);
			}
		}
		return img;
	}
	
	private int[] meanVector(int[][] eigenMtx){
		int arr[] = new int[eigenMtx.length*eigenMtx[0].length];
		int index = 0;
		for(int i = 0 ; i < eigenMtx.length; i++){
			for(int j = 0 ; j < eigenMtx[i].length; j++){
				arr[index] += eigenMtx[i][j]; 
			}
			arr[index] /= eigenMtx[i].length;
			index ++;
		}
		return arr;
	}
	
	
	
	public static void main(String args[]) throws FileNotFoundException{
		EigenFaceTest obj = new EigenFaceTest();
		obj.run();
	}
	
}
