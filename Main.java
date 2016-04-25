import java.awt.BorderLayout;

import javax.swing.JFrame;
import java.awt.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Scanner;

public class Main{

    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);
		
		//System.out.print("Name : ");
		//String name = scanner.nextLine();
		
		System.out.print("Source Port : ");
		int sourcePort=1234;
		//int sourcePort = Integer.parseInt(scanner.nextLine());
		
		System.out.print("Destination IP : ");
		//String destinationIP = "192.168.146.51";
		String destinationIP = "10.192.51.85";
		//String destinationIP = scanner.nextLine();
		
		System.out.print("Destination Port : ");
		int destinationPort=4567;
		//int destinationPort = Integer.parseInt(scanner.nextLine());
		//////////////////////////////////////here we will ask which side the player is playing. Based on that, different channel will be called.
		/*Channel channel = new Channel();
		channel.bind(sourcePort);
		channel.start(); // Start Receive
		*/
		System.out.println("Started.");
		String playmsg = scanner.nextLine();
		InetSocketAddress address = new InetSocketAddress(destinationIP, destinationPort);
		/////////////////////////////////////////////////////////////////////////////////////////

        JFrame frame = new JFrame("Pong");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        twoplayer1 two = new twoplayer1();
        two.bind(sourcePort);
        two.start();
        two.getaddress(address);
        
        if(playmsg.equals("play"))
        {
        	two.startgamelocal=true;
        	System.out.println(two.startgamelocal);
        	two.sendTo(address, playmsg);
        	System.out.println("Local machine play");	
        }
        
        frame.setResizable(false);
        //frame.pack();
        frame.add(two, BorderLayout.CENTER);
        /*Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = screenSize.height;
		int width = screenSize.width;
        
        frame.setSize(width/4,height/2);
        */
        frame.setSize(600,600);
        frame.setVisible(true);

    }
}