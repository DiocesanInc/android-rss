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

package org.mcsoxford.rss;

import org.mcsoxford.rss.media.MediaContent;
import org.mcsoxford.rss.media.MediaGroup;
import org.mcsoxford.rss.media.MediaThumbnail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Data about an RSS item.
 *
 * @author Mr Horn
 */
public class RSSItem extends RSSBase {
    protected final List<MediaThumbnail> thumbnails;
    protected final List<MediaGroup> mediaGroups;
    protected final List<MediaContent> mediaContent;
    protected String content;
    protected RSSEnclosure enclosure;


    public RSSItem() {
        this((byte) 2, (byte) 3);
    }

    /* Internal constructor for RSSHandlerOld */
    RSSItem(byte categoryCapacity, byte thumbnailCapacity) {
        super(categoryCapacity);
        thumbnails = new ArrayList<MediaThumbnail>(thumbnailCapacity);
        mediaGroups = new ArrayList<MediaGroup>(thumbnailCapacity);
        mediaContent = new ArrayList<MediaContent>(thumbnailCapacity);
    }

    /* Internal method for RSSHandlerOld */
    void addThumbnail(MediaThumbnail thumbnail) {
        thumbnails.add(thumbnail);
    }

    /**
     * Returns an unmodifiable list of thumbnails. The return value is never
     * {@code null}. Images are in order of importance.
     */
    public List<MediaThumbnail> getThumbnails() {
        return Collections.unmodifiableList(thumbnails);
    }

    void addMediaGroup(MediaGroup group) {
        mediaGroups.add(group);
    }

    public List<MediaGroup> getMediaGroups() {
        return Collections.unmodifiableList(mediaGroups);
    }

    /**
     * Returns the value of the optional &lt;content:encoded&gt; tag
     *
     * @return string value of the element data
     */
    public String getContent() {
        return content;
    }

    /* Internal method for RSSHandlerOld */
    void setContent(String content) {
        this.content = content;
    }

    public RSSEnclosure getEnclosure() {
        return enclosure;
    }

    void setEnclosure(RSSEnclosure enclosure) {
        this.enclosure = enclosure;
    }

    public void addMediaContent(MediaContent item) {
        mediaContent.add(item);
    }

    public List<MediaContent> getMediaContent() {
        return mediaContent;
    }

}
