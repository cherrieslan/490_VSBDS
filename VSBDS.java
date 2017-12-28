// ----------------------------------------------------------------------------------
// CSS 490 Multimedia Data Processing
// Project 4: VSBDS.java
// Lan Yang
// Created on: 06/01/2017
// Last update: 06/08/2017
// ----------------------------------------------------------------------------------

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Vector;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.Collection; 
import java.util.List; 
import java.util.Set;
import java.util.TreeSet;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.*;
  
import java.net.URL;
import java.net.MalformedURLException;


public class VSBDS extends JFrame
{   
    
    private JLabel photographLabel = new JLabel();  //container to hold a large 
    private JLabel picLabel;
    
    private JButton [] button; //creates an array of JButtons
    private int [] buttonOrder ; //creates an array to keep up with the image order
    private double [] imageSize ; //keeps up with the image sizes

    private GridLayout gridLayout1;
    private GridLayout gridLayout2;
    private GridLayout gridLayout3;
    private GridLayout gridLayout4;
    
    private JPanel panelBottom1;
    private JPanel panelBottom2;
    private JPanel panelTop;
    private JPanel buttonPanel;
    private JPanel eachImage;
    
    private JPanel videoPanel;
    
    private JCheckBox relevanceFeedback;
    private JLabel [] select;    // creates an array of JCheckBoxs

    private double [][] intensityMatrix = new double [4001][26];
    private Vector<Integer> Ce = new Vector<Integer>();
    private Vector<Integer> Fs = new Vector<Integer>(); 
 
    int picNo = 0;
    int imageCount = 1; //keeps up with the number of images displayed since the first page.
    int pageNo = 1;
    int [] shots;
    int frameNumberForPlayer = 0;
    boolean choose = false;
    int frameNum = 0;
    String pathToFrames = "";
    
    public static void main(String args[]) 
    {
        SwingUtilities.invokeLater(new Runnable() 
        {
            public void run() 
            {
                VSBDS app = new VSBDS();
                app.setVisible(true);
            }
        });
    }

    public VSBDS() 
    { 
        //The following lines set up the interface including the layout of the buttons and JPanels.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Icon Demo: Please Select an Image");        
        panelBottom1 = new JPanel();
        panelBottom2 = new JPanel();
        panelTop = new JPanel();
        buttonPanel = new JPanel();
        gridLayout1 = new GridLayout(5, 6, 5, 5);
        gridLayout2 = new GridLayout(2, 1, 5, 5);
        gridLayout3 = new GridLayout(1, 2, 5, 5);
        gridLayout4 = new GridLayout(2, 1, 5, 5);
        
        setLayout(gridLayout3);
        panelBottom1.setLayout(gridLayout1);
        panelBottom2.setLayout(gridLayout1);
        panelTop.setLayout(gridLayout2);
        
        add(panelTop);
        add(panelBottom1);
        photographLabel.setVerticalTextPosition(JLabel.BOTTOM);
        photographLabel.setHorizontalTextPosition(JLabel.CENTER);
        photographLabel.setHorizontalAlignment(JLabel.CENTER);
        photographLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        
        buttonPanel.setLayout(gridLayout4);
        
        panelTop.add(photographLabel);
        panelTop.add(buttonPanel);
        add(panelBottom1);

        JButton exit = new JButton("Exit");
        JButton playButton = new JButton("PLAY");

        exit.setFont(new Font("Arial", Font.PLAIN, 40));
        playButton.setFont(new Font("Arial", Font.PLAIN, 40));
        
        buttonPanel.add(playButton);
        buttonPanel.add(exit);
        exit.addActionListener(new exitHandler());
        playButton.addActionListener(new playButtonHandler());
        
        setSize(1100, 750);
        // this centers the frame on the screen
        setLocationRelativeTo(null);

        readIntensityFile();
        double[] SD = SD(intensityMatrix);
        Comparison(SD);
     
                
        buttonOrder = new int [shots.length]; //creates an array to keep up with the image order
        imageSize = new double [shots.length]; //keeps up with the image sizes 
        button = new JButton[shots.length];


        // This for loop goes through the images in the database and stores them as icons and adds
        // the images to JButtons and then to the JButton array
        for (int i = 0; i < shots.length; i++) 
        {
            ImageIcon icon; 
            frameNum = shots[i];
            File framesPath = new File("Frames"); 
            pathToFrames = framesPath.getAbsolutePath(); 
            //System.out.println(absPath);
            String f = "file:///" + pathToFrames + "\\" + frameNum + ".jpg"; 
             URL frameURL = null;

             try 
             {
                 frameURL = new URL(f);
             }
            catch (MalformedURLException ex)
            {
                System.out.println("WRONG VIDEO PATH");
            }
            
            icon = new ImageIcon(frameURL);
            Image img = icon.getImage() ;  
            Image newimg = img.getScaledInstance( icon.getIconWidth()/4, icon.getIconHeight()/4,  java.awt.Image.SCALE_DEFAULT ) ;  
            ImageIcon icon2 = new ImageIcon( newimg );
            String name = Integer.toString(i);
            if(icon != null)
            {
                button[i] = new JButton(icon2);
                button[i].addActionListener(new IconButtonHandler(i, icon));
                buttonOrder[i] = i;
            }
        }
     
        try
        {
            PrintWriter pw = new PrintWriter("Ce.txt");  // create a new text file
            for(int i = 0; i < Ce.size(); i++)
            {
                pw.print(Ce.get(i));
                pw.println();
            }
            pw.close();
        }
        catch(FileNotFoundException EE)
        {
            System.out.println("The file intensity.txt cannot be found!!");
        } 
         
        
        try
        {
            PrintWriter pw = new PrintWriter("Fs.txt");  // create a new text file
            for(int i = 0; i < Fs.size(); i++)
            {
                pw.print(Fs.get(i));
                pw.println();
            }
            pw.close();
        }
        catch(FileNotFoundException EE)
        {
            System.out.println("The file intensity.txt cannot be found!!");
        }

        //getImageSize(imageSize);
        displayFirstPage();
    }
    
    /*This method opens the intensity text file containing the intensity matrix with the histogram bin values for each image.
     * The contents of the matrix are processed and stored in a two dimensional array called intensityMatrix.
    */
    public void readIntensityFile()
    {
        StringTokenizer token;
        Scanner read;
        int intensityBin = 0;
        String line = "";
        int lineNumber = 0;
        try
        {
            read =new Scanner(new File ("intensity.txt"));   // read intensity.txt
       
            for(lineNumber = 0; lineNumber < 4000; lineNumber++) // write intensity into matrix
            {
                for(intensityBin = 0; intensityBin < 25; intensityBin++)
                {
                    intensityMatrix[lineNumber][intensityBin] = read.nextInt();
                }
            }
        }
        catch(FileNotFoundException EE)
        {
            System.out.println("The file intensity.txt does not exist");
        }
    }
    
   
  
    // Calculate frame-to-frame different
    private double[] SD(double[][] relevantMatrix)
    {
        double difference;
        double sum;
        double[] SD = new double[3999];
        for(int i = 0; i < 3999; i++)
        {
            sum = 0;
            for(int j = 0; j < 25; j++)
            {
                difference = Math.abs(relevantMatrix[i][j] - relevantMatrix[i+1][j]);
                sum += difference;
            }
            SD[i] = sum;
        }
        
        return SD;
    }
    
    // Calculate average values of each bin in matrix
    private double mean(double[] SD)
    {
        double sum = 0;
        double mean = 0;

        for(int i = 0; i < SD.length; i++)
        {
            sum += SD[i];
        }
        mean = sum / SD.length;
        
        return mean;
    }
    
    // Calculate standard deviation
    private double standardDeviation(double[] SD)
    {
        double sum = 0;
        double std = 0;
        double mean = mean(SD);
        
        for(int i = 0; i < SD.length; i++)
        {
            sum += Math.pow((SD[i] - mean),2);
        }
        std = Math.sqrt(sum / (SD.length-1));

        return std;
    }
    
    private double Tb (double[] SD)
    {
        double mean = mean(SD);
        double std = standardDeviation(SD);
        double Tb = mean + std*11;
        
        return Tb;
    }

    private double Ts (double[] SD)
    {
        double Ts = mean(SD) * 2;
        return Ts;
    }
    
    private void Comparison (double[] SD)
    {
        int sum = 0;
        for(int i = 0; i < 3998; i++)
        {
            
            if(SD[i] >= Tb(SD))
            {
                Ce.add(i+1000);
            }
            else if(SD[i] >= Ts(SD))
            {
                sum += SD[i];
            
                if(SD[i+1] < Ts(SD) && SD[i+2] < Ts(SD))
                {
                    if(sum >= Tb(SD))
                    {
                        Fs.add(i+1001);
                        sum = 0;
                    }
                }
            }
        }
        
        shots = new int[Ce.size() + Fs.size()];
        for(int i = 0; i < shots.length; i++)
        {
            if(i < Ce.size())
            {
                shots[i] = Ce.get(i);
            }
            else
            {
                shots[i] = Fs.get(i-Ce.size());
            }
        }
        
        // sort the shots array
        Arrays.sort(shots);

    }

    
    
    /*This method displays the first twenty images in the panelBottom.  The for loop starts at number one and gets the image
     * number stored in the buttonOrder array and assigns the value to imageButNo.  The button associated with the image is 
     * then added to panelBottom1.  The for loop continues this process until twenty images are displayed in the panelBottom1
    */
    private void displayFirstPage()
    {
        int imageButNo = 0;
        panelBottom1.removeAll(); 
        for(int i = 0; i < 30; i++)
        {
            imageButNo = buttonOrder[i];
            
            eachImage = new JPanel(new BorderLayout());
            eachImage.add(button[imageButNo], BorderLayout.CENTER);
            panelBottom1.add(eachImage);
            picLabel = new JLabel();
            picLabel.setText(""+shots[i]);
            eachImage.add(picLabel, BorderLayout.SOUTH);
            panelBottom1.add(eachImage);
            //panelBottom1.add(button[imageButNo]);
            
            //System.out.println(picLabel.getText());
            imageCount ++;
        }
        panelBottom1.revalidate();  
        panelBottom1.repaint();
    }
    
    // Play the shot was clicked
    private class playButtonHandler implements ActionListener
    {
        public void actionPerformed( ActionEvent e)
        {  
            if(choose == false)
                return;
            
            double second =  frameNumberForPlayer/25; // frame divided by 25 
            int minute = (int)second / 60;  
            second = second - (60 * minute); 
            
                
            File file = new File("newVideo");  
            String absPath = file.getAbsolutePath(); 
            //System.out.println(absPath);
            String path = "file:///" + absPath + "\\video.avi"; 
            String command = "\\ffplay -i video.mpg -ss 00:" + minute + ":" + second + " -x 352 -y 288";
             
                
            try
            {
                Runtime runtime = Runtime.getRuntime(); 
                Process p = runtime.exec(absPath+command, null, new File(absPath));  
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream())); //getErrorStream()
                while((reader.readLine()) != null)
                {}
                 
            }
            catch(Exception ex)
            {
                System.out.println("player handler is not working");
            }
             
            
        }
    }
    
    
    
    /*This class implements an ActionListener for each iconButton.  When an icon button is clicked, the image on the 
     * the button is added to the photographLabel and the picNo is set to the image number selected and being displayed.
    */ 
    private class IconButtonHandler implements ActionListener
    {
        int pNo = 0;
        ImageIcon iconUsed;
      
        IconButtonHandler(int i, ImageIcon j)
        {
            pNo = i;
            iconUsed = j;  //sets the icon to the one used in the buttons
        }
      
        public void actionPerformed( ActionEvent e)
        {
            choose = true;
            photographLabel.setIcon(iconUsed);
            picNo = pNo;
            frameNumberForPlayer = shots[pNo];
            //System.out.println("frame for player: "+frameNumberForPlayer);
            photographLabel.setText(""+frameNumberForPlayer); 
        }
    }

    // Listen exit button, if is is clicked, quit the program.
    private class exitHandler implements ActionListener
    {
        public void actionPerformed( ActionEvent e)
        {
            System.exit(0);
        }
    }
}
