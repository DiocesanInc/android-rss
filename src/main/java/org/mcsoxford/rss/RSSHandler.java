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

import android.net.Uri;
import org.mcsoxford.rss.media.MediaContent;
import org.mcsoxford.rss.media.MediaGroup;
import org.mcsoxford.rss.media.MediaPlayer;
import org.mcsoxford.rss.media.MediaThumbnail;
import org.mcsoxford.rss.util.AttributeParser;
import org.mcsoxford.rss.util.Dates;
import org.mcsoxford.rss.util.Integers;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class RSSHandler extends DefaultHandler {

    private static final String RSS_ITEM = "item";
    private static final String RSS_PUB_DATE = "pubDate";
    private static final String RSS_LAST_BUILD_DATE = "lastBuildDate";
    private static final String RSS_TTL = "ttl";
    private static final String RSS_TITLE = "title";
    private static final String RSS_CATEGORY = "category";
    private static final String RSS_ENCLOSURE = "enclosure";
    private static final String RSS_DESCRIPTION = "description";
    private static final String RSS_CONTENT = "content:encoded";
    private static final String RSS_LINK = "link";
    private static final String RSS_MEDIA_THUMBNAIL = "media:thumbnail";
    private static final String RSS_MEDIA_GROUP = "media:group";
    private static final String RSS_MEDIA_CONTENT = "media:content";
    private static final String RSS_MEDIA_PLAYER = "media:player";
    private static final String RSS_MEDIA_DESCRIPTION = "media:description";

    private static final String ATOM_FEED = "feed";
    private static final String ATOM_ENTRY = "entry";
    private static final String ATOM_SUBTITLE = "subtitle";
    private static final String ATOM_PUBLISHED = "published";
    private static final String ATOM_UPDATED = "udpated";
    private static final String ATOM_SUMAMRY = "summary";
    private static final String ATOM_CONTENT = "summary";

    private static final String MEDIA_CONTENT_URL = "url";
    private static final String MEDIA_CONTENT_FILE_SIZE = "fileSize";
    private static final String MEDIA_CONTENT_TYPE = "type";
    private static final String MEDIA_CONTENT_MEDIUM = "medium";
    private static final String MEDIA_CONTENT_IS_DEFAULT = "isDefault";
    private static final String MEDIA_CONTENT_EXPRESSION = "expression";
    private static final String MEDIA_CONTENT_BITRATE = "bitrate";
    private static final String MEDIA_CONTENT_FRAMERATE = "framerate";
    private static final String MEDIA_CONTENT_SAMPLINGRATE = "samplingrate";
    private static final String MEDIA_CONTENT_CHANNELS = "channels";
    private static final String MEDIA_CONTENT_DURATION = "duration";
    private static final String MEDIA_CONTENT_HEIGHT = "height";
    private static final String MEDIA_CONTENT_WIDTH = "width";
    private static final String MEDIA_CONTENT_LANG = "lang";

    /**
     * Constant symbol table to ensure efficient treatment of handler states.
     */
    private final Map<String, HandlerBase> handlers;
    private HandlerBase handler;

    /**
     * Reference is never {@code null}. Visibility must be package-private to
     * ensure efficiency of inner classes.
     */
    final RSSFeed feed = new RSSFeed();

    /**
     * If not {@code null}, then buffer the characters inside an XML text element.
     */
    private StringBuilder buffer;

    private RSSItem item;
    private MediaGroup mediaGroup;
    private MediaContent mediaContent;

    private interface HandlerBase { }

    private interface ElementAttributesHandler extends HandlerBase {
        void start(Attributes attributes);
        void end();
    }

    private interface ElementContentHandler extends HandlerBase {
        void content(String content);
    }

    private abstract class ElementHandler implements ElementAttributesHandler, ElementContentHandler { }

    public RSSHandler() {
        handlers = new HashMap<String, HandlerBase>();
        handlers.put(RSS_ITEM, itemHandler);
        handlers.put(ATOM_ENTRY, itemHandler);
        handlers.put(RSS_PUB_DATE, pubDateHandler);
        handlers.put(ATOM_PUBLISHED, atomPublishDateHandler);
        handlers.put(RSS_LAST_BUILD_DATE, lastBuildDateHandler);
        handlers.put(ATOM_UPDATED, atomUpdatedDateHandler);
        handlers.put(RSS_CATEGORY, categoryHandler);
        handlers.put(RSS_TTL, ttlHandler);
        handlers.put(RSS_TITLE, titleHandler);
        handlers.put(RSS_ENCLOSURE, enclosureHandler);
        handlers.put(RSS_DESCRIPTION, descriptionHandler);
        handlers.put(RSS_CONTENT, contentHandler);
        handlers.put(RSS_LINK, linkHandler);
        handlers.put(RSS_MEDIA_GROUP, mediaGroupHandler);
        handlers.put(RSS_MEDIA_CONTENT, mediaContentHandler);
        handlers.put(RSS_MEDIA_THUMBNAIL, mediaThumbnailHandler);
        handlers.put(RSS_MEDIA_PLAYER, mediaPlayerHandler);
        handlers.put(RSS_MEDIA_DESCRIPTION, mediaDescriptionHandler);
    }

    @Override
    public void startElement(String nsURI, String localName, String qname, Attributes attributes) {
        handler = handlers.get(qname);
        if (handler instanceof ElementAttributesHandler) {
            ((ElementAttributesHandler) handler).start(attributes);
        }
        if (handler instanceof ElementContentHandler) {
            buffer = new StringBuilder();
        }
    }

    @Override
    public void endElement(String nsURI, String localName, String qname) {
        handler = handlers.get(qname);
        if (isBuffering()) {
            ((ElementContentHandler) handler).content(buffer.toString());
        }
        buffer = null;

        if (handler instanceof ElementAttributesHandler) {
            ((ElementAttributesHandler) handler).end();
        }
    }

    @Override
    public void characters(char ch[], int start, int length) {
        if (isBuffering()) {
            buffer.append(ch, start, length);
        }
    }

    /**
     * Determines if the SAX parser is ready to receive data inside an XML element
     * such as &lt;title&gt; or &lt;description&gt;.
     *
     * @return boolean {@code true} if the SAX handler parses data inside an XML
     * element, {@code false} otherwise
     */
    boolean isBuffering() {
        return buffer != null && handler instanceof ElementContentHandler;
    }

    RSSFeed feed() {
        return feed;
    }

    private final ElementAttributesHandler itemHandler = new ElementAttributesHandler() {
        @Override
        public void start(Attributes attributes) {
            item = new RSSItem();
        }

        @Override
        public void end() {
            feed.addItem(item);
        }
    };

    private final ElementContentHandler pubDateHandler = new ElementContentHandler() {

        @Override
        public void content(String content) {
            final Date date = Dates.parseRfc822(content);
            if (item != null) {
                item.setPubDate(date);
            } else {
                feed.setPubDate(date);
            }
        }
    };

    private final ElementContentHandler atomPublishDateHandler = new ElementContentHandler() {

        @Override
        public void content(String content) {
            final Date date = Dates.parseIso8601(content);
            if (item != null) {
                item.setPubDate(date);
            } else {
                feed.setPubDate(date);
            }
        }
    };

    private final ElementContentHandler categoryHandler = new ElementContentHandler() {
        @Override
        public void content(String content) {
            if (item == null) {
                feed.addCategory(content);
            } else {
                item.addCategory(content);
            }
        }
    };

    private final ElementContentHandler ttlHandler = new ElementContentHandler() {
        @Override
        public void content(String content) {
            final Integer value = Integers.parseInteger(content);
            if (item == null) {
                feed.setTTL(value);
            }
        }
    };

    private final ElementContentHandler atomUpdatedDateHandler = new ElementContentHandler() {
        @Override
        public void content(String content) {
            final Date date = Dates.parseIso8601(content);
            if (item == null) {
                feed.setLastBuildDate(date);
            }
        }
    };

    private final ElementContentHandler lastBuildDateHandler = new ElementContentHandler() {
        @Override
        public void content(String content) {
            final Date date = Dates.parseRfc822(content);
            if (item == null) {
                feed.setLastBuildDate(date);
            }
        }
    };

    private final ElementContentHandler titleHandler = new ElementContentHandler() {
        @Override
        public void content(String content) {
            if (item == null) {
                feed.setTitle(content);
            } else {
                item.setTitle(content);
            }
        }
    };

    private final ElementContentHandler descriptionHandler = new ElementContentHandler() {
        @Override
        public void content(String content) {
            if (item == null) {
                feed.setDescription(content);
            } else {
                item.setDescription(content);
            }
        }
    };

    private final ElementAttributesHandler enclosureHandler = new ElementAttributesHandler() {
        @Override
        public void start(Attributes attributes) {
            if (item != null) {
                final String url = AttributeParser.stringValue(attributes, "url");
                final Integer length = AttributeParser.intValue(attributes, "length");
                final String mimeType = AttributeParser.stringValue(attributes, "type");

                if (url == null || length == null || mimeType == null) {
                    // Ignore invalid elements.
                    return;
                }

                RSSEnclosure enclosure = new RSSEnclosure(
                    Uri.parse(url), length, mimeType
                );
                item.setEnclosure(enclosure);
            }
        }

        @Override
        public void end() { }
    };

    private final ElementContentHandler contentHandler = new ElementContentHandler() {
        @Override
        public void content(String content) {
            if (item != null) {
                item.setContent(content);
            }
        }
    };

    private final ElementHandler linkHandler = new ElementHandler() {
        @Override
        public void start(Attributes attributes) {
            String linkString = AttributeParser.stringValue(attributes, "href");
            if (linkString == null) {
                return;
            }

            final Uri uri = Uri.parse(linkString);
            if (item == null) {
                feed.setLink(uri);
            } else {
                item.setLink(uri);
            }
        }

        @Override
         public void content(String content) {
            if (content.length() < 1) {
                return;
            }

            final Uri uri = Uri.parse(content);
            if (item == null) {
                feed.setLink(uri);
            } else {
                item.setLink(uri);
            }
        }

        @Override
        public void end() { }
    };

    private final ElementAttributesHandler mediaThumbnailHandler = new ElementAttributesHandler() {
        private static final int DEFAULT_VALUE = -1;

        @Override
        public void start(Attributes attributes) {
            Uri url = Uri.parse(AttributeParser.stringValue(attributes, "url"));
            // URL attribute is required
            if (url == null) {
                return;
            }

            MediaThumbnail thumbnail = new MediaThumbnail(
                url,
                AttributeParser.intValue(attributes, "height", DEFAULT_VALUE),
                AttributeParser.intValue(attributes, "width", DEFAULT_VALUE),
                AttributeParser.stringValue(attributes, "time")
            );

            if (mediaContent != null) {
                mediaContent.addThumbnail(thumbnail);
            } else if (mediaGroup != null) {
                mediaGroup.addThumbnail(thumbnail);
            } else if (item != null) {
                item.addThumbnail(thumbnail);
            } else if (feed != null) {
                feed.addThumbnail(thumbnail);
            }
        }

        @Override
        public void end() { }
    };

    private final ElementAttributesHandler mediaGroupHandler = new ElementAttributesHandler() {
        @Override
        public void start(Attributes attributes) {
            if (item != null) {
                mediaGroup = new MediaGroup();
            }
        }

        @Override
        public void end() {
            if (item != null) {
                item.addMediaGroup(mediaGroup);
            }
            mediaGroup = null;
        }
    };

    private final ElementAttributesHandler mediaContentHandler = new ElementAttributesHandler() {
        private static final int DEFAULT_VALUE = -1;

        @Override
        public void start(Attributes attributes) {
            if (item != null) {
                String urlString = AttributeParser.stringValue(attributes, MEDIA_CONTENT_URL);
                mediaContent = new MediaContent(
                    urlString != null ? Uri.parse(urlString) : null,
                    AttributeParser.intValue(attributes, MEDIA_CONTENT_FILE_SIZE, DEFAULT_VALUE),
                    AttributeParser.stringValue(attributes, MEDIA_CONTENT_TYPE),
                    AttributeParser.stringValue(attributes, MEDIA_CONTENT_MEDIUM),
                    AttributeParser.booleanValue(attributes, MEDIA_CONTENT_IS_DEFAULT, false),
                    AttributeParser.stringValue(attributes, MEDIA_CONTENT_EXPRESSION),
                    AttributeParser.intValue(attributes, MEDIA_CONTENT_BITRATE, DEFAULT_VALUE),
                    AttributeParser.intValue(attributes, MEDIA_CONTENT_FRAMERATE, DEFAULT_VALUE),
                    AttributeParser.intValue(attributes, MEDIA_CONTENT_SAMPLINGRATE, DEFAULT_VALUE),
                    AttributeParser.intValue(attributes, MEDIA_CONTENT_CHANNELS, DEFAULT_VALUE),
                    AttributeParser.intValue(attributes, MEDIA_CONTENT_DURATION, DEFAULT_VALUE),
                    AttributeParser.intValue(attributes, MEDIA_CONTENT_HEIGHT, DEFAULT_VALUE),
                    AttributeParser.intValue(attributes, MEDIA_CONTENT_WIDTH, DEFAULT_VALUE),
                    AttributeParser.stringValue(attributes, MEDIA_CONTENT_LANG)
                );
            }
        }

        @Override
        public void end() {
            if (item != null) {
                if (mediaGroup != null) {
                    mediaGroup.addMediaContent(mediaContent);
                } else {
                    item.addMediaContent(mediaContent);
                }
            }
            mediaContent = null;
        }
    };

    private final ElementAttributesHandler mediaPlayerHandler = new ElementAttributesHandler() {
        private static final int DEFAULT_VALUE = -1;

        @Override
        public void start(Attributes attributes) {
            if (mediaContent != null) {
                mediaContent.setMediaPlayer(new MediaPlayer(
                    Uri.parse(AttributeParser.stringValue(attributes, "url")),
                    AttributeParser.intValue(attributes, "width", DEFAULT_VALUE),
                    AttributeParser.intValue(attributes, "height", DEFAULT_VALUE)
                ));
            }
        }

        @Override
        public void end() { }
    };

    private final ElementContentHandler mediaDescriptionHandler = new ElementContentHandler() {
        @Override
        public void content(String content) {
            if (mediaContent != null) {
                mediaContent.setDescription(content);
            } else if (mediaGroup != null) {
                mediaGroup.setDescription(content);
            } else if (item != null) {
                item.setDescription(content);
            }
        }
    };
}
