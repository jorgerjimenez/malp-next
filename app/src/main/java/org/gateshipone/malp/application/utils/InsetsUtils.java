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

package org.gateshipone.malp.application.utils;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Helpers for edge-to-edge layouts (enforced when targeting API 35+).
 */
public final class InsetsUtils {

    private InsetsUtils() {
    }

    /**
     * Pads the activity's content view by the system bar / cutout insets that were not already
     * consumed by a parent (e.g. a window decor action bar). Original padding is preserved.
     */
    public static void applySystemBarPadding(Activity activity) {
        final ViewGroup content = activity.findViewById(android.R.id.content);
        if (content == null || content.getChildCount() == 0) {
            return;
        }
        final View root = content.getChildAt(0);
        final int startLeft = root.getPaddingLeft();
        final int startTop = root.getPaddingTop();
        final int startRight = root.getPaddingRight();
        final int startBottom = root.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            final Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
            v.setPadding(startLeft + bars.left, startTop + bars.top, startRight + bars.right, startBottom + bars.bottom);
            if (v instanceof ViewGroup) {
                ((ViewGroup) v).setClipToPadding(false);
            }
            return WindowInsetsCompat.CONSUMED;
        });
        ViewCompat.requestApplyInsets(root);
    }
}
