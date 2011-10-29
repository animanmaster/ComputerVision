/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package computervision.gui;

import computervision.image.BinaryImage;
import computervision.image.Pixel;
import computervision.image.RAWImage;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Malik Ahmed
 */
public class BinaryDraw extends RAWImageViewer implements MouseListener, MouseMotionListener
{
/*
    protected class HistoryItem
    {
        private BufferedImage state;

        protected HistoryItem previousState = null;
        protected HistoryItem nextState = null;

        public HistoryItem(BufferedImage state)
        {
            this.state = state;
        }

        public BufferedImage getState()
        {
            return state;
        }

        public HistoryItem getPrevious()
        {
            return previousState;
        }

        public HistoryItem getNext()
        {
            return nextState;
        }
    }
*/
//    private static final int MAX_HISTORY = 15;
    private static final int DEFAULT_WIDTH = 128, DEFAULT_HEIGHT = 128;

    private int historyCount = 0;
//    private HistoryItem lastItem = new HistoryItem(null);   //the root state, with no image and no previousState.

    private BufferedImage canvas;
    private int strokeWidth = 1;
    private Color color = Color.BLACK;

    private JMenuItem undo, redo;

    private int startX, startY; //strokes are composed of lines.

    public BinaryDraw()
    {
        this(null);
    }

    public BinaryDraw(RAWImage raw)
    {
        super(raw);
        buildGui();
    }
/*
    @Override
    protected JMenu buildMenu()
    {
        JMenu menu = super.buildMenu();
        JMenu edit = new JMenu("Edit");
//        setAccessibility(edit, 'E', KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.ALT_DOWN_MASK));
        edit.setMnemonic('E');
        undo = new JMenuItem("Undo");
        undo.setEnabled(false);
        setAccessibility(undo, 'U', KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK));
        undo.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if (historyCount > 0)
                {
                    saveState();    //lastItem becomes whatever we have on the canvas right now.
                    updateCanvasWith(lastItem.previousState);
                    lastItem = lastItem.previousState;
                    historyCount--;
                    checkHistoryState();
                }
            }
        });
        edit.add(undo);
        redo = new JMenuItem("Redo");
        setAccessibility(redo, 'R', KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_DOWN_MASK));
        undo.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if (lastItem.nextState != null)
                {
                    updateCanvasWith(lastItem.nextState);
                    lastItem = lastItem.nextState;
                    historyCount++;
                    checkHistoryState();
                }
            }
        });
        edit.add(redo);
        menu.add(edit);
        return menu;
    }
*/

    private void resizeCanvas(int width, int height)
    {
        BufferedImage old = canvas;
        canvas = new BufferedImage(width, height, old.getType());
        clearCanvas();
        canvas.getGraphics().drawImage(old, 0, 0, Color.WHITE, null);
        resizePanel();
    }

    protected Container buildToolbar()
    {
        final int MAX_WIDTH = 800, MAX_HEIGHT = 600, MIN_WIDTH = 1, MIN_HEIGHT = 1;
        JPanel tools = new JPanel(new FlowLayout(FlowLayout.LEADING, 5, 5));
        tools.add(new JLabel("Width:"));
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(DEFAULT_WIDTH, MIN_WIDTH, MAX_WIDTH, 1));
        spinner.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                int value = (Integer)((JSpinner)e.getSource()).getValue();
                value = (value < 1? 1 : value > MAX_WIDTH? MAX_WIDTH : value);
                if (value != canvas.getWidth())
                {
                    resizeCanvas(value, canvas.getHeight());
                    ((JSpinner)e.getSource()).setValue(value);
                }
            }
        });
        tools.add(spinner);

        tools.add(new JLabel("Height:"));
        spinner = new JSpinner(new SpinnerNumberModel(DEFAULT_HEIGHT, MIN_HEIGHT, MAX_HEIGHT, 1));
        spinner.addChangeListener(new ChangeListener()
        {
            public void stateChanged(ChangeEvent e) {
                int value = (Integer)((JSpinner)e.getSource()).getValue();
                value = (value < 1? 1 : value > MAX_HEIGHT? MAX_HEIGHT : value);
                if (value != canvas.getHeight())
                {
                    resizeCanvas(canvas.getWidth(), value);
                    ((JSpinner)e.getSource()).setValue(value);
                }
            }
        });
        tools.add(spinner);

        tools.add(new JLabel("Stroke:"));
        spinner = new JSpinner(new SpinnerNumberModel(strokeWidth, 1, 100, 1));
        spinner.addChangeListener(new ChangeListener()
        {
            public void stateChanged(ChangeEvent e) {
                int value = (Integer)((JSpinner)e.getSource()).getValue();
                value = (value < 1? 1 : value > 100? 100 : value);
                if (value != strokeWidth)
                {
                    strokeWidth = value;
                    ((JSpinner)e.getSource()).setValue(value);
                }
            }
        });
        tools.add(spinner);

        JButton button = new JButton("Clear");
        button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                clearCanvas();
            }
        });
        tools.add(button);

        /*
        tools.add(new JLabel("Color:"));
        final JLabel colorLabel = new JLabel()
        {

            @Override
            public void paint(Graphics g) {
                g.setColor(color);
                g.drawRect(0, 0, this.getWidth(), this.getHeight());
            }

        };
        colorLabel.setSize(50, 50);
        colorLabel.setBackground(this.color);
        colorLabel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                color = (color == Color.BLACK? Color.WHITE : Color.BLACK);
                colorLabel.setBackground(color);
                colorLabel.repaint();
            }
        });*/

        return tools;
    }

    private void buildGui()
    {
        Container imagePanel = getContentPane();    //includes scrollpane.
        Container toolbar = buildToolbar();
        canvas = new BufferedImage(DEFAULT_WIDTH, DEFAULT_HEIGHT, BufferedImage.TYPE_BYTE_GRAY);
        canvas.getGraphics().setColor(Color.WHITE);
        canvas.getGraphics().fillRect(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        canvas.getGraphics().setColor(color);
        //TODO This is disgusting.
        super.panel = new JPanel()
        {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                g.drawImage(canvas, 0, 0, Color.WHITE, null);
            }

        };
        imagePanel.removeAll();
        imagePanel.add(panel);
        super.panel.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        super.panel.addMouseListener(this);
        super.panel.addMouseMotionListener(this);
        setContentPane(new JPanel(new BorderLayout(5, 5)));
        getContentPane().add(toolbar, BorderLayout.NORTH);
        getContentPane().add(imagePanel, BorderLayout.CENTER);

    }

    private void revalidate()
    {
        invalidate();
        validate();
        repaint();
    }

    public void clearCanvas()
    {
        Graphics g = canvas.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        g.setColor(color);
        repaint();
    }

    public BinaryImage getBinaryImage()
    {
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        BinaryImage bimage = new BinaryImage(null, height, width);
        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                if (canvas.getRGB(x, y) == Color.BLACK.getRGB())
                {
                    bimage.addPixel(new Pixel(y, x, 1));
                }
            }
        }
        return bimage;
    }

    @Override
    protected boolean saveFile() {
        super.image = getRAW();
        return super.saveFile();
    }



    public RAWImage getRAW()
    {
        return new RAWImage(canvas);
    }

    public BufferedImage getRenderedImage()
    {
        return canvas;
    }

/*
    private void updateCanvasWith(HistoryItem historyState)
    {
        canvas = new BufferedImage(historyState.state.getWidth(), historyState.state.getHeight(),
                                        historyState.state.getType());
        canvas.getGraphics().drawImage(historyState.state, 0, 0, Color.WHITE, null);
        revalidate();
    }
*/
    private void resizePanel()
    {
        panel.setSize(canvas.getWidth(), canvas.getHeight());
        revalidate();
    }
/*
    private void checkHistoryState()
    {
        undo.setEnabled(lastItem.previousState != null);
        redo.setEnabled(lastItem.nextState != null);
    }

    private void saveState()
    {
        BufferedImage state = new BufferedImage(canvas.getColorModel(), canvas.copyData(null), canvas.getColorModel().isAlphaPremultiplied(), null);
        HistoryItem item = new HistoryItem(state);
        item.previousState = lastItem;  //this state's previous state is the top of the stack.
        lastItem.nextState = item;      //the previous top of the stack's next state is this new state.
        lastItem = item;                //this new state is now at the top of the stack.
    }
*/
    private void drawLine(int startX, int startY, int endX, int endY)
    {
        Graphics2D g = ((Graphics2D)canvas.getGraphics());
        g.setColor(color);
        g.setStroke(new BasicStroke(strokeWidth));
        g.drawLine(startX, startY, endX, endY);
        repaint();
    }


    /* MouseListener */

    
    public void mouseClicked(MouseEvent e)
    {
        //Meh.
    }

    public void mousePressed(MouseEvent e) 
    {
        //The start of a stroke.
        startX = e.getX();
        startY = e.getY();
        color = e.getButton() == MouseEvent.BUTTON1? Color.BLACK : Color.WHITE;
//        drawing = true;
        //Save the current state so that we can undo.
//        saveState();
    }

    public void mouseReleased(MouseEvent e) 
    {
//        drawing = false;
    }

    public void mouseEntered(MouseEvent e)
    {
        //Meh.
    }

    public void mouseExited(MouseEvent e)
    {
        //Meh
    }

    /* MouseMotionListener */

    public void mouseDragged(MouseEvent e)
    {
        drawLine(startX, startY, e.getX(), e.getY());
        startX = e.getX();
        startY = e.getY();
    }

    public void mouseMoved(MouseEvent e)
    {
        //Meh
    }

    public static void main(String[] args)
    {
        new BinaryDraw().setVisible(true);
    }
}
