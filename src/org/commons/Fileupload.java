package org.commons;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.math.RandomUtils;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class Fileupload {
	byte[] imgBufTemp = new byte[102401];
	private String tmpfath = null;

	@SuppressWarnings("unchecked")
	public String defaultProcessFileUpload(HttpServletRequest request)
			throws IOException, FileUploadException {
		ServletFileUpload upload = new ServletFileUpload();
		upload.setHeaderEncoding("UTF-8");
		InputStream stream = null;
		BufferedOutputStream bos = null;
		String fileUrl = "";
		try {
			if (ServletFileUpload.isMultipartContent(request)) {
				FileItemIterator iter = upload.getItemIterator(request);
				int i = 0;
				while (iter.hasNext()) {
					FileItemStream item = iter.next();
					stream = item.openStream();
					if (!item.isFormField()) {
						String prefix = new java.text.SimpleDateFormat(
								"yyyyMMddHHmmss").format(new Date())
								+ "-" + RandomUtils.nextInt();
						// 得到文件的扩展名(无扩展名时将得到全名)
						String ext = item.getName().substring(
								item.getName().lastIndexOf(".") + 1);
						String fileName = prefix + "." + ext;
						File savePath = getSavePath(request.getSession()
								.getServletContext(), fileName);
						if (i > 0) {
							fileUrl += ",";
						}
						fileUrl += request.getContextPath()
								+ getFileUrl(fileName);
						bos = new BufferedOutputStream(new FileOutputStream(
								savePath));
						int length;
						while ((length = stream.read(imgBufTemp)) != -1) {
							bos.write(imgBufTemp, 0, length);
						}
						i++;
					}
				}
				return fileUrl + "," + tmpfath + "," + Statics.UPLOAD_IMG_PATH;
			}
		} catch (FileUploadException e) {
			throw e;
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (Exception e) {
				}
			}
			if (bos != null) {
				try {
					bos.close();
				} catch (Exception e) {
				}
			}
			// try {
			// Tosmallerpic(ppath, this.f, null, Statics.TP_SY_WIDTH,
			// Statics.TP_SY_HEIGHT, 0.65f);
			// } catch (Exception x) {
			//				
			// }finally{
			// this.f.delete();
			// }
		}
		return null;
	}

	/**
	 * 生成保存上传文件的磁盘路径
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public File getSavePath(ServletContext servletContext, String fileName)
			throws IOException {
		String realPath = servletContext.getRealPath("/");
		File p = new File(realPath + Statics.UPLOAD_IMG_PATH);

		File tf = new File(realPath + Statics.UPLOAD_FILE_TEMP + fileName);
		File pf = new File(realPath + Statics.UPLOAD_FILE_TEMP);
		if (!p.exists()) {
			p.mkdirs();
		}
		if (!pf.exists()) {
			pf.mkdirs();
		}
		tmpfath = Statics.UPLOAD_FILE_TEMP + fileName;
		return tf;
	}

	/**
	 * 生成访问上传成功后的文件的URL地址
	 * 
	 * @param fileName
	 * @return
	 */
	public String getFileUrl(String fileName) {
		return Statics.UPLOAD_IMG_PATH + fileName;
	}

	/**
	 * 压缩图片大小
	 * 
	 * @param up_filepath
	 *            如果是修改,此项为修改前原始图片路径
	 * @param f_directory
	 *            生成新的图片所在的文件夹路径
	 * @param sourcefile
	 *            源文件图片完整路径
	 * @param zdy_fileNm
	 *            自定义图片名字(null则用源图片名)
	 * @param w
	 *            目标宽
	 * @param h
	 *            目标高
	 * @param per
	 *            清晰度百分比(越大质量越好)
	 * @throws IOException
	 */
	public void tosmallerpic(String up_filepath, String d_directory,
			String s_filepath, String zdy_fileNm, int w, int h, float per)
			throws IOException {
		Image src;
		File source_file = new File(s_filepath);
		src = javax.imageio.ImageIO.read(source_file); // 构造Image对象
		String img_midname = d_directory+"/"
				+ (zdy_fileNm == null ? source_file.getName() : zdy_fileNm
						+ source_file.getName().substring(
								source_file.getName().lastIndexOf('.')));
		int old_w = src.getWidth(null); // 得到源图宽
		int old_h = src.getHeight(null);// 得到源图长
		int new_w = 0;
		int new_h = 0;

		double w2 = (old_w * 1.00) / (w * 1.00);
		double h2 = (old_h * 1.00) / (h * 1.00);

		if (old_w > w && w != 0)
			new_w = (int) Math.round(old_w / w2);
		else
			new_w = old_w;
		if (old_h > h && w != 0)
			new_h = (int) Math.round(old_h / h2);
		else
			new_h = old_h;
		BufferedImage tag = new BufferedImage(new_w, new_h,
				BufferedImage.TYPE_INT_RGB);
		// tag.getGraphics().drawImage(src,0,0,new_w,new_h,null); //绘制缩小后的图
		tag.getGraphics().drawImage(
				src.getScaledInstance(new_w, new_h, Image.SCALE_SMOOTH), 0, 0,
				null);
		FileOutputStream newimage = new FileOutputStream(img_midname); // 输出到文件流
		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(newimage);
		JPEGEncodeParam jep = JPEGCodec.getDefaultJPEGEncodeParam(tag);
		/* 压缩质量 */
		jep.setQuality(per, true);
		encoder.encode(tag, jep);
		// encoder.encode(tag); //近JPEG编码
		newimage.close();
		source_file.delete();
		File u_file = new File(up_filepath);
		if (u_file.exists())
			u_file.delete();
	}
}
