/*
 *  Copyright (C) 2026 jorgerjimenez <https://github.com/jorgerjimenez>
 *
 *  Part of MALP Next, a fork of M.A.L.P. by Team Gateship-One
 *  (Hendrik Borghorst & Frederik Luetkes): <https://gitlab.com/gateship-one/malp>
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

package org.gateshipone.malp.application.fragments.serverfragments;

import android.os.Bundle;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import org.gateshipone.malp.R;
import org.gateshipone.malp.application.adapters.TracksRecyclerViewAdapter;
import org.gateshipone.malp.application.callbacks.AddPathToPlaylist;
import org.gateshipone.malp.application.listviewitems.GenericViewItemHolder;
import org.gateshipone.malp.application.utils.ThemeUtils;
import org.gateshipone.malp.application.viewmodels.AllTracksViewModel;
import org.gateshipone.malp.application.viewmodels.GenericViewModel;
import org.gateshipone.malp.application.views.MalpRecyclerView;
import org.gateshipone.malp.mpdservice.handlers.serverhandler.MPDQueryHandler;
import org.gateshipone.malp.mpdservice.mpdprotocol.mpdobjects.MPDFileEntry;
import org.gateshipone.malp.mpdservice.mpdprotocol.mpdobjects.MPDTrack;

import java.util.ArrayList;
import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Library tab listing every track. Tapping a track replaces the queue with that track followed by
 * random tracks; "Shuffle all" replaces the queue with a random selection from the whole library.
 */
public class AllTracksFragment extends GenericMPDRecyclerFragment<MPDFileEntry, GenericViewItemHolder> implements MalpRecyclerView.OnItemClickListener {
    public static final String TAG = AllTracksFragment.class.getSimpleName();

    /**
     * Number of random tracks queued by "Shuffle all". Keeps the queue well below MPD's default
     * max_playlist_length (16384) and the command list size limit, even for very large libraries.
     */
    private static final int SHUFFLE_ALL_TRACK_COUNT = 500;

    private List<MPDFileEntry> mTracks = new ArrayList<>();

    private enum SORT_ORDER {
        TITLE, ARTIST, ALBUM, ADDED
    }

    /** Current sort order, saved in the preferences. Default: title A-Z. */
    private SORT_ORDER mSortOrder = SORT_ORDER.TITLE;

    public static AllTracksFragment newInstance() {
        return new AllTracksFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_all_tracks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = view.findViewById(R.id.recycler_view);
        mAdapter = new TracksRecyclerViewAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemClicklistener(this);
        setLinearLayoutManagerAndDecoration();
        registerForContextMenu(mRecyclerView);

        mSwipeRefreshLayout = view.findViewById(R.id.refresh_layout);
        mSwipeRefreshLayout.setColorSchemeColors(ThemeUtils.getThemeColor(requireContext(), R.attr.colorAccent),
                ThemeUtils.getThemeColor(requireContext(), R.attr.colorPrimary));
        mSwipeRefreshLayout.setOnRefreshListener(this::refreshContent);

        final ExtendedFloatingActionButton shuffleButton = view.findViewById(R.id.all_tracks_shuffle_button);
        shuffleButton.setOnClickListener(v -> shuffleAll());
        // Shrink to icon while scrolling down, expand again when scrolling up
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 && shuffleButton.isExtended()) {
                    shuffleButton.shrink();
                } else if (dy < 0 && !shuffleButton.isExtended()) {
                    shuffleButton.extend();
                }
            }
        });

        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(requireContext());
        try {
            mSortOrder = SORT_ORDER.valueOf(sharedPref.getString(getString(R.string.pref_all_tracks_sort_key), SORT_ORDER.TITLE.name()));
        } catch (IllegalArgumentException e) {
            mSortOrder = SORT_ORDER.TITLE;
        }
        setHasOptionsMenu(true);

        getViewModel().getData().observe(getViewLifecycleOwner(), this::onDataReady);
    }

    @Override
    GenericViewModel<MPDFileEntry> getViewModel() {
        return new ViewModelProvider(this, new AllTracksViewModel.AllTracksModelFactory(requireActivity().getApplication())).get(AllTracksViewModel.class);
    }

    @Override
    protected void onDataReady(List<MPDFileEntry> model) {
        mTracks = model != null ? model : new ArrayList<>();
        super.onDataReady(model != null ? sortTracks(mTracks) : null);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.fragment_menu_all_tracks, menu);

        final MenuItem sortItem = menu.findItem(R.id.action_sort_tracks);
        final Drawable icon = sortItem.getIcon();
        if (icon != null) {
            final Drawable tinted = DrawableCompat.wrap(icon.mutate());
            DrawableCompat.setTint(tinted, ThemeUtils.getThemeColor(requireContext(), R.attr.app_color_on_surface));
            sortItem.setIcon(tinted);
        }

        final int checkedId;
        switch (mSortOrder) {
            case ARTIST:
                checkedId = R.id.action_sort_artist;
                break;
            case ALBUM:
                checkedId = R.id.action_sort_album;
                break;
            case ADDED:
                checkedId = R.id.action_sort_added;
                break;
            default:
                checkedId = R.id.action_sort_title;
                break;
        }
        final MenuItem checked = menu.findItem(checkedId);
        if (checked != null) {
            checked.setChecked(true);
        }
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        final int itemId = item.getItemId();
        SORT_ORDER newOrder = null;
        if (itemId == R.id.action_sort_title) {
            newOrder = SORT_ORDER.TITLE;
        } else if (itemId == R.id.action_sort_artist) {
            newOrder = SORT_ORDER.ARTIST;
        } else if (itemId == R.id.action_sort_album) {
            newOrder = SORT_ORDER.ALBUM;
        } else if (itemId == R.id.action_sort_added) {
            newOrder = SORT_ORDER.ADDED;
        }

        if (newOrder == null) {
            return super.onOptionsItemSelected(item);
        }

        item.setChecked(true);
        if (newOrder != mSortOrder) {
            mSortOrder = newOrder;
            PreferenceManager.getDefaultSharedPreferences(requireContext()).edit()
                    .putString(getString(R.string.pref_all_tracks_sort_key), mSortOrder.name()).apply();
            mAdapter.swapModel(sortTracks(mTracks));
            mRecyclerView.scrollToPosition(0);
        }
        return true;
    }

    /**
     * Returns a sorted copy of the track list for the current sort order.
     */
    private List<MPDFileEntry> sortTracks(List<MPDFileEntry> tracks) {
        final List<MPDFileEntry> sorted = new ArrayList<>(tracks);
        final Collator collator = Collator.getInstance();
        collator.setStrength(Collator.PRIMARY); // ignore case and accents

        final Comparator<MPDFileEntry> byTitle = (a, b) -> collator.compare(((MPDTrack) a).getVisibleTitle(), ((MPDTrack) b).getVisibleTitle());
        final Comparator<MPDFileEntry> byAlbumOrder = (a, b) -> {
            final MPDTrack ta = (MPDTrack) a;
            final MPDTrack tb = (MPDTrack) b;
            int result = collator.compare(ta.getStringTag(MPDTrack.StringTagTypes.ALBUM), tb.getStringTag(MPDTrack.StringTagTypes.ALBUM));
            if (result == 0) {
                result = Integer.compare(ta.getDiscNumber(), tb.getDiscNumber());
            }
            if (result == 0) {
                result = Integer.compare(ta.getTrackNumber(), tb.getTrackNumber());
            }
            return result;
        };

        // Plain comparators with Collections.sort: the Java 8 List / Comparator helpers need API 24 (minSdk is 21)
        final Comparator<MPDFileEntry> comparator;
        switch (mSortOrder) {
            case ARTIST:
                comparator = (a, b) -> {
                    int result = collator.compare(artistOf((MPDTrack) a), artistOf((MPDTrack) b));
                    if (result == 0) {
                        result = byAlbumOrder.compare(a, b);
                    }
                    return result != 0 ? result : byTitle.compare(a, b);
                };
                break;
            case ALBUM:
                comparator = (a, b) -> {
                    final int result = byAlbumOrder.compare(a, b);
                    return result != 0 ? result : byTitle.compare(a, b);
                };
                break;
            case ADDED:
                // MPD reports the file's modification time, which matches when it was added for most libraries
                comparator = (a, b) -> {
                    final int result = Long.compare(b.getLastModifiedTime(), a.getLastModifiedTime());
                    return result != 0 ? result : byTitle.compare(a, b);
                };
                break;
            default:
                comparator = byTitle;
                break;
        }
        Collections.sort(sorted, comparator);
        return sorted;
    }

    private static String artistOf(MPDTrack track) {
        final String artist = track.getStringTag(MPDTrack.StringTagTypes.ARTIST);
        return artist.isEmpty() ? track.getStringTag(MPDTrack.StringTagTypes.ALBUMARTIST) : artist;
    }

    @Override
    public void onItemClick(int position) {
        // Clear the queue, play the tapped song, then continue with random songs
        final MPDFileEntry entry = mAdapter.getItem(position);
        if (entry instanceof MPDTrack) {
            MPDQueryHandler.playTrackList(buildRandomQueue(entry));
        }
    }

    private void shuffleAll() {
        if (mTracks.isEmpty()) {
            return;
        }
        MPDQueryHandler.playTrackList(buildRandomQueue(null));
    }

    /**
     * Builds a queue of up to {@link #SHUFFLE_ALL_TRACK_COUNT} random tracks from the library.
     *
     * @param first Track to put first (excluded from the random part), or null.
     */
    private List<MPDFileEntry> buildRandomQueue(@Nullable MPDFileEntry first) {
        final List<MPDFileEntry> shuffled = new ArrayList<>(mTracks);
        if (first != null) {
            shuffled.remove(first);
        }
        Collections.shuffle(shuffled);

        final List<MPDFileEntry> queue = new ArrayList<>();
        if (first != null) {
            queue.add(first);
        }
        for (MPDFileEntry track : shuffled) {
            if (queue.size() >= SHUFFLE_ALL_TRACK_COUNT) {
                break;
            }
            queue.add(track);
        }
        return queue;
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = requireActivity().getMenuInflater();
        inflater.inflate(R.menu.context_menu_track, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        MalpRecyclerView.RecyclerViewContextMenuInfo info = (MalpRecyclerView.RecyclerViewContextMenuInfo) item.getMenuInfo();
        if (info == null) {
            return super.onContextItemSelected(item);
        }
        final MPDFileEntry entry = mAdapter.getItem(info.position);
        final int itemId = item.getItemId();

        if (itemId == R.id.action_song_enqueue) {
            MPDQueryHandler.addPath(entry.getPath());
            return true;
        } else if (itemId == R.id.action_song_play) {
            MPDQueryHandler.playSong(entry.getPath());
            return true;
        } else if (itemId == R.id.action_song_play_next) {
            MPDQueryHandler.playSongNext(entry.getPath());
            return true;
        } else if (itemId == R.id.action_song_enqueue_at_start) {
            MPDQueryHandler.addPathAtStart(entry.getPath());
            return true;
        } else if (itemId == R.id.action_add_to_saved_playlist) {
            ChoosePlaylistDialog choosePlaylistDialog = ChoosePlaylistDialog.newInstance(true);
            choosePlaylistDialog.setCallback(new AddPathToPlaylist(entry, getActivity()));
            choosePlaylistDialog.show(requireActivity().getSupportFragmentManager(), "ChoosePlaylistDialog");
            return true;
        } else if (itemId == R.id.action_show_details) {
            SongDetailsDialog songDetailsDialog = SongDetailsDialog.createDialog((MPDTrack) entry, false);
            songDetailsDialog.show(requireActivity().getSupportFragmentManager(), "SongDetails");
            return true;
        }
        return super.onContextItemSelected(item);
    }
}
