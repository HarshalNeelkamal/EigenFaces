import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
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
		subtractMeanFromEigenMatrix(arr);
		int cov[][] = covarianceMatrixFor(eigenMatrix);
		Mat vec = new Mat();
		Mat val = new Mat();
		Mat covMat = new Mat(new Size(cov[0].length, cov.length), CvType.CV_32S); 
		covMat = D2arrayToMatrix(cov, covMat);
		covMat.convertTo(covMat, CvType.CV_32F);
		Core.eigen(covMat, vec, val);
		printMtx(cov);
		System.out.println(vec.size().height+" "+vec.size().width);
		System.out.println(val.size().height+" "+val.size().width);
		printMat(val);
		printMat(vec);
		int eigenFace[][] = eigenMatrix; 
		for(int i = 0 ; i < eigenMatrix.length; i++){
			for(int j = 0; j < cov.length; j++){
				int temp = 0;
				for(int k = 0 ; k < cov[j].length; k++){
					eigenFace[i][j] += eigenMatrix[i][k]*cov[k][j];
					temp += cov[k][j];
				}
				eigenFace[i][j] /= temp; 
			}
		}
//		for(int i = 0 ; i < files.length; i ++){
//			Mat image = Imgcodecs.imread(location+"\\"+files[i]);
//			Imgproc.resize(image, image, new Size(200, 200));
//			Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2GRAY);
//			Core.subtract(image, img_new, image);
//			Imgcodecs.imwrite("C:\\Users\\Harshal\\Desktop\\CSYE6205\\storage\\mean_"+i+".jpg", image);
//		}
		
	}
	
	private void printMtx(int A[][]){
		for(int i = 0 ; i < A.length; i ++){
			for(int j = 0 ; j < A[i].length; j ++){
				System.out.print(" "+A[i][j]);
			}
			System.out.println("");
		}
	}
	
	private void printMat(Mat A){
		for(int i = 0 ; i < A.size().height; i ++){
			for(int j = 0 ; j < A.size().width; j ++){
				System.out.print(" "+A.get(i, j)[0]);
			}
			System.out.println("");
		}
	}
	
	private int[][] covarianceMatrixFor(int A[][]){
		int C[][] = new int[A[0].length][A[0].length];
		int AT[][] = transposeOf(A);
		for(int i = 0 ; i < C.length; i ++){
			for(int j = 0 ; j < C.length; j ++){
				for(int k = 0 ; k < A.length; k ++){
					C[i][j] += AT[i][k]*A[k][j];
				}
			}
		}
		return C;
	}
	
	private int[][] transposeOf(int mtx[][]){
		int C[][] = new int[mtx[0].length][mtx.length];
		for(int i = 0 ; i < mtx.length; i ++){
			for(int j = 0 ; j < mtx[i].length; j ++){
				C[j][i] = mtx[i][j];
			}
		}
		return C;
	}
	
	private void addImageToEigenMatrix(Mat img, int index){
		for(int i = 0 ; i < img.size().height; i++){
			for(int j = 0 ; j < img.size().width; j++){
				eigenMatrix[i*(int)(img.size().width) + j][index] = (int)img.get(i, j)[0];
			}
		}
	}
	
	private void subtractMeanFromEigenMatrix(int arr[]){
		
		for(int i = 0 ; i < eigenMatrix.length; i++){
			for(int j = 0 ; j < eigenMatrix[i].length; j++){
				eigenMatrix[i][j] = eigenMatrix[i][j] - arr[i];
				if(eigenMatrix[i][j] < 0){
					eigenMatrix[i][j] = 0;
				}
			}
		}
	}
	
	private int[] vectorizeMat(Mat image){
		int arr[] = new int[(int)image.size().height*(int)image.size().width];
		int count = 0;
		for(int i = 0 ; i < image.size().height; i++){
			for(int j = 0 ; j < image.size().width; j++){
				arr[count] = (int)image.get(i, j)[0];
				count++;
			}
		}
		return arr;
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
	
	private Mat D2arrayToMatrix(int arr[][], Mat img){
		for(int i = 0 ; i < img.size().height; i++){
			for(int j = 0 ; j < img.size().width; j++){
				int data[] = new int[1];
				data[0] = (int)arr[i][j];
				img.put(i, j, data);
			}
		}
		return img;
	}
	
	private int[] meanVector(int[][] eigenMtx){
		int arr[] = new int[eigenMtx.length*eigenMtx[0].length];
		int index = 0;
		for(int i = 0 ; i < eigenMtx.length; i++){
			arr[index] = 0;
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
