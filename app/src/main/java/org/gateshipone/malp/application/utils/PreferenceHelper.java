/*
 *  Copyright (C) 2023 Team Gateship-One
 *  (Hendrik Borghorst & Frederik Luetkes)
 *  Modified 2026 by jorgerjimenez for MALP Next (https://github.com/jorgerjimenez/malp-next)
 *
 *  The AUTHORS.md file contains a detailed contributors list:
 *  <https://gitlab.com/gateship-one/malp/blob/master/AUTHORS.md>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.gateshipone.malp.application.utils;


import android.content.Context;
import android.content.SharedPreferences;

import org.gateshipone.malp.R;
import org.gateshipone.malp.mpdservice.mpdprotocol.mpdobjects.MPDAlbum;
import org.gateshipone.malp.mpdservice.mpdprotocol.mpdobjects.MPDArtist;

public class PreferenceHelper {
    public enum LIBRARY_TRACK_CLICK_ACTION {
        ACTION_SHOW_DETAILS,
        ACTION_ADD_SONG,
        ACTION_ADD_SONG_AT_START,
        ACTION_PLAY_SONG,
        ACTION_PLAY_SONG_NEXT,
    }

    public static MPDAlbum.MPD_ALBUM_SORT_ORDER getMPDAlbumSortOrder(SharedPreferences prefs, Context context) {
        String sortOrderPref = prefs.getString(context.getString(R.string.pref_album_sort_order_key), context.getString(R.string.pref_artist_albums_sort_default));

        // Check with settings keys.
        if(sortOrderPref.equals(context.getString(R.string.pref_artist_albums_sort_name_key))) {
            return MPDAlbum.MPD_ALBUM_SORT_ORDER.TITLE;
        } else if (sortOrderPref.equals(context.getString(R.string.pref_artist_albums_sort_year_key))) {
            return MPDAlbum.MPD_ALBUM_SORT_ORDER.DATE;
        }

        // Default value
        return MPDAlbum.MPD_ALBUM_SORT_ORDER.TITLE;
    }

    public static MPDArtist.MPD_ALBUM_ARTIST_SELECTOR getAlbumArtistSelector(SharedPreferences prefs, Context context) {
        boolean useAlbumArtist = prefs.getBoolean(context.getString(R.string.pref_use_album_artists_key), context.getResources().getBoolean(R.bool.pref_use_album_artists_default));

        return useAlbumArtist ? MPDArtist.MPD_ALBUM_ARTIST_SELECTOR.MPD_ALBUM_ARTIST_SELECTOR_ALBUMARTIST : MPDArtist.MPD_ALBUM_ARTIST_SELECTOR.MPD_ALBUM_ARTIST_SELECTOR_ARTIST;
    }

    public static MPDArtist.MPD_ARTIST_SORT_SELECTOR getArtistSortSelector(SharedPreferences prefs, Context context) {
        boolean useSortArtist = prefs.getBoolean(context.getString(R.string.pref_use_artist_sort_key), context.getResources().getBoolean(R.bool.pref_use_artist_sort_default));

        return useSortArtist ? MPDArtist.MPD_ARTIST_SORT_SELECTOR.MPD_ARTIST_SORT_SELECTOR_ARTISTSORT : MPDArtist.MPD_ARTIST_SORT_SELECTOR.MPD_ARTIST_SORT_SELECTOR_ARTIST;
    }

    /**
     * Tapping a track plays it right away. The configurable click action setting was removed;
     * enqueue / play next / details remain available in the long-press menu.
     */
    public static LIBRARY_TRACK_CLICK_ACTION getClickAction(SharedPreferences prefs, Context context) {
        return LIBRARY_TRACK_CLICK_ACTION.ACTION_PLAY_SONG;
    }
}
