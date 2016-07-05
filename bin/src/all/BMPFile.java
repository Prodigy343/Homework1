package all;

import java.nio.channels.FileChannel;
import java.nio.MappedByteBuffer;
import java.io.FileInputStream;
import java.io.IOException;

public class BMPFile {
	
	//InfoHeader.bpp 1,4,8,24 bits
	public String path;
	public BMPFileHeader Header;
	public BMPInfoHeader InfoHeader;
	public int[] Palette;
	public int[] ImageData;
	
	BMPFile(){
		this.Header = new BMPFileHeader();
		this.InfoHeader = new BMPInfoHeader();
		this.Palette = new int[0];
		this.ImageData = new int[0];
	}
	
	@SuppressWarnings("resource")
	BMPFile(String path) throws IOException{		
		final FileChannel channel = new FileInputStream(path).getChannel();
		MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
		
		
		this.Header = new BMPFileHeader();
		this.InfoHeader = new BMPInfoHeader();
		this.Palette = new int[0];
		this.path = path;
		
		this.Header.ID		= Short.reverseBytes(buffer.getShort());
		this.Header.size 	= Integer.reverseBytes(buffer.getInt());
		this.Header.resv1 	= Short.reverseBytes(buffer.getShort());
		this.Header.resv2 	= Short.reverseBytes(buffer.getShort());
		this.Header.offset 	= Integer.reverseBytes(buffer.getInt());
		
		this.InfoHeader.headersize	= Integer.reverseBytes(buffer.getInt());
		this.InfoHeader.width		= Integer.reverseBytes(buffer.getInt());
		this.InfoHeader.height		= Integer.reverseBytes(buffer.getInt());
		this.InfoHeader.planos		= Short.reverseBytes(buffer.getShort());
		this.InfoHeader.bpp			= Short.reverseBytes(buffer.getShort());
		this.InfoHeader.compress	= Integer.reverseBytes(buffer.getInt());
		this.InfoHeader.imgsize		= Integer.reverseBytes(buffer.getInt());
		this.InfoHeader.bmpx		= Integer.reverseBytes(buffer.getInt());
		this.InfoHeader.bmpy		= Integer.reverseBytes(buffer.getInt());
		this.InfoHeader.colors		= Integer.reverseBytes(buffer.getInt());
		this.InfoHeader.imxtcolors	= Integer.reverseBytes(buffer.getInt());
	
		
		if(this.InfoHeader.bpp == 1){ 
			ImageData = new int[this.InfoHeader.width*this.InfoHeader.height*8];
			Palette = new int[2*3];
			buffer.get(new byte[this.InfoHeader.headersize-40]);
			readPalette(2,buffer);
			
			buffer.position(0);
			buffer.get(new byte[this.Header.offset]);
			
			if(((this.InfoHeader.width*this.InfoHeader.bpp)%32) == 0){	// NO PADDING
				for(int i=this.InfoHeader.height-1; i>=0 ;i--)
					for(int j=0; j<this.InfoHeader.width/8 ;j++)
						c1to24(Byte.toUnsignedInt(buffer.get()),i,j,this.InfoHeader.width/8);
			}else{
				int RD 	= this.InfoHeader.width; //easy math
				int M32	= 32*((RD/32)+1);
				int PD 	= M32 - RD;
				int BF 	= RD/8;
				int BP 	= PD/8;
				
				if((BF + BP) ==((RD + PD)/8)){
					for(int i=this.InfoHeader.height-1; i>=0 ;i--){
						for(int j=0; j<this.InfoHeader.width/8 ;j++)
							c1to24(Byte.toUnsignedInt(buffer.get()),i,j,this.InfoHeader.width/8);
						buffer.get(new byte[BP]);
					}
				}else{
					for(int i=this.InfoHeader.height-1; i>=0 ;i--){
						int act = 0;
						for(int j=0; j<this.InfoHeader.width/8 ;j++)
							act = p1to24(Byte.toUnsignedInt(buffer.get()),i,act,0,this.InfoHeader.width);
						p1to24(Byte.toUnsignedInt(buffer.get()),i,act,1,this.InfoHeader.width);
						buffer.get(new byte[BP]);
					}
				}
			}
			
		}else if(this.InfoHeader.bpp == 4){
			
			ImageData = new int[this.InfoHeader.width*this.InfoHeader.height*3];
			Palette = new int[16*3];
			buffer.get(new byte[this.InfoHeader.headersize-40]);
			readPalette(16,buffer);
			
			buffer.position(0);
			buffer.get(new byte[this.Header.offset]);
			
			if(((this.InfoHeader.width*this.InfoHeader.bpp)%32) == 0){	// NO PADDING
				for(int i=this.InfoHeader.height-1; i>=0 ;i--)
					for(int j=0; j<this.InfoHeader.width/2 ;j++)
						c4to24(Byte.toUnsignedInt(buffer.get()),i,j,this.InfoHeader.width/2);
			}else{
				int RD 	= this.InfoHeader.width*4; //easy math
				int M32	= 32*((RD/32)+1);
				int PD 	= M32 - RD;
				int BF 	= RD/8;
				int BP 	= PD/8;
			
				if((BF + BP) ==((RD + PD)/8)){ //Soft stuff
					for(int i=this.InfoHeader.height-1; i>=0 ;i--){
						for(int j=0; j<this.InfoHeader.width/2 ;j++)
							c4to24(Byte.toUnsignedInt(buffer.get()),i,j,this.InfoHeader.width/2);
						buffer.get(new byte[BP]);
					}
				}else{ //HARD PADDING!!
					for(int i=this.InfoHeader.height-1; i>=0 ;i--){
						int act = 0 ;
						for(int j=0; j<this.InfoHeader.width/2 ;j++)
							act = p4to24(Byte.toUnsignedInt(buffer.get()),i,act,0,this.InfoHeader.width);
						p4to24(Byte.toUnsignedInt(buffer.get()),i,act,1,this.InfoHeader.width);
						buffer.get(new byte[BP]);	
					}
				}
			}
			
		}else if(this.InfoHeader.bpp == 8){
			ImageData = new int[this.InfoHeader.width*this.InfoHeader.height*3];
			Palette = new int[256*3];
			buffer.get(new byte[this.InfoHeader.headersize-40]);
			readPalette(256,buffer);
			
			buffer.position(0);
			buffer.get(new byte[this.Header.offset]);
			
			
			if(((this.InfoHeader.width*this.InfoHeader.bpp)%32) == 0){// NO MADNESS
				for(int i=this.InfoHeader.height-1; i>=0 ;i--){
					for(int j=0; j<this.InfoHeader.width ;j++){
						int x = Byte.toUnsignedInt(buffer.get());
						ImageData[((i * this.InfoHeader.width) + j)*3] = Palette[x*3];
						ImageData[((i * this.InfoHeader.width) + j)*3+1] = Palette[x*3+1];
						ImageData[((i * this.InfoHeader.width) + j)*3+2] = Palette[x*3+2];
					}
				}
			}else{// MADNESS
				int madness = 4 - (this.InfoHeader.width%4);
				for(int i=this.InfoHeader.height-1; i>=0 ;i--){
					for(int j=0; j<this.InfoHeader.width ;j++){
						int x = Byte.toUnsignedInt(buffer.get());
						ImageData[((i * this.InfoHeader.width) + j)*3] = Palette[x*3];
						ImageData[((i * this.InfoHeader.width) + j)*3+1] = Palette[x*3+1];
						ImageData[((i * this.InfoHeader.width) + j)*3+2] = Palette[x*3+2];
					}
					for(int j=0;j<madness;j++) // BRING THE MADNESS!!
						buffer.get();
				}
			}
			
		}else if(this.InfoHeader.bpp == 24){	// NO PALETTE
			ImageData = new int[this.InfoHeader.width*this.InfoHeader.height*3];
			for(int i=0;i<this.Header.offset - 54;i++)buffer.get();

			if(((this.InfoHeader.width*this.InfoHeader.bpp)%32) == 0){// NO MADNESS	
				for(int i=this.InfoHeader.height-1; i>=0 ;i--){
					for(int j=0; j<this.InfoHeader.width ;j++){
						ImageData[((i * this.InfoHeader.width) + j)*3+2] = buffer.get();
						ImageData[((i * this.InfoHeader.width) + j)*3+1] = buffer.get();
						ImageData[((i * this.InfoHeader.width) + j)*3] = buffer.get();
					}
				}
			}else{// MADNESS
				int madness = 4 - ((this.InfoHeader.width*3)%4);
				for(int i=this.InfoHeader.height-1; i>=0 ;i--){
					for(int j=0; j<this.InfoHeader.width ;j++){
						ImageData[((i * this.InfoHeader.width) + j)*3+2] = buffer.get();
						ImageData[((i * this.InfoHeader.width) + j)*3+1] = buffer.get();
						ImageData[((i * this.InfoHeader.width) + j)*3] = buffer.get();
					}
					for(int j=0; j<madness ;j++) // BRING THE MADNESS!!
						buffer.get();
				}
			}
		}
		
	}
	
	public int p1to24(int B,int i,int act,int top,int w){
		int mask = 128;
		
		for(int d=7,l=0;d>=top;d--,l++){
			int index = ((mask>>l)&B)>>d;
			this.ImageData[((i*w)+act)*3] = Palette[index*3];
			this.ImageData[((i*w)+act)*3+1] = Palette[index*3+1];
			this.ImageData[((i*w)+act)*3+2] = Palette[index*3+2];
			act++;
		}
		return act;
	}
	
	public int p4to24(int B,int i,int act,int top,int w){
		int mask = 240;
		
		for(int d=1,l=0;d>=top;d--,l++){
			int index = ((mask>>4*l)&B)>>(4*d);
			this.ImageData[((i*w)+act)*3] = Palette[index*3];
			this.ImageData[((i*w)+act)*3+1] = Palette[index*3+1];
			this.ImageData[((i*w)+act)*3+2] = Palette[index*3+2];
			act++;
		}
		return act;
	}
	
	public void c1to24(int B,int i,int j,int w){
		
		int mask = 128;
		
		for(int d=7,l=0;d>=0;d--,l++){
			int index = ((mask>>l)&B)>>d;
			this.ImageData[((i*w+j)*8+l)*3] = Palette[index*3];
			this.ImageData[((i*w+j)*8+l)*3+1] = Palette[index*3+1];
			this.ImageData[((i*w+j)*8+l)*3+2] = Palette[index*3+2];
		}
	}
	
	public void c4to24(int B,int i,int j,int w){
		int mask = 240;
		
		for(int d=1,l=0;d>=0;d--,l++){
			int index = ((mask>>4*l)&B)>>(4*d);
			this.ImageData[((i*w+j)*2+l)*3] = Palette[index*3];
			this.ImageData[((i*w+j)*2+l)*3+1] = Palette[index*3+1];
			this.ImageData[((i*w+j)*2+l)*3+2] = Palette[index*3+2];
		}
	}
	
	public void readPalette(int colors,MappedByteBuffer in){
		for(int i=0;i<colors;i++){
			this.Palette[i*3+2] = Byte.toUnsignedInt(in.get());
			this.Palette[i*3+1] = Byte.toUnsignedInt(in.get());
			this.Palette[i*3] 	= Byte.toUnsignedInt(in.get());
			in.get();
		}
	}
	
	public void Negative(){
		for(int i=0;i<this.InfoHeader.height;i++){
			for(int j=0;j<this.InfoHeader.width;j++){
				ImageData[((i * this.InfoHeader.width) + j)*3] = 255 - ImageData[((i * this.InfoHeader.width) + j)*3];
				ImageData[((i * this.InfoHeader.width) + j)*3+1] = 255 - ImageData[((i * this.InfoHeader.width) + j)*3+1];
				ImageData[((i * this.InfoHeader.width) + j)*3+2] = 255 - ImageData[((i * this.InfoHeader.width) + j)*3+2];
			}
		}
	}
	
	public void ReflecX(){
		int [] buffer = new int[this.InfoHeader.width*this.InfoHeader.height*3];
		for(int i=0,a=this.InfoHeader.height-1;i<this.InfoHeader.height;i++,a--){
			for(int j=0;j<this.InfoHeader.width;j++){
				buffer[((i * this.InfoHeader.width) + j)*3] = ImageData[((a * this.InfoHeader.width) + j)*3];
				buffer[((i * this.InfoHeader.width) + j)*3 + 1] = ImageData[((a * this.InfoHeader.width) + j)*3 + 1];
				buffer[((i * this.InfoHeader.width) + j)*3 + 2] = ImageData[((a * this.InfoHeader.width) + j)*3 + 2];
			}
		}
		ImageData = buffer;
	}
	
	public void ReflecY(){
		int [] buffer = new int[this.InfoHeader.width*this.InfoHeader.height*3];
		for(int i=0;i<this.InfoHeader.height;i++){
			for(int j=0,a=this.InfoHeader.width-1;j<this.InfoHeader.width;j++,a--){
				buffer[((i * this.InfoHeader.width) + j)*3] = ImageData[((i * this.InfoHeader.width) + a)*3];
				buffer[((i * this.InfoHeader.width) + j)*3 + 1] = ImageData[((i * this.InfoHeader.width) + a)*3 + 1];
				buffer[((i * this.InfoHeader.width) + j)*3 + 2] = ImageData[((i * this.InfoHeader.width) + a)*3 + 2];
			}
		}
		ImageData = buffer;
	}
	
	public void C90(){
		int [] buffer = new int[this.InfoHeader.width*this.InfoHeader.height*3];
		for(int i=0,k=0;i<this.InfoHeader.width;i++,k++){
			for(int j=this.InfoHeader.height-1,l=0;j>=0;j--,l++){
				buffer[((k * this.InfoHeader.height) + l)*3] = ImageData[((j*this.InfoHeader.width)+i)*3];
				buffer[((k * this.InfoHeader.height) + l)*3 + 1] = ImageData[((j*this.InfoHeader.width)+i)*3+1];
				buffer[((k * this.InfoHeader.height) + l)*3 + 2] = ImageData[((j*this.InfoHeader.width)+i)*3+2];
			}
		}
		int a = this.InfoHeader.width;
		this.InfoHeader.width = this.InfoHeader.height;
		this.InfoHeader.height = a;
		ImageData = buffer;
	}
	
	public void C180(){
		C90();
		C90();
	}
	
	public void C270(){
		C90();
		C90();
		C90();
	}
}
