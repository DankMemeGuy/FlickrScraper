import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * FlickrMiner
 * 
 * @author Jayden Weaver
 *
 */
public class FlickrMiner {

	public static void main(String[] args) {

		if (args.length != 1) {
			System.err.println(usage());
			System.exit(0);
		}

		final int PATHALIASINDEX = 1, USERNAMEINDEX = 2, REALNAMEINDEX = 3;

		try {
			URL flickrSearchPage = new URL("https://flickr.com/search/?text=" + args[0]);
			System.out.println(flickrSearchPage.toString());
			InputStream inStream = flickrSearchPage.openStream();
			BufferedReader bufRead = new BufferedReader(new InputStreamReader(inStream));

			String currentLine;
			String[] array = null;
			while ((currentLine = bufRead.readLine()) != null) {
				if (currentLine.contains("pathAlias")) {
					array = currentLine.split("\"_flickrModelRegistry\":");
					break;
				}
			}

			ArrayList<String> photoModels = new ArrayList<String>();

			for (int i = 0; i < array.length; i++) {
				if (array[i].contains("photo-lite-models")) {
					photoModels.add(array[i]);
				}
			}

			ArrayList<FlickrProfile> flickrProfiles = new ArrayList<FlickrProfile>();
			for (String s : photoModels) {
				String[] model = s.split(",");
				FlickrProfile profile = new FlickrProfile(
						model[PATHALIASINDEX].substring(model[PATHALIASINDEX].indexOf("\":") + 3,
								model[PATHALIASINDEX].length() - 1),
						model[USERNAMEINDEX].substring(model[USERNAMEINDEX].indexOf("\":") + 3,
								model[USERNAMEINDEX].length() - 1));
				profile.setRealName(model[REALNAMEINDEX].substring(model[REALNAMEINDEX].indexOf("\":") + 3,
						model[REALNAMEINDEX].length() - 1));
				flickrProfiles.add(profile);
			}

			inStream.close();
			bufRead.close();

			for (FlickrProfile profile : flickrProfiles) {
				profile.parseProfile();
				profile.savePhotos();
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String usage() {
		return "java FlickrMiner <search term>";
	}
}