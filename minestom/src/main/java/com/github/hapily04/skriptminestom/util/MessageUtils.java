package com.github.hapily04.skriptminestom.util;

import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

public class MessageUtils {

	public static final MiniMessage BASIC_MINI_MESSAGE = MiniMessage.builder()
																	.postProcessor(component -> component.compact().decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE))
																	.tags(TagResolver.standard())
																	.build();

    public static final MiniMessage SKRIPT_MINI_MESSAGE = MiniMessage.builder()
																	 .postProcessor(component -> component.compact().decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE))
																	 .tags(TagResolver.resolver(
																		 TagResolver.standard(),
																		 TagResolver.resolver("error_color", Tag.styling(TextColor.color(0xFF7E66))),
																		 TagResolver.resolver("success_color", Tag.styling(TextColor.color(0x66FF96))),
																		 TagResolver.resolver("base_grey", Tag.styling(TextColor.color(0xCCC4C4))),
																		 Placeholder.parsed("skript_minestom_tag", "<dark_gray>[</dark_gray><gradient:#ff6c2f:#ff76b6>Skript-Minestom</gradient><dark_gray>]</dark_gray>")
																	 )).build();
            /*.tags(TagResolver.resolver(
                    TagResolver.standard(),

                    TagResolver.resolver("primary", Tag.styling(TextColor.color(0x0B96E6))),
                    TagResolver.resolver("primary_lighter", Tag.styling(TextColor.color(0x00A2FF))),
                    TagResolver.resolver("money_color", Tag.styling(TextColor.color(0x00DE30))),
                    TagResolver.resolver("tokens_color", Tag.styling(TextColor.color(0xFFCE2B))),
                    TagResolver.resolver("gems_color", Tag.styling(TextColor.color(0xC40A6A))),
                    TagResolver.resolver("credits_color", Tag.styling(NamedTextColor.AQUA)),

                    Placeholder.parsed("server_tag", "<primary><b>TowerTycoon</b></primary>"),
                    Placeholder.parsed("money_tag", "<money_color>\uD83D\uDCB0</money_color>"),
                    Placeholder.parsed("tokens_tag", "<tokens_color>⛃</tokens_color>"),
                    Placeholder.parsed("gems_tag", "<gems_color>\uD83D\uDC8E</gems_color>"),
                    Placeholder.parsed("credits_tag", "<credits_color>❂</credits_color>"),

                    Placeholder.parsed("pipe", "<b>⏐</b>"),
                    Placeholder.parsed("pipe_obf", "<base_grey><obf>|</obf></grey>")
            ))*/

}
