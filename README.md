# AussieMC
Aussie Master Chef Episode downloads
# Config to update

browser =chrome

url = https://eztv.ag/search/?q1=&q2=1248&search=Search

tor_path = /Users/<uname>/Downloads/
  
stats_filename = download_stats.txt

# Other Configs

BasePage.java -> Update chrome driver location

# Imports

Import as maven project and pom.xml has the dependencies to update

# Table iteration details

1. Iterates over top 10 rows in table ( 3 - 13 ) to check if episode has been downloaded before by comparing the status in download_stats.txt

2. If table has new episode ( top 10 rows) that are not present in download_stats.txt file, it starts the download and after download updates the download_stats.txt entry with EPIDOSE_ID-Done tag.

3. Iterating 10 rows of table only, since there are 300+ rows. This can be changed in line 56 in SearchPage.java
for (WebElement trElement : rows.subList(0, 13)) {      // Limiting to 10 rows ( 3 - 13 in table )

# Runs

1. Run as testNG in eclipse editor

2. if maven installed, run from command line $ mvn clean install

# File location

1. .torrent and .mkv are downloaded in default browser download location
2. .torrent file is removed after .mkv download is complete.
