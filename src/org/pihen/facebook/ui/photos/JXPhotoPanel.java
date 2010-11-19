package org.pihen.facebook.ui.photos;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.List;

import javax.imageio.ImageIO;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.graphics.ReflectionRenderer;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.util.PaintUtils;
import org.pihen.facebook.ui.FacebookSwingWindow;

import com.google.code.facebookapi.schema.Photo;
import com.google.code.facebookapi.schema.PhotoTag;



public class JXPhotoPanel extends JXPanel {
	
	
	
	private static final long serialVersionUID = 1L;

	private BufferedImage image=null;
	private Shape selectedShape = null;
	private ReflectionRenderer renderer;
	private Photo p;
	private Point pointInitial = null;
	private boolean isCtrlPressed = false;

	  
	public JXPhotoPanel()
	{
		initGUI();
	}
	

	public void showPhoto(Photo photo) {
		this.p=photo;
		try {
			image = ImageIO.read(new URL(p.getSrcBig()));
			image=renderer.appendReflection(image);
			
			int w = getWidth();
		    int h = getHeight();
		    int x = (w - image.getWidth())/2;
		    int y = (h - image.getHeight())/2;
			
		    selectedShape= new Rectangle2D.Double(x, y, image.getWidth(null),  image.getHeight(null));
		  
			printInfo();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		repaint();
	}

	public void printInfo()
	{
		try {
			List<PhotoTag> tags= FacebookSwingWindow.getInstance().getService().getTags(p);
			if(tags.size()>0)
				for(PhotoTag tag : tags)
				{
					System.out.println("x="+tag.getXcoord()+",y="+tag.getYcoord());
					System.out.println(FacebookSwingWindow.getInstance().getService().getUserById(tag.getSubject(),true).getName());
				}
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setStroke(new BasicStroke(0));
		
		if(image !=null)
		{
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0));   
			g2.draw(selectedShape);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
			    g2.drawImage(image, (int)selectedShape.getBounds().getX(), 
  		  			  (int)selectedShape.getBounds().getY(),
  		  			  (int)selectedShape.getBounds().getWidth(),
  		  			  (int)selectedShape.getBounds().getHeight(),
//  		  			  (int)selectedShape.getBounds(),
//  		  			  (int)selectedShape.getBounds().getHeight(),
//  		  			  (int)selectedShape.getBounds().getHeight(), 
//  		  			  (int)selectedShape.getBounds().getHeight(),
  		  			  null);    
			
		}
	}

	private void initGUI() {
		renderer = new ReflectionRenderer();
		setBackgroundPainter(new MattePainter(PaintUtils.NIGHT_GRAY,true));

	    GestionnaireEvenements interactionManager = new GestionnaireEvenements(this); 
		    this.addMouseListener(interactionManager);
		    this.addMouseMotionListener(interactionManager);
		    this.addMouseWheelListener(interactionManager);
		    this.addKeyListener(interactionManager);
		    this.setFocusable(true);
		    
	}

	private class GestionnaireEvenements extends MouseAdapter implements KeyListener 
	  {
			public JXPanel mainPanel;
		
			public GestionnaireEvenements(JXPanel panel)
			{
					this.mainPanel = panel;
			}
		
		
		 	public void mouseWheelMoved(MouseWheelEvent e) {
					double quotien = 1.1;
			          if (selectedShape.contains(e.getPoint()))
			          {
			            if(e.getWheelRotation()==-1)//zoom
			            {
			            	selectedShape= new Rectangle2D.Double((int)selectedShape.getBounds().getX(),(int) selectedShape.getBounds().getY(), (int)selectedShape.getBounds().getWidth()*quotien, (int)selectedShape.getBounds().getHeight()*quotien);
			            }
			            else
			            {
			            	selectedShape= new Rectangle2D.Double((int)selectedShape.getBounds().getX(),(int) selectedShape.getBounds().getY(), (int)selectedShape.getBounds().getWidth()/quotien, (int)selectedShape.getBounds().getHeight()/quotien);
			            }
			            mainPanel.repaint();
			          }
			}
			
			public void mousePressed(MouseEvent e)
		    {
		      if (selectedShape.contains(e.getPoint()))
		      {
		            pointInitial = e.getPoint();
		            mainPanel.repaint();
		      }
		    }
		  
			public void mouseReleased(MouseEvent e){    }
		    
		    public void mouseDragged(MouseEvent e) {
		        
		    	if(isCtrlPressed)
			    {
			    	System.out.println("rotation " + Math.PI);
			    }
		    	  
		    	if (selectedShape != null) {
		           int deltaX = e.getX() - pointInitial.x;
		           int deltaY = e.getY() - pointInitial.y;
		           pointInitial = e.getPoint();
		           AffineTransform at = AffineTransform.getTranslateInstance(deltaX,deltaY);
		           selectedShape = at.createTransformedShape(selectedShape);
		           mainPanel.repaint();
		        }
		        
		        
		        
		     }
		
			public void keyPressed(KeyEvent e) {
				
				if(e.getKeyCode()==KeyEvent.VK_CONTROL)
					isCtrlPressed=true;
			}
		
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_CONTROL)
					isCtrlPressed=false;
			}
		
			public void keyTyped(KeyEvent e) {	}
		    
	    }
}
