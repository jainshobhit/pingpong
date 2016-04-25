import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPanel;
import javax.swing.Timer;

import java.util.*;

public class twoplayer1 extends JPanel implements ActionListener, KeyListener, Runnable{

    private DatagramSocket socket;
    private boolean running;
    private InetSocketAddress address;
    private boolean startgameforeign=false;
    public boolean startgamelocal=false;
    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean wPressed = false;
    private boolean sPressed = false;

    private int ballX = 250;
    private int ballY = 250;
    private int diameter = 20;
    private int ballDeltaX = -3;
    private int ballDeltaY = 3;
    private int initialballDeltaX = ballDeltaX;
    private int initialballDeltaY = ballDeltaY;

    private int playerOneX = 25;                                //this represents the left coordinate of the bat
    private int playerOneY = 250;                               // this represents the topmost coordinated of bat
    private int playerOneWidth = 10;
    private int playerOneHeight = 50;

    private int playerTwoX = 465;
    private int playerTwoY = 250;
    private int playerTwoWidth = 10;
    private int playerTwoHeight = 50;

    private int paddleSpeed = 5;        //represents speed of the bat

    private int playerOneScore = 0;
    private int playerTwoScore = 0;

    int counter1=0;
    int counter2=0;
    int deviation=0;
    


    //construct a PongPanel
    public twoplayer1(){
        setBackground(Color.BLACK);

        //listen to key presses
        setFocusable(true);
        addKeyListener(this);

        //call step() 20 fps
        Timer timer = new Timer(1000/20, this);
        timer.start();
    }

    public void bind(int port) throws SocketException
    {
        socket = new DatagramSocket(port);
    }
    
    public void start()
    {
        Thread thread = new Thread(this);
        thread.start();
    }
    
    public void stop()
    {
        running = false;
        socket.close();
    }

    public void getaddress(InetSocketAddress addresspassed)
    {
        address=addresspassed;
    }

    
    public void sendTo(InetSocketAddress address1, String msg) throws IOException
    {
        byte[] buffer = msg.getBytes();
        
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        packet.setSocketAddress(address1);
        
        socket.send(packet);
    }


    public void actionPerformed(ActionEvent e){
        if(startgamelocal)
        {
            //System.out.println("game started");
            step();
        }
    }

    public boolean countdashes(String s)
    {
        int c=0;
        for(int i=0;i<s.length();i++)
        {
            if(s.charAt(i)=='_')
            {
                c++;
            }
        }
        return c==3;
    }

    public boolean countdashesp(String s)
    {
        int c=0;
        for(int i=0;i<s.length();i++)
        {
            if(s.charAt(i)=='_')
            {
                c++;
            }
        }
        return c==1;
    }
    @Override
    public void run()
    {
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        
        running = true;
        while(running)
        {
            try
            {
                socket.receive(packet);
                
                String msg = new String(buffer, 0, packet.getLength());
                //System.out.println(msg);// iski jagah positions yahan banani hain
                if(countdashesp(msg))
                {
                String coordinates[]=msg.split("_");
                String sxcoordinates=coordinates[0];
                String sycoordinates=coordinates[1];
                playerTwoX=Integer.parseInt(sxcoordinates);
                playerTwoY=Integer.parseInt(sycoordinates);
                playerTwoX+=440;    
                }
                else if(msg.equals("play"))
                {
                    startgameforeign=true;
                    System.out.println("play recieved here");
                }
                if(countdashes(msg)==true)
                //else
                {
                String coordinates[]=msg.split("_");
                String sxcoordinates=coordinates[0];
                String sycoordinates=coordinates[1];
                playerTwoX=Integer.parseInt(sxcoordinates);
                playerTwoY=Integer.parseInt(sycoordinates);
                playerTwoX+=440; 
                String balldeltax=coordinates[2];
                String balldeltay=coordinates[3];
                ballDeltaX=-1*Integer.parseInt(balldeltax);
                ballDeltaY=Integer.parseInt(balldeltay);
                System.out.println("ball collision msg recieved");
                }
                //repaint();

            } 
            catch (IOException e)
            {
                break;
            }
        }
    }
    public void step(){

        //where will the ball be after it moves?
        float nextBallLeft = ballX + ballDeltaX;
        float nextBallRight = ballX + diameter + ballDeltaX;
        float nextBallTop = ballY + ballDeltaY;
        float nextBallBottom = ballY + diameter + ballDeltaY;


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //move player 1
        int move1=0;
        move1=player1_move();
        
        int playerOneRight = playerOneX + playerOneWidth;
        int playerOneTop = playerOneY;
        int playerOneBottom = playerOneY + playerOneHeight;

        String positionmsg="";
        positionmsg=positionmsg+playerOneX;
        positionmsg=positionmsg+"_"+playerOneY;
        try
        {
        sendTo(address,positionmsg);    
        }
        catch(IOException e)
        {
            System.out.println("IOException caught");
        }
        
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //move player 2
        /*String playertwomsg;
        String coordinates=playertwomsg.split("-");
        String sxcoordinates=coordinates[0];
        String sycoordinates=coordinates[1];
        playerTwoX=Integer.parseInt(sxcoordinates);
        playerTwoY=Integer.parseInt(sycoordinates);
        */
        int playerTwoLeft = playerTwoX;
        int playerTwoTop = playerTwoY;
        int playerTwoBottom = playerTwoY + playerTwoHeight;
        
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////       
        //ball bounces off top and bottom of screen
        if (nextBallTop < 0 || nextBallBottom > getHeight()) {
          //if (nextBallBottom>getHeight()){
            ballDeltaY *= -1;
        }

                //will the ball go off the left side?
        if (nextBallLeft < playerOneRight) { 
            //is it going to miss the paddle?
            if ( ballY > playerOneBottom || ballY < playerOneTop) {

                playerTwoScore ++;

                ballX = 250;
                ballY = 250;
                ballDeltaX = initialballDeltaX;
                ballDeltaY = initialballDeltaY;
                
            }
            else {
                System.out.println("collision detected");
                ballDeltaX *= -1;
                String positionballmsg="";
                positionballmsg=positionballmsg+playerOneX+"_"+playerOneY+"_"+ballDeltaX+"_"+ballDeltaY;
                try
                {
                    System.out.println("entered try");
                    sendTo(address,positionballmsg);    
                }
                catch(IOException e)
                {
                    System.out.println("IOException caught");
                }
            }
        }

        /*else{
        try
        {
        sendTo(address,positionmsg);    
        }
        catch(IOException e)
        {
            System.out.println("IOException caught");
        }
        }
        */
        //will the ball go off the right side?
        /*if (nextBallRight > playerTwoLeft) {
            //is it going to miss the paddle?
            if (ballY > playerTwoBottom || ballY < playerTwoTop) {
                playerOneScore ++;
                counter1=counter2;
                deviation=0;
                
                ballX = 250;
                ballY = 250;
                ballDeltaX = initialballDeltaX;
                ballDeltaY = initialballDeltaY;
                

            }
            else {
                counter1+=1;
                ballDeltaX *= -1;
            }
            
        }*/
        
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //move the ball
        ballX += ballDeltaX;
        ballY += ballDeltaY;

        //stuff has moved, tell this JPanel to repaint itself
        repaint();
    }

    public int player1_move()
    {
        int bool=0;
        if (upPressed) {  
            bool=1;                                       //means positive direction is below. because on up press, y value is being reduced
            if (playerOneY-paddleSpeed > 0) {
                playerOneY -= paddleSpeed;
            }
        }
        if (downPressed) {
            if (playerOneY + paddleSpeed + playerOneHeight < getHeight()) {
                playerOneY += paddleSpeed;
            }
        }
        return bool;
    }
    

    //paint the game screen
    public void paintComponent(Graphics g){

        super.paintComponent(g);
        g.setColor(Color.WHITE);

        int playerOneRight = playerOneX + playerOneWidth;
        int playerTwoLeft =  playerTwoX;
        //int playerThreeTop = playerThreeY;
        //int playerFourBottom = playerFourY + playerFourHeight;

        //draw dashed line down center
        //for (int lineY = 0; lineY < getHeight(); lineY += 50) {
          //  g.drawLine(250, lineY, 250, lineY+25);
        //}

        //draw "goal lines" on each side
        g.drawLine(playerOneRight, 0, playerOneRight, getHeight());
        g.drawLine(playerTwoLeft, 0, playerTwoLeft, getHeight());
        //g.drawLine(0, playerThreeTop, getWidth(), playerThreeTop);
        //g.drawLine(0, playerFourBottom, getWidth(),playerFourBottom);

        //draw the scores
        g.setFont(new Font(Font.DIALOG, Font.BOLD, 36));
        g.drawString(String.valueOf(playerOneScore), 100, 100);
        g.drawString(String.valueOf(playerTwoScore), 400, 100);

        //draw the ball
        g.fillOval(ballX, ballY, diameter, diameter);

        //draw the paddles
        g.fillRect(playerOneX, playerOneY, playerOneWidth, playerOneHeight);
        g.fillRect(playerTwoX, playerTwoY, playerTwoWidth, playerTwoHeight);
        //g.fillRect(playerThreeX, playerThreeY, playerThreeWidth, playerThreeHeight);
        //g.fillRect(playerFourX, playerFourY, playerFourWidth, playerFourHeight);
    }

    public void keyTyped(KeyEvent e) {}



    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            upPressed = true;
        }
        else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            downPressed = true;
        }
        else if (e.getKeyCode() == KeyEvent.VK_W) {
            wPressed = true;
        }
        else if (e.getKeyCode() == KeyEvent.VK_S) {
            sPressed = true;
        }
    }


    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            upPressed = false;
        }
        else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            downPressed = false;
        }
        else if (e.getKeyCode() == KeyEvent.VK_W) {
            wPressed = false;
        }
        else if (e.getKeyCode() == KeyEvent.VK_S) {
            sPressed = false;
        }
    }

}
