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
    public int looptimer=0;
    public int waittimer=-1;
    private DatagramSocket socket;
    private boolean running;
    private InetSocketAddress address;
    private InetSocketAddress address2;
    public int destinationport1;
    public int destinationport2;
    public int sourceport;
    private boolean startgameforeign=false;
    public boolean startgamelocal=false;
    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean wPressed = false;
    private boolean sPressed = false;
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    public String location="";
    private int ballX = 240;
    private int ballY = 240;
    private int diameter = 20;
    private int ballDeltaX = -5;
    private int ballDeltaY = -5;
    private int initialballDeltaX = ballDeltaX;
    private int initialballDeltaY = ballDeltaY;

    private int localplayerX = 25;                              //this represents the left coordinate of the bat
    private int localplayerY = 250;                               // this represents the topmost coordinated of bat
    private int localplayerWidth = 10;
    private int localplayerHeight = 50;

    private int lvX = 0;                               //this represents the left coordinate of the bat
    private int lvY = 0;                               // this represents the topmost coordinated of bat
    private int lvWidth = 10;
    private int lvHeight = 50;

    private int dhX = 0;                               //this represents the left coordinate of the bat
    private int dhY = 0;                               // this represents the topmost coordinated of bat
    private int dhWidth = 50;
    private int dhHeight = 10;

    private int rvX = 0;                               //this represents the left coordinate of the bat
    private int rvY = 0;                               // this represents the topmost coordinated of bat
    private int rvWidth = 10;
    private int rvHeight = 50;

    private int uhX = 0;                               //this represents the left coordinate of the bat
    private int uhY = 0;                               // this represents the topmost coordinated of bat
    private int uhWidth = 50;
    private int uhHeight = 10;

    private int paddleSpeed = 8;        //represents speed of the bat

    private int playerlvScore = 0;
    private int playerdhScore = 0;
    private int playerrvScore = 0;
    private int playeruhScore = 0;

    private boolean islv=false;
    private boolean isdh=false;
    private boolean isrv=false;
    private boolean isuh=false;

    public String positionmsg="";

    int deviation=0;
    


    //construct a PongPanel
    public twoplayer1(String a){
        location=a;
        setBackground(Color.BLACK);
        if(location.equals("lv")){
            localplayerX = 15;                               //this represents the left coordinate of the bat
            localplayerY = 250;                               // this represents the topmost coordinated of bat
            localplayerWidth = 10;
            localplayerHeight = 50;
        }
        else if(location.equals("dh")){
            localplayerX = 250;                               //this represents the left coordinate of the bat
            localplayerY = 475;                               // this represents the topmost coordinated of bat
            localplayerWidth = 50;
            localplayerHeight = 10;
        }
        else if(location.equals("rv")){
            localplayerX = 475;                               //this represents the left coordinate of the bat
            localplayerY = 250;                               // this represents the topmost coordinated of bat
            localplayerWidth = 10;
            localplayerHeight = 50;
        }
        else if(location.equals("uh")){
            localplayerX = 250;                               //this represents the left coordinate of the bat
            localplayerY = 15;                               // this represents the topmost coordinated of bat
            localplayerWidth = 50;
            localplayerHeight = 10;
        }
        //System.out.println(localplayerX+""+localplayerY+""+localplayerWidth+""+localplayerHeight);
        //listen to key presses
        setFocusable(true);
        addKeyListener(this);

        //call step() 20 fps
        Timer timer = new Timer(1000/20, this);
        timer.start();
    }

    public void bind(int port) throws SocketException
    {
        sourceport=port;
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

    public void getaddress(InetSocketAddress addresspassed, InetSocketAddress addresspassed2)
    {
        address=addresspassed;
        address2=addresspassed2;   
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

    public int countdashes(String s)
    {
        int c=0;
        for(int i=0;i<s.length();i++)
        {
            if(s.charAt(i)=='_')
            {
                c++;
            }
        }
        return c;
    }
    public void getdestinationports(int a, int b)
    {
        destinationport1=a;
        destinationport2=b;
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
                System.out.println(msg);// iski jagah positions yahan banani hain
                if(countdashes(msg)==2)
                {
                    String coordinates[]=msg.split("_");
                    if(coordinates[0].equals("lv"))
                    {
                        islv=true;
                        String tempxcoordinates=coordinates[1];
                        String tempycoordinates=coordinates[2];
                        lvX=Integer.parseInt(tempxcoordinates);
                        lvY=Integer.parseInt(tempycoordinates);
                        
                    }
                    else if(coordinates[0].equals("dh"))
                    {
                        isdh=true;
                        String tempxcoordinates=coordinates[1];
                        String tempycoordinates=coordinates[2];
                        dhX=Integer.parseInt(tempxcoordinates);
                        dhY=Integer.parseInt(tempycoordinates);  
                    }   
                    else if(coordinates[0].equals("rv"))
                    {
                        isrv=true;
                        String tempxcoordinates=coordinates[1];
                        String tempycoordinates=coordinates[2];
                        rvX=Integer.parseInt(tempxcoordinates);
                        rvY=Integer.parseInt(tempycoordinates);  
                    }
                    else if(coordinates[0].equals("uh"))
                    {
                        isuh=true;
                        String tempxcoordinates=coordinates[1];
                        String tempycoordinates=coordinates[2];
                        uhX=Integer.parseInt(tempxcoordinates);
                        uhY=Integer.parseInt(tempycoordinates);  
                    } 
                }
                else if(msg.equals("play"))
                {
                    startgameforeign=true;
                    System.out.println("play recieved here");
                }
                if(countdashes(msg)==4)
                //else
                {
                    String coordinates[]=msg.split("_");
                    
                    if(coordinates[0].equals("lv"))
                    {
                        String tempxcoordinates=coordinates[1];
                        String tempycoordinates=coordinates[2];
                        lvX=Integer.parseInt(tempxcoordinates);
                        lvY=Integer.parseInt(tempycoordinates);
                        String balldeltax=coordinates[3];
                        String balldeltay=coordinates[4];
                        ballDeltaX=Integer.parseInt(balldeltax);
                        ballDeltaY=Integer.parseInt(balldeltay);
                        System.out.println("ball collision msg recieved");
                
                    }
                    if(coordinates[0].equals("dh"))
                    {
                        String tempxcoordinates=coordinates[1];
                        String tempycoordinates=coordinates[2];
                        dhX=Integer.parseInt(tempxcoordinates);
                        dhY=Integer.parseInt(tempycoordinates);
                        //playerThreeY=Integer.parseInt(sycoordinates2);
                        //440 change hoga
                        //playerThreeX+=440;
                        String balldeltax=coordinates[3];
                        String balldeltay=coordinates[4];
                        ballDeltaX=Integer.parseInt(balldeltay);
                        ballDeltaY=Integer.parseInt(balldeltax);
                        System.out.println("ball collision msg recieved");
                    } 
                    if(coordinates[0].equals("rv"))
                    {
                        String tempxcoordinates=coordinates[1];
                        String tempycoordinates=coordinates[2];
                        rvX=Integer.parseInt(tempxcoordinates);
                        rvY=Integer.parseInt(tempycoordinates);
                        String balldeltax=coordinates[3];
                        String balldeltay=coordinates[4];
                        ballDeltaX=Integer.parseInt(balldeltax);
                        ballDeltaY=Integer.parseInt(balldeltay);
                        System.out.println("ball collision msg recieved");
                
                    }
                    if(coordinates[0].equals("uh"))
                    {
                        String tempxcoordinates=coordinates[1];
                        String tempycoordinates=coordinates[2];
                        uhX=Integer.parseInt(tempxcoordinates);
                        uhY=Integer.parseInt(tempycoordinates);
                        //playerThreeY=Integer.parseInt(sycoordinates2);
                        //440 change hoga
                        //playerThreeX+=440;
                        String balldeltax=coordinates[3];
                        String balldeltay=coordinates[4];
                        ballDeltaX=Integer.parseInt(balldeltay);
                        ballDeltaY=Integer.parseInt(balldeltax);
                        System.out.println("ball collision msg recieved");
                    }
                }
                if(msg.equals("0"))
                {
                    ballDeltaX=0;
                    ballDeltaY=0;
                    ballX=240;
                    ballY=240;
                }
                else if(msg.equals("1"))
                {
                    ballDeltaX=initialballDeltaX;
                    ballDeltaY=initialballDeltaY;
                }

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
        if(waittimer>0)
        {
            waittimer--;
        }else if(waittimer==0){
            ballDeltaX=initialballDeltaX;
            ballDeltaY=initialballDeltaY;
            waittimer=-1;
            try{
                sendTo(address,"1");
                sendTo(address2,"1");
            }
            catch(IOException e)
            {
                System.out.println("IOException caught");
            }
        }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //move player 1
        //int move1=0;
        //System.out.println(ballX+" "+ballY+" "+looptimer++);
        if(location.equals("lv"))
        {
            lv(nextBallLeft, nextBallRight, nextBallTop, nextBallBottom);
        }
        else if(location.equals("dh"))
        {
            dh(nextBallLeft, nextBallRight, nextBallTop, nextBallBottom);
        }
        else if(location.equals("rv"))
        {
            rv(nextBallLeft, nextBallRight, nextBallTop, nextBallBottom);
        }
        else if(location.equals("uh"))
        {
            uh(nextBallLeft, nextBallRight, nextBallTop, nextBallBottom);
        }
        
        if (nextBallTop < 0 || nextBallBottom > getHeight()) {
          //if (nextBallBottom>getHeight()){
            ballDeltaY *= -1;
        }
    
        try
        {
        sendTo(address,positionmsg);
        sendTo(address2,positionmsg);    
        }
        catch(IOException e)
        {
            System.out.println("IOException caught");
        }
        
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        
        /*int playerTwoLeft = playerTwoX;
        int playerTwoTop = playerTwoY;
        int playerTwoBottom = playerTwoY + playerTwoHeight;
        */
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////       
        //ball bounces off top and bottom of screen
        /*if (nextBallTop < 0 || nextBallBottom > getHeight()) {
          //if (nextBallBottom>getHeight()){
            ballDeltaY *= -1;
        }*/

                //will the ball go off the left side?
        
       
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //move the ball
        ballX += ballDeltaX;
        ballY += ballDeltaY;

        //stuff has moved, tell this JPanel to repaint itself
        repaint();
    }

    public void lv(float nextBallLeft,float nextBallRight,float nextBallTop,float nextBallBottom)
    {
        playerlv_move();
            int localplayerRight = localplayerX + localplayerWidth;
            int localplayerTop = localplayerY;
            int localplayerBottom = localplayerY + localplayerHeight;

            if (nextBallLeft < localplayerRight) { 
            //is it going to miss the paddle?
                //here also write the code of corner case
            if ( nextBallBottom > localplayerBottom || nextBallTop < localplayerTop) {

               // playerTwoScore ++;
                try{
                    sendTo(address,"0");
                    sendTo(address2,"0");
                }
                catch(IOException e)
                {
                    System.out.println("IOException caught");
                }
                waittimer=20;

                ballX = 240;
                ballY = 240;
                ballDeltaX = 0;
                ballDeltaY = 0;
                //send a message for telling to increse the score and 
                //also to place the ball again at centre
                
            }
            else {
                System.out.println("collision detected");
                ballDeltaX *= -1;
                String positionballmsg="";
                positionballmsg=positionballmsg+"lv"+"_"+localplayerX+"_"+localplayerY+"_"+ballDeltaX+"_"+ballDeltaY;
                //System.out.println(positionballmsg+" "+looptimer++);
                //System.out.println(ballX+" "+ballY);
                try
                {
                    System.out.println("entered try");
                    sendTo(address,positionballmsg);
                    sendTo(address2,positionballmsg);    
                }
                catch(IOException e)
                {
                    System.out.println("IOException caught");
                }
            }
        
        }
        positionmsg="lv"+"_"+localplayerX+"_"+localplayerY;
        //positionmsg=positionmsg;
        
    }
    public void dh(float nextBallLeft,float nextBallRight,float nextBallTop,float nextBallBottom)
    {
        playerdh_move();
            int localplayerTop = localplayerY;
            int localplayerRight = localplayerX + localplayerWidth;  
            int localplayerLeft = localplayerX ;

            if (nextBallBottom > localplayerTop) { 
            //is it going to miss the paddle?
            if ( nextBallLeft<localplayerLeft|| nextBallRight > localplayerRight) {

               // playerTwoScore ++;
                try{
                    sendTo(address,"0");
                    sendTo(address2,"0");
                }
                catch(IOException e)
                {
                    System.out.println("IOException caught");
                }
                waittimer=20;
                ballX = 240;
                ballY = 240;
                ballDeltaX = 0;
                ballDeltaY = 0;
                //send a message for telling to increse the score and 
                //also to place the ball again at centre
                
            }
            else {
                System.out.println("collision detected");
                ballDeltaY *= -1;
                String positionballmsg="";
                positionballmsg=positionballmsg+"dh"+"_"+localplayerX+"_"+localplayerY+"_"+ballDeltaX+"_"+ballDeltaY;
                try
                {
                    System.out.println("entered try");
                    sendTo(address,positionballmsg);
                    sendTo(address2,positionballmsg);    
                }
                catch(IOException e)
                {
                    System.out.println("IOException caught");
                }
            }
        }

        positionmsg="dh"+"_"+localplayerX+"_"+localplayerY;

    }
    public void rv(float nextBallLeft,float nextBallRight,float nextBallTop,float nextBallBottom)
    {
        playerrv_move();
            int localplayerLeft= localplayerX;
            int localplayerTop = localplayerY;
            int localplayerBottom = localplayerY + localplayerHeight;

            if (nextBallRight > localplayerLeft) { 
            //is it going to miss the paddle?
                //here also write the code of corner case
            if ( nextBallBottom > localplayerBottom || nextBallTop < localplayerTop) {

               // playerTwoScore ++;
                try{
                    sendTo(address,"0");
                    sendTo(address2,"0");
                }
                catch(IOException e)
                {
                    System.out.println("IOException caught");
                }
                waittimer=20;
                ballX = 240;
                ballY = 240;
                ballDeltaX = 0;
                ballDeltaY = 0;
                //send a message for telling to increse the score and 
                //also to place the ball again at centre
                
            }
            else {
                System.out.println("collision detected");
                ballDeltaX *= -1;
                String positionballmsg="";
                positionballmsg=positionballmsg+"rv"+"_"+localplayerX+"_"+localplayerY+"_"+ballDeltaX+"_"+ballDeltaY;
                try
                {
                    System.out.println("entered try");
                    sendTo(address,positionballmsg);
                    sendTo(address2,positionballmsg);    
                }
                catch(IOException e)
                {
                    System.out.println("IOException caught");
                }
            }
        }
        positionmsg="rv"+"_"+localplayerX+"_"+localplayerY;

    }
    public void uh(float nextBallLeft,float nextBallRight,float nextBallTop,float nextBallBottom)
    {
            playeruh_move();
            int localplayerBottom = localplayerY+localplayerHeight;
            int localplayerLeft = localplayerX;  
            int localplayerRight = localplayerX + localplayerWidth;

            if (nextBallTop < localplayerBottom) { 
            //is it going to miss the paddle?
                System.out.println(nextBallTop);
            if ( nextBallLeft<localplayerLeft|| nextBallRight > localplayerRight) {

               // playerTwoScore ++;
                try{
                    sendTo(address,"0");
                    sendTo(address2,"0");
                }
                catch(IOException e)
                {
                    System.out.println("IOException caught");
                }
                waittimer=20;
                ballX = 240;
                ballY = 240;
                ballDeltaX = 0;
                ballDeltaY = 0;
                //send a message for telling to increse the score and 
                //also to place the ball again at centre
                
            }
            else {
                System.out.println("collision detected");
                ballDeltaY *= -1;
                String positionballmsg="";
                positionballmsg=positionballmsg+"uh"+"_"+localplayerX+"_"+localplayerY+"_"+ballDeltaX+"_"+ballDeltaY;
                try
                {
                    System.out.println("entered try");
                    sendTo(address,positionballmsg);
                    sendTo(address2,positionballmsg);    
                }
                catch(IOException e)
                {
                    System.out.println("IOException caught");
                }
            }
        }
        positionmsg="uh"+"_"+localplayerX+"_"+localplayerY;
    }
    public void playerlv_move()
    {
        //int bool=0;
        if (upPressed) {  
            //bool=1;                                       //means positive direction is below. because on up press, y value is being reduced
            if (localplayerY-paddleSpeed > 0) {
                localplayerY -= paddleSpeed;
            }
        }
        if (downPressed) {
            if (localplayerY + paddleSpeed + localplayerHeight < getHeight()) {
                localplayerY += paddleSpeed;
            }
        }
        //return bool;
    }
    
    public void playerrv_move()
    {
        //int bool=0;
        if (upPressed) {  
            //bool=1;                                       //means positive direction is below. because on up press, y value is being reduced
            if (localplayerY-paddleSpeed > 0) {
                localplayerY -= paddleSpeed;
            }
        }
        if (downPressed) {
            if (localplayerY + paddleSpeed + localplayerHeight < getHeight()) {
                localplayerY += paddleSpeed;
            }
        }
        //return bool;
    }
    public void playerdh_move()
    {
        //int bool=0;
        if (leftPressed) {  
            //bool=1;                                       //means positive direction is below. because on up press, y value is being reduced
            if (localplayerX-paddleSpeed > 0) {
                localplayerX -= paddleSpeed;
            }
        }
        if (rightPressed) {
            if (localplayerX + paddleSpeed + localplayerWidth < getHeight()) {
                localplayerX += paddleSpeed;
            }
        }
        //return bool;
    }
    public void playeruh_move()
    {
        //int bool=0;
        if (leftPressed) {  
            //bool=1;                                       //means positive direction is below. because on up press, y value is being reduced
            if (localplayerX-paddleSpeed > 0) {
                localplayerX -= paddleSpeed;
            }
        }
        if (rightPressed) {
            if (localplayerX + paddleSpeed + localplayerWidth < getHeight()) {
                localplayerX += paddleSpeed;
            }
        }
        //return bool;
    }

    //paint the game screen
    public void paintComponent(Graphics g){

        super.paintComponent(g);
        g.setColor(Color.WHITE);

        if(location.equals("lv")){
            g.drawLine(localplayerX+localplayerWidth, 0,localplayerX+localplayerWidth, getHeight());
        }
        else if(location.equals("dh")){
            g.drawLine(0,localplayerY,getWidth(), localplayerY);
        }
        else if(location.equals("rv")){
            g.drawLine(localplayerX, 0,localplayerX, getHeight());
        }
        else if(location.equals("uh")){
            g.drawLine(0,localplayerY+localplayerHeight,getWidth(), localplayerY+localplayerHeight);
        }
        //draw the scores
        g.setFont(new Font(Font.DIALOG, Font.BOLD, 36));
        //g.drawString(String.valueOf(playerOneScore), 100, 100);
        //g.drawString(String.valueOf(playerTwoScore), 400, 100);

        //draw the ball
        g.fillOval(ballX, ballY, diameter, diameter);

        //draw the paddles
        g.fillRect(localplayerX, localplayerY, localplayerWidth, localplayerHeight);

        if(islv)
        {
            g.drawLine(lvX+lvWidth, 0,lvX+lvWidth, getHeight());
            g.fillRect(lvX, lvY, lvWidth, lvHeight);
        }
        if(isdh)
        {
            g.drawLine(0,dhY, getWidth(), dhY);
            g.fillRect(dhX, dhY, dhWidth, dhHeight);
        }
        if(isrv)
        {
            g.drawLine(rvX, 0,rvX, getHeight());
            g.fillRect(rvX, rvY, lvWidth, lvHeight);
        }
        if(isuh)
        {
            g.drawLine(0,uhY+uhHeight, getWidth(), uhY+uhHeight);
            g.fillRect(uhX, uhY, uhWidth, uhHeight);
        }
        /*g.fillRect(playerOneX, playerOneY, playerOneWidth, playerOneHeight);
        g.fillRect(playerTwoX, playerTwoY, playerTwoWidth, playerTwoHeight);
        g.fillRect(playerThreeX, playerThreeY, playerThreeWidth, playerThreeHeight);*/
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
        else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            leftPressed = true;
        }
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            rightPressed = true;
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
        else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            leftPressed = false;
        }
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            rightPressed = false;
        }
    }

}
