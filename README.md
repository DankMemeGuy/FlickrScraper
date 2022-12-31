# FlickrScraper

FlickrScraper is a Java program that scrapes search results and user profiles on flickr.com for image and video links to save. Scraping search results can be done by command line and scraping profiles can be done by command line and through the GUI.

## Features

- Save all photos and videos from a specific user's profile.
- Search for a specific word and save all photos and videos from users who recently uploaded under that term.
- Graphic User Interface

## Usage
Compile
```sh
javac *.java
```
Scrape Search Terms

```sh
java FlickrScraper <search term>
```

Scrape Specific Profile

```sh
java FlickrScraper <user profile link>
```

Launch The GUI

```sh
java FlickrScraperGUI
```