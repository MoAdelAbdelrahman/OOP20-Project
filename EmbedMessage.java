//EmbedMessage.java
 import java.awt.image.*;
 import javax.swing.*;
 import java.awt.*;
 import java.awt.event.*;
import static java.lang.Math.*;
 import javax.imageio.*;
 
 
 
 //GUI
 public class EmbedMessage extends JFrame implements ActionListener
 {
 JButton open = new JButton("Open"), embed = new JButton("Embed"),
    save = new JButton("Save Picture"), reset = new JButton("Reset"), Decode = new JButton("Decode");
 JLabel RandomBitView = new JLabel("Embeded in:");
 JLabel RandomBit = new JLabel();
 
 JTextArea message = new JTextArea(12,30);
 JTextArea messageDecoded = new JTextArea(12,20);
 BufferedImage sourceImage = null, embeddedImage = null;
 JSplitPane MainPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
 JScrollPane OriginalPicturePane = new JScrollPane(),
    EmbededPicturePane = new JScrollPane();
 //generating bits randomly 
 public int bits =  (int) (Math.random() * (3 - 1 + 1) + 1);

 public EmbedMessage() {
    super("Steganography Embedder");
    assembleInterface();
    this.setSize(1280, 720);
    this.setLocation(0, 0);
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);   
    this.setVisible(true);
    MainPanel.setDividerLocation(0.5);
    this.validate();
    }
 
 private void assembleInterface() {
    JPanel p = new JPanel(new FlowLayout());
    RandomBit.setText(Integer.toString(bits)+" "+"bits");
    p.add(open);
    p.add(embed);
    p.add(save);   
    p.add(reset);
    p.add(Decode);
    p.add(RandomBitView);
    p.add(RandomBit);
    RandomBitView.setVisible(false);
    RandomBit.setVisible(false);
    this.getContentPane().add(p, BorderLayout.SOUTH);
    open.addActionListener(this);
    embed.addActionListener(this);
   save.addActionListener(this);   
    reset.addActionListener(this);
    Decode.addActionListener(this);
  
    
    p = new JPanel(new GridLayout(1,1));
    p.add(new JScrollPane(message));
    p.add(new JScrollPane(messageDecoded));
    messageDecoded.setBorder(BorderFactory.createTitledBorder("Decoded Message is:"));
    messageDecoded.setEditable(false);
   
    message.setFont(new Font("Arial",Font.BOLD,20));
    p.setBorder(BorderFactory.createTitledBorder("Enter The Message You want to embed"));
    this.getContentPane().add(p, BorderLayout.NORTH);
    
    MainPanel.setLeftComponent(OriginalPicturePane);
    MainPanel.setRightComponent(EmbededPicturePane);
    OriginalPicturePane.setBorder(BorderFactory.createTitledBorder("Original Image"));
    EmbededPicturePane.setBorder(BorderFactory.createTitledBorder("Steganographed Image"));
    this.getContentPane().add(MainPanel, BorderLayout.CENTER);
    }
 
 public void actionPerformed(ActionEvent ae) {
    Object o = ae.getSource();
    if(o == open)
       openImage();
    else if(o == embed)
       embedMessage();
    else if(o == save) 
       saveImage();
    else if(o == reset) 
       resetInterface();
    else if (o == Decode)
        decodeMessage();
    }
 
 private java.io.File showFileDialog(final boolean open) {
    JFileChooser fc = new JFileChooser("Open an image");
    javax.swing.filechooser.FileFilter ff = new javax.swing.filechooser.FileFilter() {
       public boolean accept(java.io.File f) {
          String name = f.getName().toLowerCase();
          if(open)
             return f.isDirectory() || name.endsWith(".jpg") || name.endsWith(".jpeg") ||
                name.endsWith(".png") || name.endsWith(".gif") || name.endsWith(".tiff") ||
                name.endsWith(".bmp") || name.endsWith(".dib");
          return f.isDirectory() || name.endsWith(".png") ||    name.endsWith(".bmp");
          }
       public String getDescription() {
          if(open)
             return "Image (*.jpg, *.jpeg, *.png, *.gif, *.tiff, *.bmp, *.dib)";
          return "Image (*.png, *.bmp)";
          }
       };
    fc.setAcceptAllFileFilterUsed(false);
    fc.addChoosableFileFilter(ff);
 
    java.io.File f = null;
   if(open && fc.showOpenDialog(this) == fc.APPROVE_OPTION)
       f = fc.getSelectedFile();
    else if(!open && fc.showSaveDialog(this) == fc.APPROVE_OPTION)
       f = fc.getSelectedFile();
    return f;
    }
 
 private void openImage() {
    java.io.File OpenedPicture = showFileDialog(true);
    try {   
       sourceImage = ImageIO.read(OpenedPicture);
       JLabel l = new JLabel(new ImageIcon(sourceImage));
       OriginalPicturePane.getViewport().add(l);
       this.validate();
       } catch(Exception ex) { ex.printStackTrace(); }
    }
 
 private void embedMessage() {
    String mess = message.getText();
    embeddedImage = sourceImage.getSubimage(0,0,
       sourceImage.getWidth(),sourceImage.getHeight());
    embedMessage(embeddedImage, mess);
    JLabel l = new JLabel(new ImageIcon(embeddedImage));
    EmbededPicturePane.getViewport().add(l);
    this.validate();
    RandomBitView.setVisible(true);
    RandomBit.setVisible(true);
    }
 
 private void embedMessage(BufferedImage InputImg, String InputMessage) {
    int messageLength = InputMessage.length();
    int imageWidth = InputImg.getWidth(), imageHeight = InputImg.getHeight(),
       imageSize = imageWidth * imageHeight;
    if(messageLength * 8 + 32 > imageSize) {
       JOptionPane.showMessageDialog(this, "Message is too long for the chosen image",
          "Message too long!", JOptionPane.ERROR_MESSAGE);
       return;
       }
    byte b[] = InputMessage.getBytes();
    for(int i=0; i<b.length; i++)
    embedByte(InputImg, b[i], (i*8*bits)+32, 0);
    }
 

 
 private void embedByte(BufferedImage img, byte b, int start, int storageBit) {
    int maxX = img.getWidth(), maxY = img.getHeight(), 
       startX = start/maxY, startY = start - startX*maxY, count=0;
    for(int i=startX; i<maxX && count<8; i++) {
       for(int j=startY; j<maxY && count<8; j++) {
          int rgb = img.getRGB(i, j), bit = getBitValue(b, count);
          rgb = setBitValue(rgb, storageBit, bit);
          img.setRGB(i, j, rgb);
          count++;
          }
       }
    }
 
 private void saveImage() {
    if(embeddedImage == null) {
       JOptionPane.showMessageDialog(this, "No message has been embedded!", 
         "Nothing to save", JOptionPane.ERROR_MESSAGE);
       return;
      }
   java.io.File f = showFileDialog(false);
    String name = f.getName();
    String ext = name.substring(name.lastIndexOf(".")+1).toLowerCase();
    if(!ext.equals("png") && !ext.equals("bmp") &&   !ext.equals("dib")) {
          ext = "png";
          f = new java.io.File(f.getAbsolutePath()+".png");
          }
    try {
       if(f.exists()) f.delete();
       ImageIO.write(embeddedImage, ext.toUpperCase(), f);
       } catch(Exception ex) { ex.printStackTrace(); }
    }

 private void resetInterface() {
    message.setText("");
    OriginalPicturePane.getViewport().removeAll();
    EmbededPicturePane.getViewport().removeAll();
    messageDecoded.setText("");
    RandomBitView.setVisible(false);
    RandomBit.setVisible(false);
    sourceImage = null;
    embeddedImage = null;
    MainPanel.setDividerLocation(0.5);
    this.validate();
    }
 
 private int getBitValue(int n, int location) {
    int v = n & (int) Math.round(Math.pow(2, location));
    return v==0?0:1;
    }
 
 private int setBitValue(int n, int location, int bit) {
    int toggle = (int) Math.pow(2, location), bv = getBitValue(n, location);
    if(bv == bit)
       return n;
    if(bv == 0 && bit == 1)
       n |= toggle;
    else if(bv == 1 && bit == 0)
       n ^= toggle;
    return n;
    }
 

   private void decodeMessage() {
       if (embeddedImage==null)
           embeddedImage=sourceImage;
    int len = extractInteger(embeddedImage, 0, 0);
    byte b[] = new byte[len];
    for(int i=0; i<len; i++)
       b[i] = extractByte(embeddedImage, (i*8*bits)+32, 0);
    messageDecoded.setText(new String(b));
    }
 
 private int extractInteger(BufferedImage img, int start, int storageBit) {
   int maxX = img.getWidth(), maxY = img.getHeight(), 
       startX = start/maxY, startY = start - startX*maxY, count=0;
    int length = 0;
    for(int i=startX; i<maxX && count<32; i++) {
       for(int j=startY; j<maxY && count<32; j++) {
          int rgb = img.getRGB(i, j), bit = getBitValue(rgb, storageBit);
          length = setBitValue(length, count, bit);
          count++;
          }
       }
    return length;
    }
 
 private byte extractByte(BufferedImage img, int start, int storageBit) {
    int maxX = img.getWidth(), maxY = img.getHeight(), 
       startX = start/maxY, startY = start - startX*maxY, count=0;
    byte b = 0;
    for(int i=startX; i<maxX && count<8; i++) {
       for(int j=startY; j<maxY && count<8; j++) {
          int rgb = img.getRGB(i, j), bit = getBitValue(rgb, storageBit);
          b = (byte)setBitValue(b, count, bit);
          count++;
          }
       }
    return b;
    }
 
 public static void main(String arg[]) {
    new EmbedMessage();
    }
}