package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.SyntaxStringBuilder;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.minestom.server.sound.Music;
import net.minestom.server.world.attribute.BackgroundMusic;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class ExprBackgroundMusic extends SimpleExpression<BackgroundMusic> {

	static {
		Skript.registerExpression(ExprBackgroundMusic.class, BackgroundMusic.class, ExpressionType.COMBINED,
			"[new] background music [with [music] %-music%] [(,|[and] with) creative [music] %-music%] [(,|[and] with) underwater [music] %-music%]");
	}

	@Nullable
	private Expression<Music> music;
	@Nullable
	private Expression<Music> creative;
	@Nullable
	private Expression<Music> underwater;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		music = (Expression<Music>) expressions[0];
		creative = (Expression<Music>) expressions[1];
		underwater = (Expression<Music>) expressions[2];
		return true;
	}

	@Override
	protected BackgroundMusic[] get(Event event) {
		Music music = null;
		if (this.music != null) music = this.music.getSingle(event);
		Music creative = null;
		if (this.creative != null) creative = this.creative.getSingle(event);
		Music underwater = null;
		if (this.underwater != null) underwater = this.underwater.getSingle(event);
		return new BackgroundMusic[]{new BackgroundMusic(music, creative, underwater)};
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends BackgroundMusic> getReturnType() {
		return BackgroundMusic.class;
	}

	@Override
	public String toString(@org.eclipse.jdt.annotation.Nullable Event event, boolean debug) {
		SyntaxStringBuilder sb = new SyntaxStringBuilder(event, debug);
		sb.append("background music");
		if (music != null) sb.append("with music", music);
		if (creative != null) sb.append("with creative music", creative);
		if (underwater != null) sb.append("with underwater music", underwater);
		return sb.toString();
	}

}
