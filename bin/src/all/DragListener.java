package all;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class DragListener implements DropTargetListener {
	
	public JLabel ImageLabel;
	public JLabel PathLabel;
	public BMPFile bmp;
	public app App;
	
	DragListener(app A,JLabel img,JLabel path){
		App = A;
		ImageLabel 	= img;
		PathLabel	= path;
	}
	
	@Override
	public void dragEnter(DropTargetDragEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dragExit(DropTargetEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dragOver(DropTargetDragEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dropActionChanged(DropTargetDragEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	//GET DROPPED IMAGE AND ADD TO LABEL
	@Override
	public void drop(DropTargetDropEvent ev) {
		// TODO Auto-generated method stub
		ev.acceptDrop(DnDConstants.ACTION_COPY);
		
		//GET DROPPED ITEMS
		Transferable t = ev.getTransferable();
		
		//GET DATA FORMAT
		DataFlavor[] df = t.getTransferDataFlavors();
		
		for(DataFlavor f:df){
			try{
				if(f.isFlavorJavaFileListType()){		
					@SuppressWarnings("unchecked")
					List<File> files =(List<File>) t.getTransferData(f);
					
					for(File file : files){
						displaysome(fixIt(file.getPath()));
					}
				}
			}catch(Exception ex){
				
			}
		}
	}
	
	public String fixIt(String path){
		String newP = path.replace("\\", "/");
		return newP;
	}
	
	public static Image getImageFromArray(int[] pixels, int width, int height){
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		WritableRaster raster = (WritableRaster) img.getData();
	    raster.setPixels(0, 0, width, height,pixels);
	    img.setData(raster);
	    BufferedImage nimg = resizeImage(img);
	    return nimg;   
	}
	
	public static BufferedImage resizeImage(BufferedImage originalImage){
        BufferedImage resizedImage = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, 512, 512, null);
        g.dispose();
        return resizedImage;
    }
	
	public void displaysome(String path) throws IOException{
		if(path.substring(path.length()-3).equals("bmp")){
			PathLabel.setText(path);
			bmp = new BMPFile(path);
			updateImageLabel(new ImageIcon(getImageFromArray(bmp.ImageData, bmp.InfoHeader.width, bmp.InfoHeader.height)));
		}else{
			JOptionPane.showMessageDialog(null,"Please drop a BMP File");
		}
	}
	
	public void Negative(){
		bmp.Negative();
		updateImageLabel(new ImageIcon(getImageFromArray(bmp.ImageData, bmp.InfoHeader.width, bmp.InfoHeader.height)));
	}
	
	public void ReflecX(){
		bmp.ReflecX();
		updateImageLabel(new ImageIcon(getImageFromArray(bmp.ImageData, bmp.InfoHeader.width, bmp.InfoHeader.height)));
	}
	
	public void ReflecY(){
		bmp.ReflecY();
		updateImageLabel(new ImageIcon(getImageFromArray(bmp.ImageData, bmp.InfoHeader.width, bmp.InfoHeader.height)));
	}
	
	public void rotate(boolean mode,int deg){
		if((deg == 90 && mode)||(deg == 270 && !mode)){
			bmp.C90();
		}else if((deg == 270 && mode)||(deg == 90 && !mode)){
			bmp.C270();
		}else{
			bmp.C180();
		}
		updateImageLabel(new ImageIcon(getImageFromArray(bmp.ImageData, bmp.InfoHeader.width, bmp.InfoHeader.height)));
	}
	
	public void updateImageLabel(ImageIcon img){
		ImageLabel.setIcon(img);
	}
}
