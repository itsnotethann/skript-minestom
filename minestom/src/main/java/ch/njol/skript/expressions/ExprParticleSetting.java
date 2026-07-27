package ch.njol.skript.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import net.minestom.server.entity.Player;
import net.minestom.server.network.player.ClientSettings;
import org.jspecify.annotations.Nullable;


@Name("Particle Setting")
@Description("A player's particle display setting.")
@Examples("send particle setting of player to player")
public class ExprParticleSetting extends SimplePropertyExpression<Player, ClientSettings.ParticleSetting> {

	static {
		register(ExprParticleSetting.class, ClientSettings.ParticleSetting.class,
			"particle setting", "players");
	}

	@Override
	public ClientSettings.@Nullable ParticleSetting convert(Player from) {
		return from.getSettings().particleSetting();
	}

	@Override
	protected String getPropertyName() {
		return "particle setting";
	}

	@Override
	public Class<? extends ClientSettings.ParticleSetting> getReturnType() {
		return ClientSettings.ParticleSetting.class;
	}

}
