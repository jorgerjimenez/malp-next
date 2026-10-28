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

package org.gateshipone.malp.application.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import org.gateshipone.malp.mpdservice.handlers.responsehandler.MPDResponseFileList;
import org.gateshipone.malp.mpdservice.handlers.serverhandler.MPDQueryHandler;
import org.gateshipone.malp.mpdservice.mpdprotocol.mpdobjects.MPDFileEntry;
import org.gateshipone.malp.mpdservice.mpdprotocol.mpdobjects.MPDTrack;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads every track in the MPD database (directories and playlists are dropped).
 */
public class AllTracksViewModel extends GenericViewModel<MPDFileEntry> {

    private final MPDResponseFileList mTrackResponseHandler;

    private AllTracksViewModel(@NonNull final Application application) {
        super(application);
        mTrackResponseHandler = new TrackResponseHandler(this);
    }

    @Override
    void loadData() {
        MPDQueryHandler.getAllLibraryTracks(mTrackResponseHandler);
    }

    private static class TrackResponseHandler extends MPDResponseFileList {
        private final WeakReference<AllTracksViewModel> mViewModel;

        private TrackResponseHandler(final AllTracksViewModel viewModel) {
            mViewModel = new WeakReference<>(viewModel);
        }

        @Override
        public void handleTracks(final List<MPDFileEntry> fileList, final int start, final int end) {
            final AllTracksViewModel model = mViewModel.get();
            if (model == null) {
                return;
            }
            final List<MPDFileEntry> tracks = new ArrayList<>();
            if (fileList != null) {
                for (MPDFileEntry entry : fileList) {
                    if (entry instanceof MPDTrack) {
                        tracks.add(entry);
                    }
                }
            }
            model.setData(tracks);
        }
    }

    public static class AllTracksModelFactory implements ViewModelProvider.Factory {
        private final Application mApplication;

        public AllTracksModelFactory(final Application application) {
            mApplication = application;
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new AllTracksViewModel(mApplication);
        }
    }
}
