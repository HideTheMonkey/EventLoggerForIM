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
package com.hidethemonkey.elfim.messaging;

import com.slack.api.model.block.*;
import com.slack.api.model.block.composition.BlockCompositions;
import com.slack.api.model.block.composition.MarkdownTextObject;
import com.slack.api.model.block.composition.TextObject;
import com.slack.api.model.block.element.ImageElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockBuilder {
  /**
   * @param text
   * @return
   */
  public static HeaderBlock getHeader(String text) {
    return HeaderBlock.builder().text(BlockCompositions.plainText(text)).build();
  }

  /**
   * @param text
   * @return
   */
  public static MarkdownTextObject getMarkdown(String text) {
    return MarkdownTextObject.builder().text(text).build();
  }

  /**
   * @param url
   * @param alt
   * @return
   */
  public static ImageElement getImageElement(String url, String alt) {
    return ImageElement.builder().imageUrl(url).altText(alt).build();
  }

  /**
   * @param elements
   * @return
   */
  public static ContextBlock getContextBlock(ContextBlockElement... elements) {
    return ContextBlock.builder().elements(Arrays.asList(elements)).build();
  }

  /**
   *
   * @param elements
   * @return
   */
  public static ContextBlock getContextBlock(List elements) {
    return ContextBlock.builder().elements(elements).build();
  }

  /**
   * @param text
   * @param image
   * @return
   */
  public static SectionBlock getSection(String text, ImageElement image) {
    return SectionBlock.builder()
        .text(BlockCompositions.markdownText(text))
        .accessory(image)
        .build();
  }

  /**
   * @param image
   * @param text
   * @return
   */
  public static SectionBlock getSectionWithFields(ImageElement image, TextObject... text) {
    return SectionBlock.builder().fields(Arrays.asList(text)).accessory(image).build();
  }

  /**
   * @param headerText
   * @return
   */
  public static List<LayoutBlock> getListBlocksWithHeader(String headerText) {
    List<LayoutBlock> blocks = new ArrayList<>();
    blocks.add(BlockBuilder.getHeader(headerText));
    return blocks;
  }
}
