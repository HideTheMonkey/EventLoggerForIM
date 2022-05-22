/*
 * MIT License
 *
 * Copyright (c) 2022 HideTheMonkey
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.hidethemonkey.elfim.messaging.json;

import java.util.ArrayList;
import java.util.List;

public class Embed {

  private int color;
  private Author author;
  private String title;
  private String url;
  private String description;
  private List<Field> fields;
  private Image thumbnail;
  private Image image;
  private Footer footer;

  public Embed(int color) {
    this.color = color;
  }

  public void setColor(int color) {
    this.color = color;
  }

  public void addAuthor(String name, String icon_url) {
    this.author = new Author(name, icon_url);
  }

  public void addAuthor(String name, String icon_url, String url) {
    this.author = new Author(name, icon_url, url);
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void addField(String name, String value) {
    if (this.fields == null) {
      this.fields = new ArrayList<>();
    }
    this.fields.add(new Field(name, value));
  }

  public void setThumbnail(String url) {
    this.thumbnail = new Image(url);
  }

  public void setImage(String url) {
    this.image = new Image(url);
  }

  public void setFooter(String text, String icon_url) {
    this.footer = new Footer(text, icon_url);
  }

  /////////////////////////////////////////////////
  // Inner Object Classes
  /////////////////////////////////////////////////
  public class Author {
    private String icon_url;
    private String name;
    private String url;

    public Author(String name, String icon_url) {
      this.icon_url = icon_url;
      this.name = name;
    }

    public Author(String name, String icon_url, String url) {
      this.icon_url = icon_url;
      this.name = name;
      this.url = url;
    }
  }

  public class Image {
    private String url;

    public Image(String url) {
      this.url = url;
    }
  }

  public class Footer {
    private String text;
    private String icon_url;

    public Footer(String text, String icon_url) {
      this.text = text;
      this.icon_url = icon_url;
    }
  }

  public class Field {
    private String name;
    private String value;
    private boolean inline = true;

    public Field(String name, String value) {
      this.name = name;
      this.value = value;
    }

    public void setInline(boolean inline) {
      this.inline = inline;
    }
  }
}
