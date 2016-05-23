package org.kerwin.shutdownui.bean;

public class PictureInfo {

	private String thumbnailPic;
	private String bmiddlePic;
	private String originalPic;

	public PictureInfo(String thumbnailPic, String bmiddlePic,
			String originalPic) {
		this.thumbnailPic = thumbnailPic;
		this.bmiddlePic = bmiddlePic;
		this.originalPic = originalPic;
	}

	public String getThumbnailPic() {
		return thumbnailPic;
	}

	public void setThumbnailPic(String thumbnailPic) {
		this.thumbnailPic = thumbnailPic;
	}

	public String getBmiddlePic() {
		return bmiddlePic;
	}

	public void setBmiddlePic(String bmiddlePic) {
		this.bmiddlePic = bmiddlePic;
	}

	public String getOriginalPic() {
		return originalPic;
	}

	public void setOriginalPic(String originalPic) {
		this.originalPic = originalPic;
	}

}
