package computervision.gui;

import computervision.image.RAWImage;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * View RAWImages.
 * @author Malik Ahmed
 */
public class RAWImageViewer extends JFrame
{
    protected RAWImage image = null;
    protected JPanel panel = new JPanel();
    protected FileNameExtensionFilter rawFilter = new FileNameExtensionFilter("RAW File", "raw", "RAW"),
                                    pngFilter = new FileNameExtensionFilter("PNG File", "png", "PNG");
    protected File fileDir = new File(".");
    protected JFileChooser openFileChooser = new JFileChooser();
    protected JFileChooser saveFileChooser = new JFileChooser();

    protected JPopupMenu popupMenu = new JPopupMenu();

    public RAWImageViewer()
    {
        super("RAW Image Viewer");

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JMenuBar menubar = new JMenuBar();
        menubar.add(buildMenu());
        setJMenuBar(menubar);

        JPanel centeredPanel = new JPanel(new GridBagLayout());
        centeredPanel.add(panel, new GridBagConstraints());

        JScrollPane scrollPane = new JScrollPane(centeredPanel);
        popupMenu.add(buildMenu());
        this.panel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                if (e.getButton() == MouseEvent.BUTTON3 ||
                        e.getButton() == MouseEvent.BUTTON2)    //Right- or Middle-Click
                {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        
        setContentPane(scrollPane);

        setSize(400, 300);

        openFileChooser.setAcceptAllFileFilterUsed(false);
        openFileChooser.setFileFilter(rawFilter);
        saveFileChooser.setAcceptAllFileFilterUsed(false);
        saveFileChooser.addChoosableFileFilter(rawFilter);
        saveFileChooser.addChoosableFileFilter(pngFilter);
        saveFileChooser.setFileFilter(pngFilter);

    }

    protected void setAccessibility(JMenuItem item, char mnemonic, KeyStroke keystroke)
    {
       item.setMnemonic(mnemonic);
       item.setAccelerator(keystroke);
    }

    protected JMenu buildMenu()
    {
        JMenu menu = new JMenu("File");
        menu.setMnemonic('F');
        JMenuItem item = new JMenuItem("Open");
        setAccessibility(item, 'O', KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
        item.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                RAWImage opened = openFile();
                if (opened != null)
                    displayImage(opened);
            }
        });
        menu.add(item);

        item = new JMenuItem("Save As");
        setAccessibility(item, 'S', KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
        item.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                saveFile();
            }
        });
        menu.add(item);

        item = new JMenuItem("Quit");
        item.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                quit();
            }
        });
        setAccessibility(item, 'Q', KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK));
        menu.add(item);
        return menu;
    }

    public RAWImageViewer(RAWImage image)
    {
        this();
        displayImage(image);
    }

    protected void uhOh(Exception e)
    {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this,
                "An error occurred: " + e.getMessage(),
                "Exception", JOptionPane.ERROR_MESSAGE);
    }

    protected RAWImage openFile()
    {
        RAWImage opened = null;
        openFileChooser.setCurrentDirectory(fileDir);
        if (JFileChooser.APPROVE_OPTION == openFileChooser.showOpenDialog(null))
        {
            try
            {
                File file = openFileChooser.getSelectedFile();
                int rows = Integer.parseInt(JOptionPane.showInputDialog("Enter the number of rows in the image."));
                int cols = Integer.parseInt(JOptionPane.showInputDialog("Enter the number of columns in the image."));
                opened = new RAWImage(file, rows, cols);
                fileDir = openFileChooser.getCurrentDirectory();
            }
            catch(Exception ioe)
            {
                uhOh(ioe);
            }
        }
        return opened;
    }

    protected boolean saveFile()
    {
        boolean saved = false;
        saveFileChooser.setCurrentDirectory(fileDir);
        if (JFileChooser.APPROVE_OPTION == saveFileChooser.showSaveDialog(this))
        {
            try
            {
                File file = saveFileChooser.getSelectedFile();
                if (rawFilter.equals(saveFileChooser.getFileFilter()))
                {
                    if (file.getName().length() - file.getName().indexOf((int)'.') - 1 != 3)
                    {
                        //No appropriate extension.
                        file = new File(file.getAbsolutePath() + ".raw");
                    }
                    this.image.write(new FileOutputStream(file));
                    saved = true;
                }
                else if (pngFilter.equals(saveFileChooser.getFileFilter()))
                {
                    if (file.getName().length() - file.getName().indexOf((int)'.') - 1 != 3)
                    {
                        //No appropriate extension.
                        file = new File(file.getAbsolutePath() + ".png");
                    }
                    ImageIO.write(this.image.toBufferedImage(), "png", file);
                    saved = true;
                }
                else
                {
                    saved = false;
                }
                fileDir = saveFileChooser.getCurrentDirectory();
            }
            catch(Exception ioe)
            {
                uhOh(ioe);
            }
        }
        return saved;
    }

    private void quit()
    {
        dispose();
    }

    public void displayImage(RAWImage image)
    {
        //TODO maybe clear the panel if we get a null?
        if (image != null)
        {
            this.image = image;
            this.panel.removeAll();
            this.panel.add(new JLabel(new ImageIcon(this.image.toBufferedImage())));
            this.panel.validate();
        }
    }

    public RAWImage getImage()
    {
        return this.image;
    }

    public static void main(String[] args)
    {
        new RAWImageViewer().setVisible(true);
    }

}
