/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package computervision;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JFrame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JPanel;

class thelulz extends JFrame {
public thelulz(){
super("Painting Application");
setSize(400, 500);
setVisible(true);
addWindowListener(new WindowAdapter() {
@Override
public void windowClosing(WindowEvent e){
System.exit(0);
}
});
add(new MyPanel());
}
public static void main(String[] args) {
new thelulz();
}
}

class MyPanel extends JPanel{
MouseMotionListener theListener;
Graphics g;
MouseListener theListening;
int x,y,x1,y1;
boolean inside;
public MyPanel(){
x=0; y=0; x1=-99; y1=-99;
inside=false;
theListener=new MouseMotionListener() {
public void mouseDragged(MouseEvent arg0) {
if(inside){
repaint();
}
}
public void mouseMoved(MouseEvent arg0) {
}
};
theListening=new MouseListener() {

public void mouseClicked(MouseEvent arg0) {
//Nothing to be done…
}

public void mousePressed(MouseEvent arg0) {
//Nothing to be done…
}

public void mouseReleased(MouseEvent arg0) {
x1=-99;y1=-99;//Mouse released. We need to make sure no last point for future use.
}

public void mouseEntered(MouseEvent arg0) {
inside=true; //Mouse has just entered
x1=-99;y1=-99; //No last point because the mouse was outside.
}

public void mouseExited(MouseEvent arg0) {
inside=false; //Mouse is now outside
x1=-99; y1=-99; //No last point, mouse is outside
}
};
this.setSize(400,500);
this.setVisible(true);
this.addMouseMotionListener(theListener);
this.addMouseListener(theListening);
}
@Override
public void paint(Graphics g){

try{
if(x1==-99){ // if x equals to -99 then y also equals to -99. A point of (-99,-99) is not possible so nothing will be drawn.

x=getMousePosition().x; x1=x; //Getting the point so that we will be able to use it as the first point
y=getMousePosition().y; y1=y;  //of the line when another point is clicked.
}
else//Event just occurred at (x1,y1). Between (x,y) and (x1,y1) a line will be drawn
{
x=getMousePosition().x;
y=getMousePosition().y;
((Graphics2D)g).setStroke(new BasicStroke(20));
g.drawLine(x, y, x1, y1);
x1=x; y1=y;
}
}
catch(Exception ex){
}
}
}
