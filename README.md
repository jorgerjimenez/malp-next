# MALP Next — Android MPD client

**MALP Next is a fork of [M.A.L.P.](https://gitlab.com/gateship-one/malp) by Team Gateship-One
(Hendrik Borghorst & Frederik Lütkes).** All credit for the original app goes to them and the
[M.A.L.P. contributors](AUTHORS.md). This fork is maintained by
[@jorgerjimenez](https://github.com/jorgerjimenez) and is not affiliated with or endorsed by
the original authors.

Like the original, the whole project is licensed under the **GPLv3 or later** (see [LICENSE](LICENSE)).
Files changed in this fork carry a "Modified 2026 by jorgerjimenez" note in their header.

## What's different from M.A.L.P. 1.3.2

**Platform**
- Targets Android 16 (API 36), builds with Android Gradle Plugin 9 / Gradle 9 and Java 17
- Edge-to-edge layout and predictive back gesture support
- Android 15 foreground-service time limit handled for the artwork downloader

**Library**
- Tap a song in an album: the queue is replaced with the album and playback starts at that song
- New **Tracks** tab with every song, sortable by title (default), artist, album or date added
- Tap a song in Tracks: plays it, followed by random songs; **Shuffle all** queues 500 random songs
- Lists show song titles without the track-number prefix

**Now playing**
- New layout: large cover, audio format line (e.g. FLAC 44.1 kHz / 16 bit), title, artist, album,
  outlined controls and a background tinted from the cover art
- Always opens on what's playing; the queue is one tap away; tap the mini player to open it
- Clear on/off state for shuffle and repeat
- Simplified queue: title, artist and duration only

**Simplified**
- Theme setting reduced to Follow system / Light / Dark / Black (OLED), using Material You colours
- Removed: track-click setting, "show queue first" setting, colour theme variants, Wikipedia
  lookups, stop button and volume controls in the player (stop is in the ⋮ menu)

**Fixes**
- Player could reopen on the queue instead of the cover
- Crash in the artwork downloader for songs stored in the music root folder

The app installs as **MALP Next** (`com.jorge.malp`) and can sit next to the original M.A.L.P.

## Building

Open the project in Android Studio (Quail or newer) and run the `app` configuration.
For a signed release build, create a `keystore.properties` file in the project root
(`storeFile`, `storePassword`, `keyAlias`, `keyPassword`); it is git-ignored.

---

# Original M.A.L.P. README

![Logo](https://gitlab.com/gateship-one/malp/raw/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png)
# M.A.L.P. - Android MPD Client #

[<img src="https://f-droid.org/badge/get-it-on.png" alt="Get it on F-Droid" height="80">](https://f-droid.org/app/org.gateshipone.malp)

This whole project is licensed under the  **GPLv3 or later** license (see LICENSE)

**[Screenshots](https://gitlab.com/gateship-one/malp/wikis/Screenshots)**

**Please check out the [Frequently Asked Questions](https://gitlab.com/gateship-one/malp/wikis/FAQ)**

## Requirements: ##
 - Android 5.0
 - MPD >= 0.14 (Everything < 0.23 is considered experimental going forward)
 
## Features: ##
 - Artist/Album/Files library browsing
 - AlbumArtist filtering
 - MBID filtering (multiple albums with same AlbumArtist, Title can be accessed separately) 
 - Server-based search
 - Local fast search
 - Launcher widget
 - Notification with background service (**optional, can be disabled in the settings**)
 - Volume control if application is in background (**only if notification is enabled & visible**)
 - Mostly optimized for tablets
 - Basic playlist management (add songs to saved lists, remove songs from saved lists, remove lists, save lists)
 - Multiple server profiles
 - Server statistics **(here is also the update database button)**
 - Artwork support with MusicBrainz, Last.fm, Fanart.tv as artwork provider (Album, artist images and fullscreen fanart)
 - Fanart view for tablets or stuff like that (click on the cover in the now playing view, or select fullscreen mode from menu)
 - Bulk downloader to download all artwork to local memory
 - HTTP audio streaming from MPD server
 - HTTP cover download
 - MPD artwork loading
 - Partitions (create, delete, move outputs)
 - Tag browser (filter Artists, Albums, Tracks) by all available MPD tags (e.g. genre, performer, composer, label, ...)

## Description ##

This is M.A.L.P. a fast and easy to use MPD client. The main goal is to create a MPD client that is both simple and fast to use and also follow the material design guidelines. 

This MPD client is tested with MPD 0.14, 0.19 and 0.23 but you really should not use outdated versions of MPD.

Support for outdated versions is experimental.

**This MPD client works best with accurately tagged music libraries. I recommend tagging using [MusicBrainz Picard](https://picard.musicbrainz.org/)** 

Starting with MPD 0.19 it is possible to filter list requests which allows this client to 
use the AlbumArtist and Artist tag for retrieval of albums. So if you have a lot of "Greatest Hits" albums you will get a list entry for each one of it. 


