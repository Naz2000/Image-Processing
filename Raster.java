//INDEX
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;

import javax.imageio.ImageIO;

public class Raster {
	
	BufferedImage  image;
   	 
	
	public static void RGBtoHSB(){
		try{
			// Convert RGB image to HSB and thresholding according hue value
			int width = 512;
			int height = 384;
			File f = null;
			f = new File("Lymphocytes1.jpg");
			// BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
			BufferedImage image = ImageIO.read(f);
			 width = image.getWidth() ;
			 height = image.getHeight() ;
			 BufferedImage imgout = new BufferedImage(width,height,BufferedImage.TYPE_BYTE_BINARY);
			 WritableRaster raster = imgout.getRaster();
			 for(int i =0; i<width; i++){
				 for(int j = 0; j<height; j++){
					 int rgb = image.getRGB(i, j);
					 int red = (rgb >> 16) & 0xff;
			         int green = (rgb >> 8) & 0xff;
			         int blue = rgb & 0xff;
			         float[] hsb = Color.RGBtoHSB(red, green, blue, null);
			         float hue = hsb[0];

			            if(hue > 0.553  &&  hue < 0.823){
			            	raster.setSample(i, j, 0, 1); //0 is band 1 is white
			            }else{
			            	raster.setSample(i, j, 0, 0);
			            	}
			          
				 }
				 
			 }//End of loop(RGB to HSB)
			 
			 //morphological erosion operation
				 BufferedImage imgout2 = new BufferedImage(width,height,BufferedImage.TYPE_BYTE_BINARY);
				 WritableRaster raster2 = imgout2.getRaster();
				 
				 for(int i =1; i<width-1; i++){
					 for(int j = 1; j<height-1; j++){            // Introduce image
						 int val = raster.getSample(i, j, 0); //0 is band here, this is a pixel
 
						 if(val == 1){
							 for(int in = -1; in <=1; in++){ // Check thr neighbour
								 
								 for(int jn = -1; jn <=1; jn++){
									   int valn = raster.getSample(i+in, j+jn, 0);
									   if(valn == 0){raster2.setSample(i, j, 0, 1);
									   jn = 30;
									   in = 30;}
									   
									  
									   
								 	}//end of second loop
							 }//end of secod loop
						 } //end of if
						 if(val == 0){raster2.setSample(i, j, 0, 1);}
							 }//end of first loop
					 
				 								 
							 
				 }//End of loop(morphological erosion operation)
				 
				 //obtained after binary edge detection
				 BufferedImage imgout3 = new BufferedImage(width,height,BufferedImage.TYPE_BYTE_BINARY);
				 WritableRaster raster3 = imgout3.getRaster();
				 for(int i =1; i<width-1; i++){
					 for(int j = 1; j<height-1; j++){            // Introduce image
						 int val2 = raster2.getSample(i, j, 0); 
						 if(val2 == 1)
							 for(int in2 = -1; in2 <=1; in2++){ // Check thr neighbour
								 
								 for(int jn2 = -1; jn2 <=1; jn2++){
									   int valn2 = raster2.getSample(i+in2, j+jn2, 0);
									   if(valn2 == 0){raster3.setSample(i, j, 0, 1);}
									  
								 }
							 }
						 }
							
						 }//End of loop(after edge detection)
				 
				 //Computing the Hough_Circles
				 BufferedImage imgout4 = new BufferedImage(width,height,BufferedImage.TYPE_BYTE_GRAY); //changed
				 WritableRaster raster4 = imgout4.getRaster();
				 int val = 0;
				 for(int i =1; i<width; i++){
					 for(int j = 1; j<height; j++){
						 int val_c = raster3.getSample(i, j, 0);
						 
						    int radius =30;
 						    if (val_c == 1){
						    	for(int theta = 0;theta<=360 ; theta++){
						    		Double X1 =Math.cos(theta*3.14/180) * radius + i;
						    		int X = X1.intValue();
						    		Double Y1 =Math.sin(theta*3.14/180) * radius + j;
						    		int Y = Y1.intValue();
						    		if(X>width-1){continue;}
						    		if(X<0){continue;}
						    		if(Y>height-1){continue;}
						    		if(Y<0){continue;}
						    		 val = raster4.getSample(X, Y, 0);
						    		val = val+3;
						    		if(val>255)val = 255;
						    		raster4.setSample(X, Y, 0, val);
					    			 						    	
						    	}
						    }
 						  
 								 }
					 }
				 
				 //Thresholding to find the center
				 BufferedImage imgout5 = new BufferedImage(width,height,BufferedImage.TYPE_BYTE_BINARY); //changed
				 WritableRaster raster5 = imgout5.getRaster();
				 for(int i =1; i<width-1; i++){
					 for(int j = 1; j<height-1; j++){
						 int val_t = raster4.getSample(i, j, 0);
						 if(val_t> 150){
				            	raster5.setSample(i, j, 0, 1); //0 is band 1 is white
				            }else{
				            	raster5.setSample(i, j, 0, 0);
				         	}
						 
						
					 
				 }
		}
				 
				 //morphological operation dilation
				 BufferedImage imgout6 = new BufferedImage(width,height,BufferedImage.TYPE_BYTE_BINARY); //changed
				 WritableRaster raster6 = imgout6.getRaster();
				 for(int i =1; i<width-1; i++){
					 for(int j = 1; j<height-1; j++){
						 int val_d = raster5.getSample(i, j, 0); //0 is band here, this is a pixel
						 
						 if(val_d == 0){
							 for(int in = -1; in <=1; in++){ // Check thr neighbour
								 
								 for(int jn = -1; jn <=1; jn++){
									   int valn = raster5.getSample(i+in, j+jn, 0);
									   if(valn == 1){raster6.setSample(i, j, 0, 1);
								   jn = 30;
									   in = 30;}
									  		   
								   
								 	}//end of second loop
							 }//end of secod loop
						 } //end of if
						 if(val_d > 5){raster6.setSample(i, j, 0, 1);}
						
					 }
					 }
				 BufferedImage imgout7 = new BufferedImage(width,height,BufferedImage.TYPE_BYTE_BINARY); //changed
				 WritableRaster raster7 = imgout7.getRaster();
				 for(int i =1; i<width-1; i++){
					 for(int j = 1; j<height-1; j++){
						 int val_c = raster7.getSample(i,j,0);
						 if (val_c == 1){
						
						 }
						 
					 }
					 }
				 
				 
				 

//***************************************write image***************************************					 
					f = new File("Lymphocytes8.jpg"); //output file path
				    ImageIO. write(imgout6, "jpg", f) ;
				    
				    
				 

		}catch (Exception e) {}
	}//End of method
		
	
		   static public void main(String args[]) throws Exception {
			   RGBtoHSB();
			   
			   }
		   }
		
