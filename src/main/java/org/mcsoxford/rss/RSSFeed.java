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

import org.mcsoxford.rss.media.MediaThumbnail;

import java.util.*;

/**
 * Data about an RSS feed and its RSS items.
 *
 * @author Mr Horn
 */
public class RSSFeed extends RSSBase {

    private final List<RSSItem> items;
    private Date lastBuildDate;
    private Integer ttl;
    private List<MediaThumbnail> thumbnails;

    RSSFeed() {
        super(/* initial capacity for category names */ (byte) 3);
        items = new LinkedList<RSSItem>();
        thumbnails = new ArrayList<MediaThumbnail>();
    }

    /**
     * Returns an unmodifiable list of RSS items.
     */
    public List<RSSItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    void addItem(RSSItem item) {
        items.add(item);
    }

    void setLastBuildDate(Date date) {
        lastBuildDate = date;
    }

    public Date getLastBuildDate() {
        return lastBuildDate;
    }

    void setTTL(Integer value) {
        ttl = value;
    }

    public Integer getTTL() {
        return ttl;
    }

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
}

