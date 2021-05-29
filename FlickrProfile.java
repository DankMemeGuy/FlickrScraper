import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;

/**
 * Flickr Profile
 * 
 * @author Jayden Weaver
 *
 */
public class FlickrProfile {

	private String pathAlias, username, realName;
	private URL profileURL, aboutURL, albumsURL, favesURL, galleriesURL, groupsURL;
	private String flickrPath = "https://www.flickr.com/photos/";
	private String aboutPath = "https://www.flickr.com/people/";
	ArrayList<String> photoLinks = new ArrayList<String>();

	public FlickrProfile(String pathAlias, String username) {
		this.pathAlias = pathAlias;
		this.username = username;
		setProfileURL(flickrPath + pathAlias + "/");
		setAboutURL(aboutPath + pathAlias + "/");
		setAlbumsURL();
		setFavesURL();
		setGalleriesURL();
		setGroupsURL();
	}

	/**
	 * Parse the FlickrProfile.
	 * 
	 * @return true if parsing is successful.
	 */
	public boolean parseProfile() {
		try {
			InputStream inStream;
			BufferedReader bufRead;
			String currentLine = null;
			int numberOfPages = getNumberOfPages();
			for (int i = 1; i <= numberOfPages; i++) {
				System.out.println("Parsing Page " + i + " of " + numberOfPages);
				inStream = new URL(getProfileURL().toString() + "page" + i).openStream();
				bufRead = new BufferedReader(new InputStreamReader(inStream));

				while ((currentLine = bufRead.readLine()) != null) {
					// Parse the image links.
					if (currentLine.contains("view photo-list-photo-view requiredToShowOnServer photostream awake")) {
						currentLine = currentLine.substring(currentLine.indexOf("live.staticflickr.com"),
								currentLine.indexOf(")\" data-view-signature"));
						int firstUnderscoreIndex = currentLine.indexOf("_");
						int secondUnderscoreIndex = currentLine.indexOf("_", firstUnderscoreIndex + 1);
						String fileExtension, fileEnd;
						if (secondUnderscoreIndex == -1) {
							fileExtension = currentLine.substring(currentLine.indexOf(".", firstUnderscoreIndex));
							currentLine = currentLine.replace(fileExtension, "_b" + fileExtension);
						} else {
							fileEnd = currentLine.substring(secondUnderscoreIndex);
							fileExtension = fileEnd.substring(fileEnd.indexOf("."));
							currentLine = currentLine.replace(fileEnd, "_b" + fileExtension);
						}
						addPhotoLink(currentLine);
					}
				}
				bufRead.close();
			}
		} catch (IOException e) {
			System.err.println("Parsing failed: 1");
			return false;
		}
		return true;
	}

	/**
	 * Get the number of pages.
	 * 
	 * @return number of pages.
	 */
	public int getNumberOfPages() {
		InputStream inStream;
		int numberOfPages = 0;
		try {
			inStream = getProfileURL().openStream();
			BufferedReader bufRead = new BufferedReader(new InputStreamReader(inStream));
			String currentLine = null;

			while ((currentLine = bufRead.readLine()) != null) {
				if (currentLine.contains("pagination1Click")) {
					while (!(currentLine = bufRead.readLine()).contains("rel=\"next\"")) {
						if (!currentLine.contains("moredots")) {
							currentLine = currentLine.substring(currentLine.indexOf("page"));
							currentLine = currentLine.substring(currentLine.indexOf("<span>") + 6,
									currentLine.indexOf("</span>"));
							numberOfPages = Integer.parseInt(currentLine);
						}
					}
				}
			}
			bufRead.close();
		} catch (IOException e) {
			System.err.println("Failed to get number of pages.");
		}
		return numberOfPages;
	}

	/**
	 * Saves photos to Flickr_Users.
	 * 
	 * @return true if all photos are saved.
	 */
	public boolean savePhotos() {
		File outputFile, directory;
		int saveCount = 0, failCount = 0, totalCount = 0, numberOfPhotos = this.photoLinks.size();
		for (String link : this.photoLinks) {
			try {
				System.out.println("Attempting to save photo " + (totalCount + 1) + " of " + numberOfPhotos);
				URL imageURL = new URL("https://" + link);
				BufferedImage photo = ImageIO.read(imageURL);
				directory = new File("Flickr_Users\\");
				directory.mkdir();
				directory = new File("Flickr_Users\\" + this.username + "\\");
				directory.mkdir();
				String filename = link.substring(link.indexOf("/", link.indexOf("/") + 1) + 1);
				outputFile = new File("Flickr_Users\\" + this.username + "\\" + filename);
				ImageIO.write(photo, filename.substring(filename.indexOf(".") + 1), outputFile);
				System.out.println("Saved: " + filename);
				saveCount++;
			} catch (MalformedURLException e) {
				System.out.println("Failed to save photo: 1 | " + link);
				failCount++;
			} catch (IOException e) {
				System.out.println("Failed to save photo: 2 | " + link);
				failCount++;
			}
			totalCount++;
		}
		System.out.println("Saved " + saveCount + " images." + " Failed to save " + failCount + " images.");
		return saveCount == totalCount;
	}

	public ArrayList<String> getPhotoLinks() {
		return this.photoLinks;
	}

	public boolean addPhotoLink(String link) {
		this.photoLinks.add(link);
		return !photoLinks.isEmpty();
	}

	public boolean setUsername(String username) {
		this.username = username;
		return this.username.equals(username);
	}

	public boolean setPathAlias(String pathAlias) {
		this.pathAlias = pathAlias;
		return this.pathAlias.equals(pathAlias);
	}

	public boolean setRealName(String realName) {
		this.realName = realName;
		return this.realName.equals(realName);
	}

	public boolean setProfileURL(String profileURL) {
		try {
			this.profileURL = new URL(profileURL);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return this.profileURL.getPath().equals(profileURL) && setAlbumsURL() && setFavesURL() && setGalleriesURL();
	}

	public boolean setAboutURL(String aboutURL) {
		try {
			this.aboutURL = new URL(aboutURL);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return this.aboutURL.getPath().equals(aboutURL) && setGroupsURL();
	}

	public boolean setAlbumsURL() {
		try {
			this.albumsURL = new URL(getProfileURL().toString() + "/albums");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return this.albumsURL.getPath().equals(getProfileURL().toString() + "/albums");
	}

	public boolean setFavesURL() {
		try {
			this.favesURL = new URL(getProfileURL().toString() + "/favorites");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return this.favesURL.getPath().equals(getProfileURL().toString() + "/favorites");
	}

	public boolean setGalleriesURL() {
		try {
			this.galleriesURL = new URL(getProfileURL().toString() + "/galleries");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return this.galleriesURL.toString().equals(getProfileURL().toString() + "galleries");
	}

	public boolean setGroupsURL() {
		try {
			this.groupsURL = new URL(getAboutURL().toString() + "/groups");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return this.groupsURL.toString().equals(getProfileURL().toString() + "/groups");
	}

	public URL getGroupsURL() {
		return this.groupsURL;
	}

	public URL getGalleriesURL() {
		return this.galleriesURL;
	}

	public URL getAlbumsURL() {
		return this.albumsURL;
	}

	public String getUsername() {
		return this.username;
	}

	public String getPathAlias() {
		return this.pathAlias;
	}

	public URL getProfileURL() {
		return this.profileURL;
	}

	public String getRealName() {
		return this.realName;
	}

	public URL getAboutURL() {
		return this.aboutURL;
	}

}
