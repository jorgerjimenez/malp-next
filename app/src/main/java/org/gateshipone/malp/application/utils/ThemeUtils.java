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
import android.util.TypedValue;

public class ThemeUtils {

    /**
     * returns the color for the given theme attribute
     *
     * @param context        the current context to resolve the attribute id
     * @param attributeColor the requested theme attribute id
     * @return the requested color as an integer
     */
    public static int getThemeColor(final Context context, final int attributeColor) {
        final TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(attributeColor, value, true);
        return value.data;
    }

    /**
     * returns the resource id for the given theme attribute
     *
     * @param context     the current context to resolve the attribute id
     * @param attributeId the requested theme attribute id
     * @return the resolved resource id as an integer
     */
    public static int getThemeResourceId(final Context context, final int attributeId) {
        final TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(attributeId, value, true);
        return value.resourceId;
    }

    /**
     * Returns the selected theme mode (system / light / dark / oled). Migrates the old
     * colour theme settings once, so existing users keep light / dark / OLED.
     */
    public static String getThemeMode(android.content.Context context, android.content.SharedPreferences prefs) {
        final String key = context.getString(org.gateshipone.malp.R.string.pref_app_theme_key);
        if (prefs.contains(key)) {
            return prefs.getString(key, context.getString(org.gateshipone.malp.R.string.pref_app_theme_default));
        }

        String mode = context.getString(org.gateshipone.malp.R.string.pref_app_theme_system_key);
        final boolean legacy = prefs.getBoolean(context.getString(org.gateshipone.malp.R.string.pref_legacy_theme_key), false);
        final String legacyColor = prefs.getString(context.getString(org.gateshipone.malp.R.string.pref_legacy_theme_selector_key), "");
        final String youColor = prefs.getString(context.getString(org.gateshipone.malp.R.string.pref_materialyou_theme_selector_key), "");
        final String darkKey = context.getString(org.gateshipone.malp.R.string.pref_dark_theme_key);

        if (legacy && legacyColor.equals(context.getString(org.gateshipone.malp.R.string.pref_oleddark_key))) {
            mode = context.getString(org.gateshipone.malp.R.string.pref_app_theme_oled_key);
        } else if (prefs.contains(darkKey) && !youColor.equals(context.getString(org.gateshipone.malp.R.string.pref_materialyou_auto_key))) {
            mode = context.getString(prefs.getBoolean(darkKey, true) ? org.gateshipone.malp.R.string.pref_app_theme_dark_key : org.gateshipone.malp.R.string.pref_app_theme_light_key);
        }
        prefs.edit().putString(key, mode).apply();
        return mode;
    }
}
