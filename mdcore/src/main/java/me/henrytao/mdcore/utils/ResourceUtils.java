/*
 * Copyright 2015 "Henry Tao <hi@henrytao.me>"
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.henrytao.mdcore.utils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.util.StateSet;
import android.util.TypedValue;
import android.util.Xml;
import android.view.Window;
import android.view.WindowManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.henrytao.mdcore.R;

/**
 * Created by henrytao on 10/10/15.
 */
public class ResourceUtils {

  public static ColorStateList createColorStateListFromResId(Context context, int resId) throws IOException, XmlPullParserException {
    XmlResourceParser parser = context.getResources().getXml(resId);
    AttributeSet attrs = Xml.asAttributeSet(parser);
    Resources r = context.getResources();

    final int innerDepth = parser.getDepth() + 2;
    int depth;
    int type;

    List<int[]> customStateList = new ArrayList<>();
    List<Integer> customColorList = new ArrayList<>();

    while ((type = parser.next()) != XmlPullParser.END_DOCUMENT
        && ((depth = parser.getDepth()) >= innerDepth || type != XmlPullParser.END_TAG)) {
      if (type != XmlPullParser.START_TAG || depth > innerDepth
          || !parser.getName().equals("item")) {
        continue;
      }

      int color = 0;
      //TypedValue typedValue = new TypedValue();
      //context.getTheme().resolveAttribute(android.R.attr.textAppearanceLarge, typedValue, true);
      //TypedArray a = context.getTheme().obtainStyledAttributes( new int[]{android.R.attr.color});
      //int color = a.getColor(0, 0);
      //a.recycle();

      // Parse all unrecognized attributes as state specifiers.
      int j = 0;
      final int numAttrs = attrs.getAttributeCount();
      int[] stateSpec = new int[numAttrs];
      for (int i = 0; i < numAttrs; i++) {
        final int stateResId = attrs.getAttributeNameResource(i);
        switch (stateResId) {
          case android.R.attr.color:
            int colorAttrId = attrs.getAttributeResourceValue(i, 0);
            String colorAttrValue = attrs.getAttributeValue(i);
            if (colorAttrId == 0) {
              colorAttrId = Integer.valueOf(colorAttrValue.replace("?", ""));
            }
            //TypedArray a = context.obtainStyledAttributes(colorAttrId, new int[] {android.R.attr.color});
            //color = a.getColor(0, 0);
            //a.recycle();
            color = getColorFromAttribute(context, colorAttrId);

            //color = r.getColor(attrs.getAttributeResourceValue(i, 0));
            //color = getColorFromAttribute(context, attrs.getAttributeResourceValue(i, 0));
            break;
          case android.R.attr.alpha:
            // Recognized attribute, ignore.
            break;
          default:
            stateSpec[j++] = attrs.getAttributeBooleanValue(i, false) ? stateResId : -stateResId;
        }
      }
      stateSpec = StateSet.trimStateSet(stateSpec, j);

      customColorList.add(color);
      customStateList.add(stateSpec);
    }

    int[] colors = new int[customColorList.size()];
    int[][] states = new int[customStateList.size()][];
    int i = 0;
    for (int n = states.length; i < n; i++) {
      colors[i] = customColorList.get(i);
      states[i] = customStateList.get(i);
    }
    return new ColorStateList(states, colors);
  }

  public static void enableTranslucentStatus(Activity activity) {
    Window window = activity.getWindow();
    window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
      window.setStatusBarColor(Color.TRANSPARENT);
    }
  }

  public static int getActionBarSize(Context context) {
    TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{android.R.attr.actionBarSize});
    int size = (int) a.getDimension(0, 0);
    a.recycle();
    return size;
  }

  public static int getColorFromAttribute(Context context, int attrId) {
    if (attrId == 0) {
      return 0;
    }
    TypedValue a = new TypedValue();
    context.getTheme().resolveAttribute(attrId, a, true);
    return context.getResources().getColor(a.resourceId);
  }

  public static float getFloatFromAttribute(Context context, int attrId) {
    if (attrId == 0) {
      return 0;
    }
    TypedValue a = new TypedValue();
    context.getTheme().resolveAttribute(attrId, a, true);
    return a.getFloat();
  }

  public static int getStatusBarSize(Context context) {
    int statusBarSize = 0;
    if (context != null) {
      int id = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
      if (id > 0) {
        statusBarSize = context.getResources().getDimensionPixelSize(id);
      }
    }
    return statusBarSize;
  }
}
