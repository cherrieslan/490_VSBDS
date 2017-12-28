import java.lang.*;
import java.awt.image.BufferedImage;
import java.lang.Object.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.File;
import java.util.concurrent.ExecutionException;
import java.lang.InterruptedException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;



public class readVideo
{
    int imageCount = 1000;
    int intensityBins [] = new int [26];
    int intensityMatrix [][] = new int[4001][26];
    
    File file = null;
    BufferedImage image = null;
    
    public static void main(String[] args)
    {
        new readVideo();
    }
    
    public readVideo()
    {
        //ProcessBuilder builder = new ProcessBuilder("ffmpeg.exe", "-i", "myfile.avi", "-f", "image2", "image-%05d.jpg");
        try
        {
            Runtime runtime = Runtime.getRuntime();
            Process p = runtime.exec("cmd /c start newVideo/ffmpeg.exe -i newVideo/video.avi -f image2 Frames/%01d.jpg");

            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while((reader.readLine()) != null){}
            p.waitFor();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        
        readImage();
    }
    
    public void readImage()
    {
        while(imageCount < 5000)
        {
            try
            {
                // the line that reads the image file
                file = new File("Frames/" + imageCount + ".jpg");
                image = ImageIO.read(file);
                
                //BufferedImage image = ImageIO.read(getClass().getResourceAsStream("Frames/" + imageCount + ".jpg"));
        
                int width = image.getWidth();   // get the image width
                int height = image.getHeight(); // get the image height
        
                getIntensity(image, height, width); // get image's intensity
       
                imageCount++;   // update image number
            } 
            catch (IOException e)
            {
                System.out.println("Error occurred when reading the file.");
            }
        }
    
        
        writeIntensity();   // write image's intensity to intensity.txt
    }
    
    public void getIntensity(BufferedImage image, int height, int width)
    {
        for(int i = 0; i < height; i++)
        {
            for(int j = 0; j < width; j++)
            {
                int pixel = image.getRGB(j, i); // get image's RGB
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = pixel & 0xff;
                // calculate teh intensity
                double Intensity = 0.299*red + 0.587*green + 0.114*blue;
  
                int binIndex = 0;
                // set intensities to different bins
                if(Intensity < 240)
                {
                    binIndex = (int)Intensity/10 + 1;
                }
                else
                {
                    binIndex = 25;
                }
            
                intensityBins[binIndex]++;  // update the content number of bin in array
                intensityMatrix [imageCount-999][binIndex]++;   // update the content number of bin in Matrix
            }
        }
    }
  
    public void writeIntensity()
    {
        try
        {
            PrintWriter pw = new PrintWriter("intensity.txt");  // create a new text file
            for(int i = 1; i < 4001; i++)
            {
                for(int j = 1; j < 26; j++)
                {
                    pw.print(intensityMatrix[i][j] + " ");  // write intensity into file
                }
                pw.println();   // next line
            }
            pw.close();
        }
        catch(FileNotFoundException EE)
        {
            System.out.println("The file intensity.txt cannot be found!!");
        }
    }
  
}