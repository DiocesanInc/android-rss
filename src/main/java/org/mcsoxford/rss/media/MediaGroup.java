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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MediaGroup {
    protected List<MediaContent> content;
    protected List<MediaThumbnail> thumbnails;
    protected String description;

    public MediaGroup() {
        content = new ArrayList<MediaContent>();
        thumbnails = new ArrayList<MediaThumbnail>();
    }

    public void addMediaContent(MediaContent item) {
        content.add(item);
    }

    public List<MediaContent> getMediaContent() {
        return content;
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

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
