/*
 * Copyright (C) 2010 A. Horn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mcsoxford.rss.media;

import android.net.Uri;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MediaContent {
    protected Uri url;
    protected int fileSize;
    protected String type;
    protected String medium;
    protected boolean isDefault;
    protected String expression;
    protected float bitrate;
    protected float framerate;
    protected float samplingrate;
    protected int channels;
    protected int duration;
    protected int height;
    protected int width;
    protected String lang;

    protected MediaPlayer mediaPlayer;
    protected List<MediaThumbnail> thumbnails;

    public MediaContent(Uri url, int fileSize, String type, String medium,
        boolean isDefault, String expression, float bitrate, float framerate,
        float samplingrate, int channels, int duration, int height, int width,
        String lang) {

        this.url = url;
        this.fileSize = fileSize;
        this.type = type;
        this.medium = medium;
        this.isDefault = isDefault;
        this.expression = expression;
        this.bitrate = bitrate;
        this.framerate = framerate;
        this.samplingrate = samplingrate;
        this.channels = channels;
        this.duration = duration;
        this.height = height;
        this.width = width;
        this.lang = lang;

        thumbnails = new ArrayList<MediaThumbnail>();
    }

    public Uri geturl() {
        return url;
    }

    public int getFileSize() {
        return fileSize;
    }

    public String getType() {
        return type;
    }

    public String getMedium() {
        return medium;
    }

    public boolean getIsDefault() {
        return isDefault;
    }

    public String getExpression() {
        return expression;
    }

    public float getBitrate() {
        return bitrate;
    }

    public float getFramerate() {
        return framerate;
    }

    public float getSamplingrate() {
        return samplingrate;
    }

    public int getChannels() {
        return channels;
    }

    public int getDuration() {
        return duration;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public String getLang() {
        return lang;
    }

    public void setMediaPlayer(MediaPlayer player) {
        mediaPlayer = player;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void addThumbnail(MediaThumbnail thumbnail) {
        thumbnails.add(thumbnail);
    }

    /**
     * Returns an unmodifiable list of thumbnails. The return value is never
     * {@code null}. Images are in order of importance.
     */
    public List<MediaThumbnail> getThumbnails() {
        return Collections.unmodifiableList(thumbnails);
    }
}
