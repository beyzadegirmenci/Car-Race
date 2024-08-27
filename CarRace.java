import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class CarRace extends JFrame{
	
	private JPanel gamePanel;
	private Player player1, player2;
	private Bot [] bots;
	private boolean isRaceFinished;
	private String winner;
	private String endTime;
	private int speed;
	
	public CarRace() {;
		setTitle("Car Race Game");
		setSize(800,800);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JButton startButton = new JButton("Oyunu Baslat");
		//Burada buton tanimlamak zorunda kaldim cunku tanimlamazsam speed baslangicta 0 olarak ayarlaniyordu
	   
		gamePanel = new JPanel() {
		
			@Override
			protected void paintComponent(Graphics g) {
				// TODO Auto-generated method stub
				super.paintComponent(g);
				drawOuterCircle(g);
				drawInnerCircle(g);
				drawStartLine(g);
				player1.paint(g);
				player2.paint(g);
				for(Bot bot :bots) {
					bot.paint(g);
				}
			}
		};
		gamePanel.setPreferredSize(new Dimension(800,800));
		gamePanel.setLayout(null);

		player1 = new Player (Color.RED, 25, 385);
		player2 = new Player (Color.GREEN, 45,385);
		
		bots = new Bot[5];
		int x=65;
		for(int i=0; i<bots.length; i++) {
			Color color = Color.BLACK;
			bots[i] = new Bot (color, x,385);	
			x+=20;
		}
		
		add(gamePanel);
		gamePanel.setLayout(new BorderLayout());
		gamePanel.add(startButton, BorderLayout.NORTH);
		startButton.addActionListener(e -> startGame());
		MyKeyListener keyListener = new MyKeyListener();
        gamePanel.addKeyListener(keyListener);
		gamePanel.setFocusable(true); 
		 
		setVisible(true);
		
		isRaceFinished=false;
		
	}
	
	private void startGame() {
		gamePanel.requestFocus();
		
		for (Bot bot : bots) {
			Thread botThread = new Thread(bot);
			botThread.start();
		}

		Thread player1Thread = new Thread(player1);
		player1Thread.start();

		Thread player2Thread = new Thread(player2);
		player2Thread.start();

		new Thread(() -> {
			long startTime = System.currentTimeMillis();
			while (!isRaceFinished) {
				long currentTime = System.currentTimeMillis();
				long elapsedTime = currentTime - startTime;
				int minutes = (int) (elapsedTime / (1000 * 60));
				int seconds = (int) ((elapsedTime / 1000) % 60);
				int splitSeconds = (int) (elapsedTime % 1000);
				String time = String.valueOf(minutes) + ":" + String.valueOf(seconds) + ":" + String.valueOf(splitSeconds);
				setEndTime(time);
				gamePanel.getGraphics().setColor(Color.BLACK);
				gamePanel.getGraphics().drawString(time, 10, 20);
			}
			checkWinner();
		}).start();

	}
	
	
	 public void checkCollision() {
		  
		//Birbirlerine carptiklarinda yatay olarak birbirlerinden uzaklassin istedim
		//yatay olarak uzaklastiklarinda dikey uzaklasmaya gerek yok
		if((Math.abs(player1.x-player2.x)<=10)&&(Math.abs(player1.y-player2.y)<=10)) {
			player1.x-=10;
			player2.x+=10;
			try {
				Thread.sleep(500);
			}catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		 
		for (Bot bot : bots) {
			if((Math.abs(player1.x-bot.x)<=10)&&(Math.abs(player1.y-bot.y)<=10)){
				player1.x-=10;
				bot.x+=10;
				try {
					Thread.sleep(500);
				}catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if((Math.abs(player2.x-bot.x)<=10)&&(Math.abs(player2.y-bot.y)<=10)){
				player2.x-=10;
				bot.x+=10;
				try {
					Thread.sleep(500);
				}catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		for (int i = 0; i < bots.length; i++) {
			for (int j = i + 1; j < bots.length; j++) {
				if (Math.abs(bots[i].x - bots[j].x) <= 10 && Math.abs(bots[i].y - bots[j].y) <= 10) {
					bots[i].x -= 10;
			        bots[j].x += 10;
			        try {
			        	Thread.sleep(500);
			        }catch (InterruptedException e) {
			        	e.printStackTrace();
			        }
				}
			}
		} 	
	}

	public void checkWinner() {
		
		if (isRaceFinished){	        
			String message = getWinner() + " Suresi " + getEndTime();
		    SwingUtilities.invokeLater(() -> {
		    	JOptionPane.showMessageDialog(this, message, "Oyun Bitti", JOptionPane.INFORMATION_MESSAGE);
		    });	
		}
	}	   	

	public void game(int speed) {
		
		setSpeed(speed);
		
		while(!isRaceFinished) {	
			checkCollision();
			checkWinner();
		
			try {
				Thread.sleep(1/getSpeed());
				gamePanel.repaint();
			}catch(InterruptedException e){
				e.printStackTrace();
			}
		}
	}
	
	public void drawOuterCircle(Graphics g) {
		g.setColor(Color.BLACK);
		g.drawOval(20, 40, 700, 700);
	}
	public void drawInnerCircle(Graphics g) {
		g.setColor(Color.BLACK);
		g.drawOval(170, 190, 400, 400);
	}
	public void drawStartLine (Graphics g) {
		g.setColor(Color.BLACK);
		g.drawLine(20, 390, 170, 390);
	}
	
	 public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public String getWinner() {
		return winner;
	}

	public void setWinner(String winner) {
		this.winner = winner;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	// Ic cember ve dis cember koordinatlari
    int outerCircleCenterX = 370; // Dis cemberin merkezi x koordinati
    int outerCircleCenterY = 390; // Dis cemberin merkezi y koordinati
    int outerCircleRadius = 350; // Dis cemberin yaricapi
    int innerCircleCenterX = 370; // Ic cemberin merkezi x koordinati
    int innerCircleCenterY = 390; //Ic cemberin merkezi x koordinati
    int innerCircleRadius = 200; // Ic cemberin yaricapi
	
	class Player extends Thread{
		
		private Color color;
		private int x,y;
		
		public Player (Color color, int x, int y) {
			this.color=color;
			this.x=x;
			this.y=y;
			 
		}
		
		public boolean isCollidingWithCircle(int centerX, int centerY, int radius) {
			/*Tuslara sirayla ve sakince basildiginda
			 *Ic cemberin icine girmiyor
			 *Ama dis cemberin cok 2-3 piksel disina ciktigi durumlar oluyor.
			 */
	    	  
			int carLeft=x;
			int carRight=x+10;
			int carTop=y;
			int carBottom = y+10;
	           
			int dx = centerX - Math.max(carLeft, Math.min(centerX, carRight));
			int dy = centerY - Math.max(carTop, Math.min(centerY, carBottom));
	        
			boolean isColliding = (dx * dx + dy * dy <= radius * radius);
	           
			if(isColliding) {
				int squaredD = dx*dx + dy*dy;
				int squaredInnerR = (int)Math.pow((radius-10),2);
				if(squaredD <= squaredInnerR) {
					return false;
				}
			}
			return isColliding;
		}
		@Override
	    public void run() {
			
			while (!isRaceFinished) {
				try {
					
					//Ben burda carpismalari koordinatlara gore kontrol etmeyi tercih ettim.
					//Hareket metotlarini MyKeyListener sinifinda tanimladim
					while ((x>20&&x<=370)&&(y<=390&&y>=190)) {
		        	
	            		if (isCollidingWithCircle(outerCircleCenterX, outerCircleCenterY, outerCircleRadius)){
	            			Thread.sleep(500);
	            			x+=10;
	            		}
	            		if (isCollidingWithCircle(innerCircleCenterX, innerCircleCenterY, innerCircleRadius)){
	            			Thread.sleep(500);
	            			x-=10;
	            		}
		        	}
		        	
		        	while((x>20&&x<=570)&&(y>40&&y<=190)){	
		        
	            		if (isCollidingWithCircle(outerCircleCenterX, outerCircleCenterY, outerCircleRadius)){
	            			Thread.sleep(500);
	            			y+=10;
	            		}
	            		if (isCollidingWithCircle(innerCircleCenterX, innerCircleCenterY, innerCircleRadius)){
	            			Thread.sleep(500);
	            			y-=10;
	            		}
		        	}
		        	
		        	while ((x<720&&x>570)&&(y>40&&y<=190)) {
		      
	            		if (isCollidingWithCircle(outerCircleCenterX, outerCircleCenterY, outerCircleRadius)){
	            			Thread.sleep(500);
	            			x-=10;
	            		}
		        	}			        					        		
		        	
		        	while((x<720&&x>370)&&(y>190&&y<=590)){
		        	
	            		if (isCollidingWithCircle(outerCircleCenterX, outerCircleCenterY, outerCircleRadius)){
	            			Thread.sleep(500);
	            			x-=10;
	            		}
	            		if (isCollidingWithCircle(innerCircleCenterX, innerCircleCenterY, innerCircleRadius)){
	            			 Thread.sleep(500);
	            			 x+=10;
	            		}
		        	}
		        	
		        	while((x<720&&x>=170)&&(y>590&&y<740)){
		        	
	            		if (isCollidingWithCircle(outerCircleCenterX, outerCircleCenterY, outerCircleRadius)){
	            			Thread.sleep(500);
	            			y-=10;
	            		}
	            		if (isCollidingWithCircle(innerCircleCenterX, innerCircleCenterY, innerCircleRadius)){
	            			Thread.sleep(500);
	            			y+=10;
	            		}
		        	}
		        	
		        	while ((x<170&&x>20)&&(y>=590&&y<740)) {
		       
	            		if (isCollidingWithCircle(outerCircleCenterX, outerCircleCenterY, outerCircleRadius)){
	            			Thread.sleep(500);
	            			x+=10;
	            		}
		        	}
		        	
		        	while ((x>20&&x<=370)&&(y>385&&y<590)) {
		      
	            		if (isCollidingWithCircle(outerCircleCenterX, outerCircleCenterY, outerCircleRadius)){
	            			Thread.sleep(500);
	            			x+=10;
	            		}
	            		if (isCollidingWithCircle(innerCircleCenterX, innerCircleCenterY, innerCircleRadius)){
	            			Thread.sleep(500);
	            			x-=10;
	            		}
	            		
	            		if(y==385 ) {
	            			isRaceFinished = true;
	            		
	            		}
	            		
		        	}
		        	repaint();
				}catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		public void paint(Graphics g) {
			g.setColor(color);
			g.fillRect(x, y, 10, 10);
		}	
	}
	
	class Bot extends Thread{
		private Color color;
		private int x, y;
	   
		public Bot(Color color, int x, int y) {
			this.color = color;
			this.x = x;
			this.y = y;
		}
	        
		public boolean isCollidingWithCircle(int centerX, int centerY, int radius) {
			int carLeft=x;
			int carRight=x+10;
			int carTop=y;
			int carBottom = y+10;
	           
			int dx = centerX - Math.max(carLeft, Math.min(centerX, carRight));
			int dy = centerY - Math.max(carTop, Math.min(centerY, carBottom));
	        
			boolean isColliding = (dx * dx + dy * dy <= radius * radius);
	           
			if(isColliding) {
				int squaredD = dx*dx + dy*dy;
				int squaredInnerR = (int)Math.pow((radius-10),2);
				if(squaredD <= squaredInnerR) {
					return false;
				}
			}
			return isColliding;
		}

		@Override
		public void run() {
			// Siyah arabalarin hareketleri ve carpisma kontrolleri
			while (!isRaceFinished) {
	            	
				try {
	        	
					while ((x>20&&x<=370)&&(y<=390&&y>=190)) {
						Thread.sleep((long) (1000/getSpeed()));
						int direction = (int)(Math.random()*3);
						switch(direction) {
							case 0: //West
								x-=1;
								break;
							case 1:  //East
								x+=1;
								break;
							case 2: //North
								y-=1;
								break;
		            		}
		            		if (isCollidingWithCircle(outerCircleCenterX, outerCircleCenterY, outerCircleRadius)){
		            			Thread.sleep(500);
		            			x+=5;
		            		}
		            		if (isCollidingWithCircle(innerCircleCenterX, innerCircleCenterY, innerCircleRadius)){
		            			Thread.sleep(500);
		            			x-=5;
		            		}
			        	}
			        	
			        	while((x>20&&x<=570)&&(y>40&&y<=190)){	
			        		Thread.sleep((long) (1000.0/getSpeed()));
			        		int direction = (int)(Math.random()*3);
		            		switch(direction) {
		            			case 0: //South
		            				y+=1;
		            				break;
		            			case 1:  //East
		            				x+=1;
		            				break;
		            			case 2: //North
		            				y-=1;
		            				break;
		            		}
		            		if (isCollidingWithCircle(outerCircleCenterX, outerCircleCenterY, outerCircleRadius)){
		            			Thread.sleep(500);
		            			y+=10;
		            		}
		            		if (isCollidingWithCircle(innerCircleCenterX, innerCircleCenterY, innerCircleRadius)){
		            			Thread.sleep(500);
		            			y-=10;
		            		}
			        	}
			        	
			        	while ((x<720&&x>570)&&(y>40&&y<=190)) {
			        		Thread.sleep((long) (1000.0/getSpeed()));
			        		int direction = (int)(Math.random()*3);
		            		switch(direction) {
		            			case 0: //South
		            				y+=1;
		            				break;
		            			case 1:  //East
		            				x+=1;
		            				break;
		            			case 2: //West
		            				x-=1;
		            				break;
		            		}
		            		if (isCollidingWithCircle(outerCircleCenterX, outerCircleCenterY, outerCircleRadius)){
		            			Thread.sleep(500);
		            			x-=10;
		            		}
			        	}			        					        		
			        	
			        	while((x<720&&x>370)&&(y>190&&y<=590)){
			        		Thread.sleep((long) (1000.0/getSpeed()));
			        		int direction = (int)(Math.random()*3);
		            		switch(direction) {
		            			case 0: //South
		            				y+=1;
		            				break;
		            			case 1:  //East
		            				x+=1;
		            				break;
		            			case 2: //West
		            				x-=1;
		            				break;
		            		}
		            		if (isCollidingWithCircle(outerCircleCenterX, outerCircleCenterY, outerCircleRadius)){
		            			Thread.sleep(500);
		            			x-=10;
		            		}
		            		if (isCollidingWithCircle(innerCircleCenterX, innerCircleCenterY, innerCircleRadius)){
		            			 Thread.sleep(500);
		            			 x+=10;
		            		}
			        	}
			        	
			        	while((x<720&&x>=170)&&(y>590&&y<740)){
			        		Thread.sleep((long) (1000.0/getSpeed()));
			        		int direction = (int)(Math.random()*3);
		            		switch(direction) {
		            			case 0: //South
		            				y+=1;
		            				break;
		            			case 1:  //North
		            				y-=1;
		            				break;
		            			case 2: //West
		            				x-=1;
		            				break;
		            		}
		            		if (isCollidingWithCircle(outerCircleCenterX, outerCircleCenterY, outerCircleRadius)){
		            			Thread.sleep(500);
		            			y-=10;
		            		}
		            		if (isCollidingWithCircle(innerCircleCenterX, innerCircleCenterY, innerCircleRadius)){
		            			Thread.sleep(500);
		            			y+=10;
		            		}
			        	}
			        	
			        	while ((x<170&&x>20)&&(y>=590&&y<740)) {
			        		Thread.sleep((long) (1000.0/getSpeed()));
			        		int direction = (int)(Math.random()*3);
		            		switch(direction) {
		            			case 0: //North
		            				y-=1;
		            				break;
		            			case 1:  //East
		            				x+=1;
		            				break;
		            			case 2: //West
		            				x-=1;
		            				break;
		            		}
		            		if (isCollidingWithCircle(outerCircleCenterX, outerCircleCenterY, outerCircleRadius)){
		            			Thread.sleep(500);
		            			x+=10;
		            		}
			        	}
			        	
			        	while ((x>20&&x<=370)&&(y>=385&&y<590)) {
			        		Thread.sleep((long) (1000.0/getSpeed()));
			        		int direction = (int)(Math.random()*3);
		            		switch(direction) {
		            			case 0: //West
		            				x-=1;
		            				break;
		            			case 1:  //East
		            				x+=1;
		            				break;
		            			case 2: //North
		            				y-=1;
		            				break;
		            		}
		            		if (isCollidingWithCircle(outerCircleCenterX, outerCircleCenterY, outerCircleRadius)){
		            			Thread.sleep(500);
		            			x+=10;
		            		}
		            		if (isCollidingWithCircle(innerCircleCenterX, innerCircleCenterY, innerCircleRadius)){
		            			Thread.sleep(500);
		            			x-=10;
		            		}
		            		if((y)==385) {
		            			
		            			isRaceFinished = true;
		            			setWinner("Bot Kazandi!");		
		            		}
			        	}			        	
			        
			        	gamePanel.repaint(); 
	                }catch (InterruptedException e) {
	                	e.printStackTrace();
	                }
	            }
		}

		public void paint(Graphics g) {
			g.setColor(color);
			g.fillRect(x, y, 10, 10);
		}
	}

	private class MyKeyListener implements KeyListener {

		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub	
		}

		@Override
		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();
			
			if (key == KeyEvent.VK_A) {
				player1.x-=2;
			}else if (key == KeyEvent.VK_S) {
				if(player1.y!=385||player1.x>170)
				player1.y+=2;
			}else if (key == KeyEvent.VK_D) {
				player1.x+=2;
			}else if (key == KeyEvent.VK_W) {
				player1.y-=2;
				if(player1.y==385) {
					isRaceFinished = true;
					setWinner("1. Oyuncu Kazandi!");
				}
			}else if (key == KeyEvent.VK_LEFT) {
				player2.x-=2;
				
			}else if (key == KeyEvent.VK_DOWN) {
				if(player2.y!=385||player2.x>170)
				player2.y+=2;
			}else if (key == KeyEvent.VK_RIGHT) {
				player2.x+=2;
			}else if (key == KeyEvent.VK_UP) {
				player2.y-=2;
				if(player2.y==385) {
					isRaceFinished = true;
					setWinner("2. Oyuncu Kazandi!");
				}
			}
			gamePanel.repaint();
		}
		
		@Override
		public void keyReleased(KeyEvent e) {
			// TODO Auto-generated method stub	
		}		
	}		
}